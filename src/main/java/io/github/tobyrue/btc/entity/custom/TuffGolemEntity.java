package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.CopperGolemWanderGoal;
import io.github.tobyrue.btc.entity.ai.TuffWanderAroundGoal;
import io.github.tobyrue.btc.entity.ai.TuffWanderGoal;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.ArmorDyeRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TuffGolemEntity extends GolemEntity {
    private static final double SPEED = 0.4D;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState sleepAnimationState = new AnimationState();

    public final AnimationState wakeAnimationState = new AnimationState();

    public final AnimationState pickUpItemAnimationState = new AnimationState();

    public final AnimationState dropItemAnimationState = new AnimationState();

    private boolean justWokeUp = false;
    private Float homeYaw = null;
    private Vec3d homePos = null;

    private Vec3d lastPosition = Vec3d.ZERO;
    private int ticksStill = 0;


    private static final TrackedData<Boolean> IS_SLEEPING;
    private static final TrackedData<Boolean> CAN_MOVE;
    private static final TrackedData<Boolean> DYED;
    private static final TrackedData<Integer> AGE;
    private static final TrackedData<ItemStack> HELD_ITEM;
    private static final TrackedData<Integer> COLOR;

    static {
        IS_SLEEPING = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_MOVE = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DYED = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        AGE = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HELD_ITEM = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
        COLOR = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    }

    public TuffGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SLEEPING, false);
        builder.add(CAN_MOVE, true);
        builder.add(DYED, false);
        builder.add(AGE, this.age);
        builder.add(HELD_ITEM, ItemStack.EMPTY);
        builder.add(COLOR, DyeColor.RED.getEntityColor());
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new TuffWanderGoal(this, SPEED));
        this.goalSelector.add(1, new TuffWanderAroundGoal(this, SPEED));
        this.goalSelector.add(1, new TemptGoal(this, SPEED, Ingredient.ofItems(Items.TUFF), false));
    }

    public static DefaultAttributeContainer.Builder createTuffGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 25f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20f)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1f);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient()) {
            Vec3d currentPosition = this.getPos();
            if (!isSleeping()) {
                if (justWokeUp) {
                    setCanMove(false);
                }
                if (this.getAgeLock() + 20 <= this.age && this.getAgeLock() + 30 <= this.age) {
                    setCanMove(true);
                }
            }
            if (currentPosition.squaredDistanceTo(lastPosition) < 0.001) {
                ticksStill++;
            } else {
                if (isSleeping()) {
                    setSleeping(false);
                    System.out.println("Is sleeping: " + isSleeping());
                }
                ticksStill = 0;
            }

            if (ticksStill >= 200 && !isSleeping()) {
                setSleeping(true);
            }

            lastPosition = currentPosition;
        }

        if (this.getWorld().isClient()) {
            setupAnimationStatesClient();
        }
    }
    public Integer getColor() {
        return this.dataTracker.get(COLOR);
    }
    public ItemStack getHeldItem() {
        return this.dataTracker.get(HELD_ITEM);
    }

    public void setHeldItem(ItemStack stack) {
        this.dataTracker.set(HELD_ITEM, stack);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(IS_SLEEPING);
    }
    public void setDyed(boolean dyed) {
        this.dataTracker.set(DYED, dyed);
    }

    public boolean getDyed() {
        return this.dataTracker.get(DYED);
    }

    public void setSleeping(boolean isSleeping) {
        this.dataTracker.set(IS_SLEEPING, isSleeping);
    }

    public boolean getCanMove() {
        return this.dataTracker.get(CAN_MOVE);
    }

    public void setCanMove(boolean move) {
        this.dataTracker.set(CAN_MOVE, move);
        if (!move) {
            this.dataTracker.set(AGE, this.age);
        }
    }
    public Integer getAgeLock() {
        return this.dataTracker.get(AGE);
    }
    public void setHome(Vec3d pos, float yaw) {
        this.homeYaw = yaw;
        this.homePos = pos;
    }
    public void setHomePos(Vec3d pos) {
        this.homePos = pos;
    }

    @Nullable
    public Float getHomeYaw() {
        return this.homeYaw;
    }

    public @Nullable Vec3d getHomePosition() {
        return homePos;
    }

    private void setupAnimationStatesClient() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
        if (isSleeping()) {
            sleepAnimationState.startIfNotRunning(this.age);
            justWokeUp = true;
            wakeAnimationState.stop();
        } else {
            sleepAnimationState.stop();

            System.out.println("Just Woke Up: " + justWokeUp);
            if (justWokeUp) {
                wakeAnimationState.start(this.age);
                justWokeUp = false;
//                System.out.println("Just Woke Up 222: " + justWokeUp);
            }
        }
        if (this.getHeldItem() != ItemStack.EMPTY) {
            pickUpItemAnimationState.startIfNotRunning(this.age);
            dropItemAnimationState.stop();
        } else {
            dropItemAnimationState.startIfNotRunning(this.age);
            pickUpItemAnimationState.stop();
        }
    }

    public static int combineColors(int color1, int color2) {
        int alpha1 = (color1 >> 24) & 0xFF;
        int red1 = (color1 >> 16) & 0xFF;
        int green1 = (color1 >> 8) & 0xFF;
        int blue1 = color1 & 0xFF;

        int alpha2 = (color2 >> 24) & 0xFF;
        int red2 = (color2 >> 16) & 0xFF;
        int green2 = (color2 >> 8) & 0xFF;
        int blue2 = color2 & 0xFF;

        // Combine by averaging the RGB channels, and keeping the alpha as is (fully opaque)
        int combinedRed = (red1 + red2) / 2;
        int combinedGreen = (green1 + green2) / 2;
        int combinedBlue = (blue1 + blue2) / 2;

        return (alpha1 << 24) | (combinedRed << 16) | (combinedGreen << 8) | combinedBlue;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);
        if (player.isSneaking() && handStack.getItem() instanceof PotionItem) {
            this.setDyed(false);
            this.dataTracker.set(COLOR, DyeColor.RED.getEntityColor());
            if (!player.isCreative()) {
                handStack.decrement(1);
                ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!this.getWorld().isClient) {
                    player.getInventory().offerOrDrop(emptyBottle);
                }
            }
            return ActionResult.SUCCESS;
        }

        if (player.isSneaking() && handStack.getItem() instanceof DyeItem dyeItem) {
            int newColor = dyeItem.getColor().getEntityColor();
            if (!this.getDyed()) {
                // First time clicking, just set the color
                this.dataTracker.set(COLOR, newColor);
                this.setDyed(true);
            } else {
                // Subsequent clicks, combine the color
                int currentColor = this.getColor();
                int combinedColor = combineColors(currentColor, newColor);
                this.dataTracker.set(COLOR, combinedColor);
            }
            if (!player.isCreative()) {
                handStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }

        if (player.isSneaking() && handStack.getItem() == Items.COMPASS) {
            Vec3d pos = this.getPos();
            this.setHome(pos, this.getHeadYaw());
            player.sendMessage(Text.literal("Tuff Golem home set!"), true);
            return ActionResult.SUCCESS;
        } else if (this.getHomePosition() != null && handStack.isIn(ItemTags.AXES) && player.isSneaking()) {
            setHomePos(null);
            player.sendMessage(Text.literal("Tuff Golem home cleared!"), true);
            if (!player.getAbilities().creativeMode) {
                handStack.damage(1, player, EquipmentSlot.MAINHAND);
            }
            return ActionResult.SUCCESS;
        }
        if (!player.isSneaking()) {
            System.out.println("Held Item is: " + this.getHeldItem() + ", Item Stack: " + handStack.isEmpty());
            if (this.getHeldItem().isEmpty() && !handStack.isEmpty()) {
                // Store the item
                setSleeping(false);
                ticksStill = 0;
                this.setHeldItem(handStack.copyWithCount(1));
                System.out.println("Item Stored: " + handStack.getItem().getName());
                if (!player.isCreative()) {
                    handStack.decrement(1);
                }
                return ActionResult.SUCCESS;
            } else if (!this.getHeldItem().isEmpty()) {
                // Give item back to player
                ticksStill = 0;
                setSleeping(false);
                if (!player.getInventory().insertStack(this.getHeldItem())) {
                    player.dropItem(this.getHeldItem(), false);
                }
                System.out.println("Item Returned: " + this.getHeldItem().getItem().getName());
                this.setHeldItem(ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sleeping", this.isSleeping());
        nbt.putBoolean("Dyed", this.getDyed());
        if (!this.getHeldItem().isEmpty()) {
            nbt.put("Item", this.getHeldItem().encode(this.getRegistryManager()));
        }
        if (homePos != null) {
            nbt.putDouble("HomeX", homePos.x);
            nbt.putDouble("HomeY", homePos.y);
            nbt.putDouble("HomeZ", homePos.z);
        }
        nbt.putInt("Color", this.getColor());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("Sleeping"));
        this.setDyed(nbt.getBoolean("Dyed"));
        ItemStack itemStack;
        if (nbt.contains("Item", 10)) {
            NbtCompound nbtCompound = nbt.getCompound("Item");
            itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
        } else {
            itemStack = ItemStack.EMPTY;
        }
        if (nbt.contains("Item", 10)) {
            NbtCompound nbtCompound = nbt.getCompound("Item");
            itemStack = ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY);
        }
        setHeldItem(itemStack);
        if (nbt.contains("HomeX")) {
            this.homePos = new Vec3d(nbt.getDouble("HomeX"), nbt.getDouble("HomeY"), nbt.getDouble("HomeZ"));
        }
        this.dataTracker.set(COLOR, nbt.getInt("Color"));
    }

}

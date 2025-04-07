package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.CopperGolemWanderGoal;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
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

    private Vec3d lastPosition = Vec3d.ZERO;
    private int ticksStill = 0;

    private Vec3d homePos = null;
    public int ticksAwayFromHome = 0;

    private static final TrackedData<Boolean> IS_SLEEPING; // New waxed state
    private static final TrackedData<Boolean> CAN_MOVE; // New waxed state
    private static final TrackedData<Integer> AGE; // New waxed state
    private static final TrackedData<ItemStack> HELD_ITEM;

    static {
        IS_SLEEPING = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_MOVE = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        AGE = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        HELD_ITEM = DataTracker.registerData(TuffGolemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    public TuffGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_SLEEPING, false);
        builder.add(CAN_MOVE, true);
        builder.add(AGE, this.age);
        builder.add(HELD_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, SPEED));
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

    public ItemStack getHeldItem() {
        return this.dataTracker.get(HELD_ITEM);
    }

    public void setHeldItem(ItemStack stack) {
        this.dataTracker.set(HELD_ITEM, stack);
    }

    public boolean isSleeping() {
        return this.dataTracker.get(IS_SLEEPING);
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
    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack handStack = player.getStackInHand(hand);

        if (player.isSneaking() && handStack.getItem() == Items.COMPASS) {
            this.homePos = this.getPos();
            player.sendMessage(Text.literal("Tuff Golem home set!"), true);
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
        if (!this.getHeldItem().isEmpty()) {
            nbt.put("Item", this.getHeldItem().encode(this.getRegistryManager()));
        }
        if (homePos != null) {
            nbt.putDouble("HomeX", homePos.x);
            nbt.putDouble("HomeY", homePos.y);
            nbt.putDouble("HomeZ", homePos.z);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSleeping(nbt.getBoolean("Sleeping"));
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
    }
    public @Nullable Vec3d getHomePosition() {
        return homePos;
    }
}

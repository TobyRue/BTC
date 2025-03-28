package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.CopperGolemButtonPressGoal;
import io.github.tobyrue.btc.entity.ai.CopperGolemTemptGoal;
import io.github.tobyrue.btc.entity.ai.CopperGolemWanderGoal;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperGolemEntity extends GolemEntity {
    private int oxidationTimer = 0;
    private static final int OXIDATION_INTERVAL = 20

//            * 60
            * 5; // Every 5 minutes (6000 ticks)
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    
    private static final double SPEED = 0.4D;

    public enum Oxidation {
        UNOXIDIZED, EXPOSED, WEATHERED, OXIDIZED
    }
    public enum ButtonDirection {
        NONE, UP, FRONT, DOWN
    }
    private static final TrackedData<Byte> OXIDATION_STATE;
    private static final TrackedData<Byte> BUTTON_STATE;
    private static final TrackedData<Boolean> WAXED_STATE; // New waxed state
    private static final TrackedData<Boolean> WAKE_UP_PLAYED; // New wake-up state
    private static final TrackedData<Boolean> CAN_MOVE_DELAY_ONE;
    private static final TrackedData<Boolean> CAN_MOVE_DELAY_TWO;
    private static final TrackedData<Integer> AGE_LOCK;

    static {
        OXIDATION_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
        WAXED_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        WAKE_UP_PLAYED = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_MOVE_DELAY_ONE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        CAN_MOVE_DELAY_TWO = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        AGE_LOCK = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
        BUTTON_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    public final AnimationState wakeUpAnimationState = new AnimationState();

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState buttonPressFrontAnimationState = new AnimationState();
    public final AnimationState buttonPressUpAnimationState = new AnimationState();
    public final AnimationState buttonPressDownAnimationState = new AnimationState();

    public CopperGolemEntity(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createCopperGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.8f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20f)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(0, new CopperGolemButtonPressGoal(this, SPEED));
        this.goalSelector.add(1, new CopperGolemWanderGoal(this, SPEED));
        this.goalSelector.add(2, new CopperGolemTemptGoal(this, SPEED, Ingredient.ofItems(Items.HONEYCOMB), false));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OXIDATION_STATE, (byte) Oxidation.UNOXIDIZED.ordinal());
        builder.add(BUTTON_STATE, (byte) ButtonDirection.NONE.ordinal());
        builder.add(WAXED_STATE, false);
        builder.add(WAKE_UP_PLAYED, false);
        builder.add(CAN_MOVE_DELAY_ONE, false);
        builder.add(CAN_MOVE_DELAY_TWO, true);
        builder.add(AGE_LOCK, this.age);
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        return NoPenaltyTargeting.find(this, 7, 7);
    }

    private void setupAnimationStatesClient() {
        if (this.getOxidation() != Oxidation.OXIDIZED) {
            if (this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.startIfNotRunning(this.age);
            } else {
                --this.idleAnimationTimeout;
            }
        }
        if (this.age <= 80 && this.getOxidation() != Oxidation.OXIDIZED && !this.hasWokenUp()) {
            this.wakeUpAnimationState.startIfNotRunning(this.age);
        }
        if (this.getOxidation() != Oxidation.OXIDIZED) {
            if (this.getButtonDirection() != ButtonDirection.NONE) {
                if (this.getButtonDirection() == ButtonDirection.FRONT) {
                    this.buttonPressFrontAnimationState.startIfNotRunning(this.age);
                } else if (this.getButtonDirection() == ButtonDirection.UP) {
                    this.buttonPressUpAnimationState.startIfNotRunning(this.age);
                } else if (this.getButtonDirection() == ButtonDirection.DOWN) {
                    this.buttonPressDownAnimationState.startIfNotRunning(this.age);
                }
            } else {
                this.buttonPressFrontAnimationState.stop();
                this.buttonPressUpAnimationState.stop();
                this.buttonPressDownAnimationState.stop();
            }
        }
    }
    private void setupAnimationStatesServer() {
        if (this.age <= 80 && this.getOxidation() != Oxidation.OXIDIZED) {
            setWokenUp(true);
        } else if ((this.getAgeLock() + 80 < this.age && this.getAgeLock() + 120 >= this.age)) {
            setCanMoveDelayOne(true);
        } else if ((this.getAgeLock() + 40 <= this.age && this.getAgeLock() + 80 > this.age)) {
            setCanMoveDelayTwo(true);
            setButtonDirection(ButtonDirection.NONE);
            navigateAround();
        }
    }
    public void navigateAround() {
        Vec3d vec3d = this.getWanderTarget();
        if (vec3d != null) {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
        }
        this.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, SPEED * this.getSpeedMultiplier());
    }

    public void setButtonDirection(ButtonDirection direction) {
        this.dataTracker.set(BUTTON_STATE, (byte) direction.ordinal());
    }

    public ButtonDirection getButtonDirection() {
        return ButtonDirection.values()[this.dataTracker.get(BUTTON_STATE)];
    }

    public boolean hasWokenUp() {
        return this.dataTracker.get(WAKE_UP_PLAYED);
    }

    public void setWokenUp(boolean wake) {
        this.dataTracker.set(WAKE_UP_PLAYED, wake);
    }

    public boolean cantMove() {
        return !getCanMoveDelayOne() || !getCanMoveDelayTwo();
    }

    public boolean getCanMoveDelayOne() {
        return this.dataTracker.get(CAN_MOVE_DELAY_ONE);
    }

    public void setCanMoveDelayOne(boolean move) {
        this.dataTracker.set(CAN_MOVE_DELAY_ONE, move);
        if (!move) {
            this.dataTracker.set(AGE_LOCK, this.age);
        }
    }

    public boolean getCanMoveDelayTwo() {
        return this.dataTracker.get(CAN_MOVE_DELAY_TWO);
    }

    public void setCanMoveDelayTwo(boolean move) {
        this.dataTracker.set(CAN_MOVE_DELAY_TWO, move);
        if (!move) {
            this.dataTracker.set(AGE_LOCK, this.age);
        }
    }

    public Integer getAgeLock() {
        return this.dataTracker.get(AGE_LOCK);
    }

    public boolean isWaxed() {
        return this.dataTracker.get(WAXED_STATE);
    }

    public void setWaxed(boolean waxed) {
        this.dataTracker.set(WAXED_STATE, waxed);
    }

    public void setOxidation(Oxidation oxidation) {
        this.dataTracker.set(OXIDATION_STATE, (byte) oxidation.ordinal());
    }

    public Oxidation getOxidation() {
        return Oxidation.values()[this.dataTracker.get(OXIDATION_STATE)];
    }

    private void advanceOxidation() {
        Oxidation currentState = this.getOxidation();
        Oxidation nextState = switch (currentState) {
            case UNOXIDIZED -> Oxidation.EXPOSED;
            case EXPOSED -> Oxidation.WEATHERED;
            case WEATHERED, OXIDIZED -> Oxidation.OXIDIZED;
        };
        this.setOxidation(nextState);
    }

    private void lowerOxidation() {
        Oxidation currentState = this.getOxidation();
        Oxidation nextState = switch (currentState) {
            case OXIDIZED -> Oxidation.WEATHERED;
            case WEATHERED -> Oxidation.EXPOSED;
            case EXPOSED, UNOXIDIZED -> Oxidation.UNOXIDIZED;
        };
        this.setOxidation(nextState);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.LIGHTNING_BOLT) || source.isOf(DamageTypes.LAVA)) {
            return false;
        }
        return super.damage(source, amount);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    public float getSpeedMultiplier() {
        return switch (this.getOxidation()) {
            case UNOXIDIZED -> 1.0f;
            case EXPOSED -> 0.75f;
            case WEATHERED -> 0.5f;
            case OXIDIZED -> 0.0f;
        };
    }


    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            setupAnimationStatesClient();
        } else {
            setupAnimationStatesServer();
        }
        if (!this.getWorld().isClient) {
            if (!this.isWaxed()) {
                this.oxidationTimer++;

                if (this.oxidationTimer >= OXIDATION_INTERVAL) {
                    this.oxidationTimer = 0;
                    this.advanceOxidation();
                }
            }
        }
    }



    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (itemStack.isOf(Items.HONEYCOMB)) {
            if (!this.isWaxed()) {
                this.setWaxed(true); // Mark as waxed
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0F, 1.0F);

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        }
        if (itemStack.getItem() instanceof AxeItem) {
            if (this.isWaxed()) {
                this.setWaxed(false); // Mark as waxed
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().creativeMode) {
                    itemStack.damage(3, player, EquipmentSlot.MAINHAND);
                }
                return ActionResult.SUCCESS;
            } else if (this.getOxidation() != Oxidation.UNOXIDIZED) {
                lowerOxidation();
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().creativeMode) {
                    itemStack.damage(3, player, EquipmentSlot.MAINHAND);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.interactMob(player, hand);
    }



    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("OxidationState", (byte) this.getOxidation().ordinal());  // Save the oxidation state
        nbt.putBoolean("Waxed", this.isWaxed());  // Save the waxed state
        nbt.putBoolean("WakeUpState", this.hasWokenUp());  // Save the wake-up state
        nbt.putBoolean("FirstSpawnedState", this.getCanMoveDelayOne());  // Save the spawn state
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("OxidationState", NbtElement.BYTE_TYPE)) {
            this.setOxidation(Oxidation.values()[nbt.getByte("OxidationState")]);
        }
        if (nbt.contains("Waxed")) {  // No type check needed for booleans
            this.setWaxed(nbt.getBoolean("Waxed"));
        }
        if (nbt.contains("WakeUpState")) {  // No type check needed for booleans
            this.setWokenUp(nbt.getBoolean("WakeUpState"));
        }
        if (nbt.contains("FirstSpawnedState")) {  // No type check needed for booleans
            this.setCanMoveDelayOne(nbt.getBoolean("FirstSpawnedState"));
        }
    }
}

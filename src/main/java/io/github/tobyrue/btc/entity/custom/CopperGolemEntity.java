package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.CopperGolemTemptGoal;
import io.github.tobyrue.btc.entity.ai.CopperGolemWanderGoal;
import io.github.tobyrue.btc.entity.animation.ModAnimations;
import io.github.tobyrue.btc.enums.AttackType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CopperGolemEntity extends GolemEntity {
    private int oxidationTimer = 0;
    private static final int OXIDATION_INTERVAL = 20
//            * 60
            * 5; // Every 5 minutes (6000 ticks)

    public enum Oxidation {
        UNOXIDIZED, EXPOSED, WEATHERED, OXIDIZED
    }


    public final AnimationState wakeUpAnimationState = new AnimationState();


    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;


    private static final TrackedData<Byte> OXIDATION_STATE;
    private static final TrackedData<Boolean> WAXED_STATE; // New waxed state
    private static final TrackedData<Boolean> WAKE_UP_PLAYED; // New wake-up state

    static {
        OXIDATION_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
        WAXED_STATE = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        WAKE_UP_PLAYED = DataTracker.registerData(CopperGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }


    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0 && this.getOxidation() != Oxidation.OXIDIZED) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
        if (this.age <= 80 && !hasWokenUp() && this.getOxidation() != Oxidation.OXIDIZED) {
            this.wakeUpAnimationState.start(this.age);
            setWokenUp(true);
        }
    }

    public boolean hasWokenUp() {
        return this.dataTracker.get(WAKE_UP_PLAYED);
    }

    public void setWokenUp(boolean wake) {
        this.dataTracker.set(WAKE_UP_PLAYED, wake);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.IN_FIRE) || source.isOf(DamageTypes.ON_FIRE) || source.isOf(DamageTypes.LIGHTNING_BOLT) || source.isOf(DamageTypes.LAVA)) {
            return false;
        }
        return super.damage(source, amount);
    }

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
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(OXIDATION_STATE, (byte) Oxidation.UNOXIDIZED.ordinal());
        builder.add(WAXED_STATE, false);
        builder.add(WAKE_UP_PLAYED, false);
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
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new CopperGolemWanderGoal(this, 0.4D));
        this.goalSelector.add(2, new CopperGolemTemptGoal(this, 0.4D, Ingredient.ofItems(Items.HONEYCOMB), false));
    }

    @Override
    public void tick() {
        super.tick();
//        System.out.println("Oxidation Timer: " + this.oxidationTimer + " Oxidation: " + this.getOxidation());

        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }

        if (!this.getWorld().isClient) {
            if (!this.isWaxed()) { // Only oxidize if NOT waxed
                this.oxidationTimer++;

                if (this.oxidationTimer >= OXIDATION_INTERVAL) {
                    this.oxidationTimer = 0;
                    this.advanceOxidation();
//                    System.out.println("Oxidation State Changed: " + this.getOxidation());
                }
            }
        }
    }

    public boolean isWaxed() {
        return this.dataTracker.get(WAXED_STATE);
    }

    public void setWaxed(boolean waxed) {
        this.dataTracker.set(WAXED_STATE, waxed);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Check if the player is using honeycomb
        if (itemStack.isOf(Items.HONEYCOMB)) {
            if (!this.isWaxed()) {
                this.setWaxed(true); // Mark as waxed
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0F, 1.0F);

                // Reduce honeycomb count
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


    public void setOxidation(Oxidation oxidation) {
        this.dataTracker.set(OXIDATION_STATE, (byte) oxidation.ordinal());
    }

    public Oxidation getOxidation() {
        return Oxidation.values()[this.dataTracker.get(OXIDATION_STATE)];
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("OxidationState", (byte) this.getOxidation().ordinal());  // Save the oxidation state
        nbt.putBoolean("Waxed", this.isWaxed());  // Save the waxed state
        nbt.putBoolean("WakeUpState", this.hasWokenUp());  // Save the wake-up state
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
            this.setWokenUp(nbt.getBoolean("Waxed"));
        }
    }
}

package io.github.tobyrue.btc.entity.custom;

import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class KeyGolemEntity extends TameableShoulderEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState disappointedAnimationState = new AnimationState();
    public final AnimationState fallAsleepAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState baseAnimationState = new AnimationState();
    private ActionAnim currentAnim = ActionAnim.NONE;
    private ActionAnim desiredAnim = ActionAnim.NONE;
    private int idleTime = 0;
    private boolean sleeping = false;
    private boolean didMoveLastTick = false;
    private boolean didMoveThisTick = false;
    private Vec3d currentPos;
    private Vec3d lastPos;
    private int disappointTimer = 0;
    private int animationLock = 0;

    public boolean isWakingUp() {
        return wakingUp;
    }

    public void setWakingUp(boolean wakingUp) {
        this.wakingUp = wakingUp;
    }

    private boolean wakingUp = false;


    private static final TrackedData<Boolean> IS_PANICKED = DataTracker.registerData(KeyGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> WAS_PICKED_UP = DataTracker.registerData(KeyGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> IS_DISAPPOINTED = DataTracker.registerData(KeyGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> PLAYER = DataTracker.registerData(KeyGolemEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    public KeyGolemEntity(EntityType<? extends TameableShoulderEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createKeyGolemAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 5f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1);
    }


    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(IS_PANICKED, false);
        builder.add(WAS_PICKED_UP, false);
        builder.add(IS_DISAPPOINTED, 0);
        builder.add(PLAYER, Optional.empty());
    }

    public boolean isPanicked() {
        return this.dataTracker.get(IS_PANICKED);
    }

    public void setIsPanicked(boolean isPanicked) {
        this.dataTracker.set(IS_PANICKED, isPanicked);
    }
    public boolean wasPickedUp() {
        return this.dataTracker.get(WAS_PICKED_UP);
    }

    public void setWasPickedUp(boolean wasPickedUp) {
        this.dataTracker.set(WAS_PICKED_UP, wasPickedUp);
    }

    public int getIsDisappointed() {
        return this.dataTracker.get(IS_DISAPPOINTED);
    }

    public void setIsDisappointed(int isDisappointed) {
        this.dataTracker.set(IS_DISAPPOINTED, isDisappointed);
    }

    public Optional<UUID> getPlayerUUID() {
        return this.dataTracker.get(PLAYER);
    }

    public void setPlayerUUID(Optional<UUID> uuid) {
        this.dataTracker.set(PLAYER, uuid);
    }


    private void playAction(ActionAnim anim) {

        if (anim.priority < currentAnim.priority)
            return;

        stopActionsNotCurrent(anim);

        currentAnim = anim;
        switch (anim) {
            case IDLE -> idleAnimationState.startIfNotRunning(this.age);
            case FALL_ASLEEP -> fallAsleepAnimationState.startIfNotRunning(this.age);
            case SLEEP -> sleepAnimationState.startIfNotRunning(this.age);
            case WAKE_UP -> wakeUpAnimationState.startIfNotRunning(this.age);
            case DISAPPOINTED -> disappointedAnimationState.startIfNotRunning(this.age);
        }
    }

    private void stopActionsNotCurrent(ActionAnim anim) {
        switch (anim) {
            case IDLE -> {
                disappointedAnimationState.stop();
                fallAsleepAnimationState.stop();
                sleepAnimationState.stop();
                wakeUpAnimationState.stop();
            }
            case FALL_ASLEEP -> {
                idleAnimationState.stop();
                disappointedAnimationState.stop();
                sleepAnimationState.stop();
                wakeUpAnimationState.stop();
            }
            case SLEEP -> {
                idleAnimationState.stop();
                disappointedAnimationState.stop();
                fallAsleepAnimationState.stop();
                wakeUpAnimationState.stop();
            }
            case WAKE_UP -> {
                idleAnimationState.stop();
                disappointedAnimationState.stop();
                fallAsleepAnimationState.stop();
                sleepAnimationState.stop();
            }
            case DISAPPOINTED -> {
                idleAnimationState.stop();
                fallAsleepAnimationState.stop();
                sleepAnimationState.stop();
                wakeUpAnimationState.stop();
            }
        }
    }


    private void stopActions() {
        idleAnimationState.stop();
        disappointedAnimationState.stop();
        fallAsleepAnimationState.stop();
        sleepAnimationState.stop();
        wakeUpAnimationState.stop();
    }

    @Override
    public boolean mountOnto(ServerPlayerEntity player) {
        setPlayerUUID(Optional.ofNullable(player.getUuid()));
        return super.mountOnto(player);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isDirect() && source.getAttacker() instanceof ServerPlayerEntity entity) {
            this.heal(amount);
            mountOnto(entity);
        }
        return super.damage(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        lastPos = currentPos;
        currentPos = this.getPos();

        didMoveLastTick = didMoveThisTick;
        didMoveThisTick = (this.getVelocity().lengthSquared() > 0.001) ||
                (lastPos != null && currentPos != null && !lastPos.equals(currentPos));
        if (disappointTimer > 0) {
            disappointTimer--;
        }
        // Panic sync
        if (this.isPanicked() != (this.getMaxHealth() == this.getHealth())) {
            this.setIsPanicked(!this.isPanicked());
        }

        if (!this.getWorld().isClient)
            return;

        baseAnimationState.startIfNotRunning(this.age);


        if (getIsDisappointed() > 0) {
            desiredAnim = ActionAnim.DISAPPOINTED;
        }

        else if (didMoveThisTick) {

            idleTime = 0;

            if (currentAnim == ActionAnim.SLEEP || currentAnim == ActionAnim.FALL_ASLEEP)
                desiredAnim = ActionAnim.WAKE_UP;
            else
                desiredAnim = ActionAnim.IDLE;

            sleeping = false;
        }
        else {

            idleTime++;

            if (!sleeping && idleTime > 20 * 20) {
                desiredAnim = ActionAnim.FALL_ASLEEP;
            }
            else if (sleeping) {
                desiredAnim = ActionAnim.SLEEP;
            }
            else {
                desiredAnim = ActionAnim.IDLE;
            }
        }

        /*  TRANSITIONS  */

        if (animationLock > 0)
            animationLock--;

        if (animationLock <= 0 && getIsDisappointed() > 0) {
            setIsDisappointed(0);
            disappointedAnimationState.stop();
        }

        // only allow switching when not locked
        if (animationLock == 0 && desiredAnim != currentAnim) {

            stopActions();
            currentAnim = desiredAnim;

            switch (currentAnim) {

                case IDLE -> idleAnimationState.start(this.age);

                case FALL_ASLEEP -> {
                    fallAsleepAnimationState.start(this.age);
                    sleeping = true;
                    animationLock = 60; // length of fall asleep animation
                }

                case SLEEP -> sleepAnimationState.start(this.age);

                case WAKE_UP -> {
                    wakeUpAnimationState.start(this.age);
                    sleeping = false;
                    animationLock = 20;
                }

                case DISAPPOINTED -> {
                    disappointedAnimationState.start(this.age);
                    animationLock = disappointTimer; // exact duration
                }
            }
        }
    }



    public void onPlayerDeath(PlayerEntity player, DamageSource source) {
        if (this.getPlayerUUID().isEmpty()) return;
        if (!player.getUuid().equals(this.getPlayerUUID().get())) return;
        setIsDisappointed(this.random.nextInt(2) + 1);
        disappointTimer = (this.random.nextInt(2) + 1) == 1 ? 30 : 75;
    }


    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Panicked", this.isPanicked());
        nbt.putBoolean("WasPickedUp", this.wasPickedUp());
        nbt.putInt("Disappointed", this.getIsDisappointed());
        this.getPlayerUUID().ifPresent(uuid -> nbt.putUuid("PlayerUUID", uuid));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("Panicked")) {
            this.setIsPanicked(nbt.getBoolean("Panicked"));
        }
        if (nbt.contains("WasPickedUp")) {
            this.setWasPickedUp(nbt.getBoolean("WasPickedUp"));
        }
        if (nbt.contains("Disappointed")) {
            this.setIsDisappointed(nbt.getInt("Disappointed"));
        }
        if (nbt.containsUuid("PlayerUUID")) {
            this.setPlayerUUID(Optional.of(nbt.getUuid("PlayerUUID")));
        } else {
            this.setPlayerUUID(Optional.empty());
        }
    }
    private enum ActionAnim {
        NONE(0),
        IDLE(1),
        SLEEP(2),
        WAKE_UP(3),
        FALL_ASLEEP(4),
        DISAPPOINTED(10);

        final int priority;
        ActionAnim(int priority) { this.priority = priority; }
    }

}

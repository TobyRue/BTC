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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class KeyGolemEntity extends TameableShoulderEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState playerDie1AnimationState = new AnimationState();
    public final AnimationState playerDie2AnimationState = new AnimationState();
    public final AnimationState fallAsleepAnimationState = new AnimationState();
    public final AnimationState wakeUpAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState baseAnimationState = new AnimationState();
    private ActionAnim currentAnim = ActionAnim.NONE;
    private ActionAnim lastAnim = ActionAnim.NONE;
    private Vec3d currentPos;
    private Vec3d lastPos;
    private boolean movedThisTick = false;
    private boolean movedLastTick = false;
    private int idleTimer = 0;
    private int animationTimer = 0;
    private int movementStopDelay = 0;
    private static final int MOVEMENT_STOP_TICKS = 2; // overlap for 2 ticks
    private static final int IDLE_PLAYS = 1;

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


//    private void playAction(ActionAnim anim) {
//
//
//        stopActionsNotCurrent(anim);
//
//        currentAnim = anim;
//        switch (anim) {
//            case IDLE -> idleAnimationState.startIfNotRunning(this.age);
//            case FALL_ASLEEP -> fallAsleepAnimationState.startIfNotRunning(this.age);
//            case SLEEP -> sleepAnimationState.startIfNotRunning(this.age);
//            case WAKE_UP -> wakeUpAnimationState.startIfNotRunning(this.age);
//            case PLAYER_DEATH_1 -> playerDie1AnimationState.startIfNotRunning(this.age);
//        }
//    }
//
//    private void stopActionsNotCurrent(ActionAnim anim) {
//        switch (anim) {
//            case IDLE -> {
//                playerDie1AnimationState.stop();
//                playerDie2AnimationState.stop();
//                fallAsleepAnimationState.stop();
//                sleepAnimationState.stop();
//                wakeUpAnimationState.stop();
//            }
//            case FALL_ASLEEP -> {
//                idleAnimationState.stop();
//                playerDie1AnimationState.stop();
//                playerDie2AnimationState.stop();
//                sleepAnimationState.stop();
//                wakeUpAnimationState.stop();
//            }
//            case SLEEP -> {
//                idleAnimationState.stop();
//                playerDie1AnimationState.stop();
//                playerDie2AnimationState.stop();
//                fallAsleepAnimationState.stop();
//                wakeUpAnimationState.stop();
//            }
//            case WAKE_UP -> {
//                idleAnimationState.stop();
//                playerDie1AnimationState.stop();
//                playerDie2AnimationState.stop();
//                fallAsleepAnimationState.stop();
//                sleepAnimationState.stop();
//            }
//            case PLAYER_DEATH_1 -> {
//                idleAnimationState.stop();
//                playerDie2AnimationState.stop();
//                fallAsleepAnimationState.stop();
//                sleepAnimationState.stop();
//                wakeUpAnimationState.stop();
//            }
//            case PLAYER_DEATH_2 -> {
//                idleAnimationState.stop();
//                playerDie1AnimationState.stop();
//                fallAsleepAnimationState.stop();
//                sleepAnimationState.stop();
//                wakeUpAnimationState.stop();
//            }
//        }
//    }
//

    private void stopActions() {
        idleAnimationState.stop();
        playerDie1AnimationState.stop();
        playerDie2AnimationState.stop();
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

    public void setAnimation(ActionAnim requested) {
        if (this.getWorld().isClient) {
            System.out.println(requested);
        }
        if (didMove() && !requested.playsOverMovement) {
            requested = ActionAnim.NONE;
            System.out.println("Hello mf: " + didMove() + " 2: " + !requested.playsOverMovement);
        }
        if (animationTimer > 0 && !currentAnim.canBeStopped && !requested.canOveride) {
            return;
        }
        if (requested == currentAnim && !currentAnim.repeats) {
            return;
        }

        switch (currentAnim) {
            case IDLE -> idleAnimationState.stop();
            case SLEEP -> sleepAnimationState.stop();
            case WAKE_UP -> wakeUpAnimationState.stop();
            case FALL_ASLEEP -> fallAsleepAnimationState.stop();
            case PLAYER_DEATH_1 -> playerDie1AnimationState.stop();
            case PLAYER_DEATH_2 -> playerDie2AnimationState.stop();
            case null, default -> stopActions();
        }

        lastAnim = currentAnim;
        currentAnim = requested;

        switch (currentAnim) {
            case IDLE -> idleAnimationState.startIfNotRunning(this.age);
            case SLEEP -> sleepAnimationState.startIfNotRunning(this.age);
            case WAKE_UP -> wakeUpAnimationState.startIfNotRunning(this.age);
            case FALL_ASLEEP -> fallAsleepAnimationState.startIfNotRunning(this.age);
            case PLAYER_DEATH_1 -> playerDie1AnimationState.startIfNotRunning(this.age);
            case PLAYER_DEATH_2 -> playerDie2AnimationState.startIfNotRunning(this.age);
            case null, default -> baseAnimationState.startIfNotRunning(this.age);
        }
        animationTimer = currentAnim.canBeStopped ? 0 : currentAnim.duration;
    }


    public boolean didMove() {
        return this.getVelocity().lengthSquared() > 0.01 || (lastPos != null && currentPos != null && !lastPos.equals(currentPos));
    }

    @Override
    public void tick() {
        super.tick();
        baseAnimationState.startIfNotRunning(this.age);

        this.lastPos = currentPos;
        this.currentPos = this.getPos();
        this.movedLastTick = movedThisTick;
        this.movedThisTick = didMove();
        if (this.isPanicked() != (this.getMaxHealth() == this.getHealth())) {
            this.setIsPanicked(!this.isPanicked());
        }

        if (animationTimer > 0) {
            animationTimer--;
        }

        if (animationTimer == 0 && currentAnim.next != null) {
            setAnimation(currentAnim.next);
            return;
        }

        if (didMove()) {
            idleTimer = 0;
            if (currentAnim == ActionAnim.SLEEP || currentAnim == ActionAnim.FALL_ASLEEP) {
                setAnimation(ActionAnim.WAKE_UP);
            } else if (!currentAnim.playsOverMovement) {
                setAnimation(ActionAnim.NONE);
            }
        } else {
            idleTimer++;
            if (idleTimer == 100 * IDLE_PLAYS) {
                setAnimation(ActionAnim.FALL_ASLEEP);
            } else if (idleTimer < 100 * IDLE_PLAYS && currentAnim == ActionAnim.NONE) {
                setAnimation(ActionAnim.IDLE);
            }
        }
    }



    public void onPlayerDeath(PlayerEntity player, DamageSource source) {
        if (this.getPlayerUUID().isEmpty()) return;
        if (!player.getUuid().equals(this.getPlayerUUID().get())) return;
        setIsDisappointed(this.random.nextInt(2) + 1);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);

        if (IS_DISAPPOINTED.equals(data)) {
            setAnimation(getIsDisappointed() == 0 ? ActionAnim.NONE : getIsDisappointed() == 1 ? ActionAnim.PLAYER_DEATH_1 : ActionAnim.PLAYER_DEATH_2);
        }
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
    public enum ActionAnim {
        NONE(true, false, false, false, 0, null),
        IDLE(true, false,  false, true, 10 * 20, null),
        SLEEP(true, false, true, true, (int) (9.25 * 20), null),
        WAKE_UP(false, false, true, false, 1 * 20, IDLE),
        FALL_ASLEEP(false, false, false, false, 3 * 20, SLEEP),
        PLAYER_DEATH_1(false, true, true, false, 30, IDLE),
        PLAYER_DEATH_2(false, true, true, false, 75, IDLE);

        final boolean canBeStopped;
        final boolean canOveride;
        final boolean playsOverMovement;
        final boolean repeats;
        final int duration;
        @Nullable final ActionAnim next;
        ActionAnim(boolean canBeStopped, boolean canOveride, boolean playsOverMovement, boolean repeats, int duration, @Nullable ActionAnim next) {
            this.canBeStopped = canBeStopped;
            this.canOveride = canOveride;
            this.playsOverMovement = playsOverMovement;
            this.repeats = repeats;
            this.duration = duration;
            this.next = next;
        }
    }

}

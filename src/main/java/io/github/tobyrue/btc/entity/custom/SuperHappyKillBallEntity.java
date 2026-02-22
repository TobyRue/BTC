package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.List;

public class SuperHappyKillBallEntity extends ProjectileEntity {
    public final AnimationState state = new AnimationState();
    private static final TrackedData<Integer> BOUNCES = DataTracker.registerData(SuperHappyKillBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> SIZE = DataTracker.registerData(SuperHappyKillBallEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> SET = DataTracker.registerData(SuperHappyKillBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> WAIT = DataTracker.registerData(SuperHappyKillBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DMG = DataTracker.registerData(SuperHappyKillBallEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean set = false;

    public float getSize() {
        return this.dataTracker.get(SIZE);
    }
    public int getBounces() {
        return this.dataTracker.get(BOUNCES);
    }
    public int getWait() {
        return this.dataTracker.get(WAIT);
    }
    public int getDamage() {
        return this.dataTracker.get(DMG);
    }
    public void setSize(float size) {
        this.dataTracker.set(SIZE, size);
    }
    public void setBounces(int bounces) {
        this.dataTracker.set(BOUNCES, bounces);
    }
    public void setWait(int wait) {
        this.dataTracker.set(WAIT, wait);
    }
    public void setDamage(int damage) {
        this.dataTracker.set(DMG, damage);
    }
    public SuperHappyKillBallEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.noClip = false;
        this.dataTracker.set(BOUNCES, -1);
        this.dataTracker.set(SIZE, 1f);
    }

    protected SuperHappyKillBallEntity(double x, double y, double z, World world) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.noClip = false;
        this.dataTracker.set(BOUNCES, -1);
        this.dataTracker.set(SIZE, 1f);
    }

    public SuperHappyKillBallEntity(double x, double y, double z, Vec3d velocity, World world) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
        this.noClip = false;
        this.dataTracker.set(BOUNCES, -1);
        this.dataTracker.set(SIZE, 1f);
    }
    public SuperHappyKillBallEntity(double x, double y, double z, Vec3d velocity, World world, int bounces) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
        this.noClip = false;
        this.dataTracker.set(BOUNCES, bounces);
        this.dataTracker.set(SIZE, 1f);
    }
    public SuperHappyKillBallEntity(double x, double y, double z, Vec3d velocity, World world, int bounces, float size) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
        this.noClip = false;
        this.dataTracker.set(BOUNCES, bounces);
        this.dataTracker.set(SIZE, size);
    }

    public static Vec3d getVelocityFromDirection(Direction direction, double speed) {
        return switch (direction) {
            case NORTH -> new Vec3d(0, 0, -speed);
            case EAST -> new Vec3d(speed, 0, 0);
            case SOUTH -> new Vec3d(0, 0, speed);
            case WEST -> new Vec3d(-speed, 0, 0);
            case UP -> new Vec3d(0, speed, 0);
            case DOWN -> new Vec3d(0, -speed, 0);
        };
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(BOUNCES, -1);
        builder.add(SIZE, 1f);
        builder.add(SET, false);
        builder.add(WAIT, 0);
        builder.add(DMG, 4);
    }

    public static Vec3d getVelocityFromDirection(Direction xDir, Direction yDir, Direction zDir, double speedX, double speedY, double speedZ) {
        var xAxis = switch (xDir) {
            case NORTH -> -1;
            case EAST -> 0;
            case SOUTH -> -1;
            case WEST -> 1;
            case UP -> -1;
            case DOWN -> -1;
        };
        var yAxis = switch (yDir) {
            case NORTH -> 1;
            case EAST -> -1;
            case SOUTH -> 0;
            case WEST -> -1;
            case UP -> -1;
            case DOWN -> -1;
        };
        var zAxis = switch (zDir) {
            case NORTH -> -1;
            case EAST -> -1;
            case SOUTH -> -1;
            case WEST -> -1;
            case UP -> 0;
            case DOWN -> 1;
        };

        var x = xAxis == -1 ? 0 : xAxis == 0 ? speedX : -speedX;
        var y = yAxis == -1 ? 0 : yAxis == 0 ? speedY : -speedY;
        var z = zAxis == -1 ? 0 : zAxis == 0 ? speedZ : -speedZ;
        return new Vec3d(x, y, z);
    }

    @Override
    public void tick() {
        if (getBounces() != -1 && !this.dataTracker.get(SET)) {
            setBounces(getBounces() + 1);
            this.dataTracker.set(SET, true);
        }
        if (this.dataTracker.get(SIZE) == 0) {
            this.dataTracker.set(SIZE, 1f);
            this.calculateDimensions();
        }
        this.state.startIfNotRunning(this.age);
        if (this.dataTracker.get(WAIT) > 0) {
            this.dataTracker.set(WAIT, this.dataTracker.get(WAIT) - 1);
        }
        if (this.dataTracker.get(DMG) <= 0) {
            this.dataTracker.set(DMG, 4);
        }
        // 1. Basic Setup

        // 2. MOVE FIRST (Using your custom move method)
        // This is where the bounce happens
        Vec3d velocityBeforeMove = this.getVelocity();
        this.move(MovementType.SELF, velocityBeforeMove);

        // 3. APPLY ACCELERATION SECOND
        // Get the velocity again (it might have changed due to a bounce)
        Vec3d velocityAfterMove = this.getVelocity();

        // 4. APPLY DRAG
        this.setVelocity(velocityAfterMove.multiply(this.isTouchingWater() ? 0.8 : 1));
        // 5. UPDATE ROTATION
        // Face the direction of current velocity
        Vec3d finalVel = this.getVelocity();
        float yaw = (float)(MathHelper.atan2(finalVel.x, finalVel.z) * (180 / Math.PI));
        float pitch = (float)(MathHelper.atan2(finalVel.y, finalVel.horizontalLength()) * (180 / Math.PI));
        this.setRotation(yaw, pitch);

        // 6. PARTICLES
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY() + (getHeight() / 2f), this.getZ(), 0, 0, 0);
        }

        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            this.discard();
        }
        if (!this.getWorld().isClient && this.getBlockY() < this.getWorld().getBottomY() - 30) {
            this.discard();
        }
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.hitOrDeflect(hitResult);
    }

    public float getRotationSpeed() {
        final float s = 2;
        final float k = (float) Math.E/10;
        return (float) ((s - 1)*Math.exp(-k*(getSize() - 1)) + 1);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        Direction hitSide = blockHitResult.getSide();
        Vec3d velocity = this.getVelocity();

        velocity = switch (hitSide.getAxis()) {
            case X -> new Vec3d(-velocity.x, velocity.y, velocity.z);
            case Y -> new Vec3d(velocity.x, -velocity.y, velocity.z);
            case Z -> new Vec3d(velocity.x, velocity.y, -velocity.z);
        };

        this.setVelocity(velocity);

        Vec3d nudge = Vec3d.of(hitSide.getVector()).multiply(0.02);
        this.setPosition(this.getPos().add(nudge));
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }
    
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (this.dataTracker.get(WAIT) <= 0) {
            this.dataTracker.set(WAIT, 20);
            entityHitResult.getEntity().damage(this.getDamageSources().magic(), this.dataTracker.get(DMG));
        }
        super.onEntityHit(entityHitResult);
    }




    @Override
    public void move(MovementType movementType, Vec3d movement) {
        // 1. Handle NoClip
        if (this.noClip) {
            this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
            return;
        }

        this.getWorld().getProfiler().push("move");

        // 2. Simple Collision Adjustment
        // This uses your method to see how far it can go before hitting a wall/ceiling
        Vec3d collidedMovement = this.adjustMovementForCollisions(movement);

        if (collidedMovement.lengthSquared() > 1.0E-7) {
            this.setPosition(this.getX() + collidedMovement.x, this.getY() + collidedMovement.y, this.getZ() + collidedMovement.z);
        }


        boolean hitX = !MathHelper.approximatelyEquals(movement.x, collidedMovement.x);
        boolean hitY = !MathHelper.approximatelyEquals(movement.y, collidedMovement.y);
        boolean hitZ = !MathHelper.approximatelyEquals(movement.z, collidedMovement.z);

        if (hitX || hitY || hitZ) {
            if (this.dataTracker.get(BOUNCES) == 0) {
                this.discard();
            }
            Vec3d currentVel = this.getVelocity();

            double newX = hitX ? -currentVel.x : currentVel.x;
            double newY = hitY ? -currentVel.y : currentVel.y;
            double newZ = hitZ ? -currentVel.z : currentVel.z;

            this.setVelocity(newX, newY, newZ);
            this.velocityDirty = true;
            if (this.dataTracker.get(BOUNCES) > 0) {
                this.dataTracker.set(BOUNCES, this.dataTracker.get(BOUNCES) - 1);
            }
        }


        this.setOnGround(false);
        this.verticalCollision = hitY;
        this.horizontalCollision = hitX || hitZ;

        this.getWorld().getProfiler().pop();
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.getWorld().getEntityCollisions(this, box.stretch(movement));
        // Just return the standard collision adjustment without the 'stepping' logic
        return movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.getWorld(), list);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return super.getDimensions(pose).scaled(this.dataTracker.get(SIZE));
    }
    public void onTrackedDataSet(TrackedData<?> data) {
        if (SIZE.equals(data)) {
            this.calculateDimensions();
        }

        super.onTrackedDataSet(data);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Bounces", this.dataTracker.get(BOUNCES) + 1);
        nbt.putFloat("Size", this.dataTracker.get(SIZE));
        nbt.putBoolean("Set", this.dataTracker.get(SET));
        nbt.putInt("WaitForNextHit", this.dataTracker.get(WAIT));
        nbt.putInt("Damage", this.dataTracker.get(DMG));
        this.calculateDimensions();
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.dataTracker.set(BOUNCES, nbt.getInt("Bounces") - 1);
        this.dataTracker.set(SIZE, nbt.getFloat("Size"));
        this.dataTracker.set(SET, nbt.getBoolean("Set"));
        this.dataTracker.set(WAIT, nbt.getInt("WaitForNextHit"));
        this.dataTracker.set(DMG, nbt.getInt("Damage"));
    }
}

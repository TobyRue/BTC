package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SuperHappyKillBallEntity extends ProjectileEntity {
    public double accelerationPower;
    public final AnimationState state = new AnimationState();

    public SuperHappyKillBallEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.1;
        this.noClip = false;
    }

    protected SuperHappyKillBallEntity(double x, double y, double z, World world) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.noClip = false;
    }

    public SuperHappyKillBallEntity(double x, double y, double z, Vec3d velocity, World world) {
        this(ModEntities.SUPER_HAPPY_KILL_BALL, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
        this.noClip = false;
//        this.setVelocityWithAcceleration(velocity, 2);
    }

    public final Vec3d getVelocityFromDirection(Direction direction, double speed) {
        return switch (direction) {
            case NORTH -> new Vec3d(0, 0, -speed);
            case EAST -> new Vec3d(speed, 0, 0);
            case SOUTH -> new Vec3d(0, 0, speed);
            case WEST -> new Vec3d(-speed, 0, 0);
            case UP -> new Vec3d(0, speed, 0);
            case DOWN -> new Vec3d(0, -speed, 0);
        };
    }

    public final Vec3d getVelocityFromDirection(Direction xDir, Direction yDir, Direction zDir, double speedX, double speedY, double speedZ) {
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
        this.state.startIfNotRunning(this.age);

        // 1. Basic Setup

        // 2. MOVE FIRST (Using your custom move method)
        // This is where the bounce happens
        Vec3d velocityBeforeMove = this.getVelocity();
        this.move(MovementType.SELF, velocityBeforeMove);

        // 3. APPLY ACCELERATION SECOND
        // Get the velocity again (it might have changed due to a bounce)
        Vec3d velocityAfterMove = this.getVelocity();
//        if (velocityAfterMove.lengthSquared() > 0.0001) {
//            // Accelerate in the direction we are NOW heading
//            velocityAfterMove = velocityAfterMove.add(velocityAfterMove.normalize().multiply(this.accelerationPower));
//        }

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
            this.getWorld().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.5, this.getZ(), 0, 0, 0);
        }

        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            this.discard();
        }
        if (!this.getWorld().isClient && this.getBlockY() < this.getWorld().getBottomY() - 30) {
            this.discard();
        }
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

    protected float getDrag() {
        return 0.95F;
    }

    protected float getDragInWater() {
        return 0.8F;
    }


    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    public static BlockHitResult getCollision(Entity entity, Predicate<Entity> predicate) {
        Vec3d velocity = entity.getVelocity();
        World world = entity.getWorld();
        Vec3d vec3d2 = entity.getPos();
        Vec3d vec3d = vec3d2.add(velocity);
        BlockHitResult hitResult = world.raycast(new RaycastContext(vec3d2, vec3d, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity));
        if (hitResult.getType() != HitResult.Type.MISS) {
            vec3d = hitResult.getPos();
        }
        return hitResult;
    }

//    @Override
//    protected void onBlockCollision(BlockState state) {
//        System.out.println("=== onBlockCollision CALLED ===");
//        System.out.println("Position: " + this.getPos());
//        System.out.println("Velocity BEFORE: " + this.getVelocity());
//
//        BlockPos pos = this.getBlockPos();
//        VoxelShape shape = state.getCollisionShape(this.getWorld(), pos);
//
//        if (shape.isEmpty()) {
//            System.out.println("EMPTY collision shape — skipping");
//            return;
//        }
//
//        var entityBox = this.getBoundingBox();
//        var blockBox = shape.getBoundingBox().offset(pos);
//
//        System.out.println("Entity BB: " + entityBox);
//        System.out.println("Block BB: " + blockBox);
//
//        if (!entityBox.intersects(blockBox)) {
//            System.out.println("NO INTERSECTION");
//            return;
//        }
//
//        System.out.println("INTERSECTION DETECTED");
//    }
    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity);
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }

    protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
        super.onDeflected(deflector, fromAttack);
        if (fromAttack) {
            this.accelerationPower = 0.1;
        } else {
            this.accelerationPower *= 0.5;
        }
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
            Vec3d currentVel = this.getVelocity();

            double newX = hitX ? -currentVel.x : currentVel.x;
            double newY = hitY ? -currentVel.y : currentVel.y;
            double newZ = hitZ ? -currentVel.z : currentVel.z;

            this.setVelocity(newX, newY, newZ);
            this.velocityDirty = true;
        }


        this.setOnGround(false);
        this.verticalCollision = hitY;
        this.horizontalCollision = hitX || hitZ;

        this.getWorld().getProfiler().pop();
    }

    @Override
    public float getTargetingMargin() {
        return 0.0F;
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        Box box = this.getBoundingBox();
        List<VoxelShape> list = this.getWorld().getEntityCollisions(this, box.stretch(movement));
        // Just return the standard collision adjustment without the 'stepping' logic
        return movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.getWorld(), list);
    }
}

package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.util.AABB;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Predicate;

public class HighEnergyPelletEntity extends ProjectileEntity {
    public double accelerationPower;

    public HighEnergyPelletEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.accelerationPower = 0.1;
    }

    protected HighEnergyPelletEntity(double x, double y, double z, World world) {
        this(ModEntities.HIGH_ENERGY_PELLET, world);
        this.setPosition(x, y, z);
    }

    public HighEnergyPelletEntity(double x, double y, double z, Vec3d velocity, World world) {
        this(ModEntities.HIGH_ENERGY_PELLET, world);
        this.setPosition(x, y, z);
        this.setVelocity(velocity);
//        this.setVelocityWithAcceleration(velocity, 2);
    }
    @Override
    public void tick() {
        super.tick();
        this.setPosition(this.getPos().add(this.getVelocity()));

        Vec3d velocity = this.getVelocity();

        if (velocity.lengthSquared() > 0.0001) {
            velocity = velocity.add(velocity.normalize().multiply(this.accelerationPower));
        }

        velocity = velocity.multiply(this.isTouchingWater() ? getDragInWater() : getDrag());
        this.setVelocity(velocity);

        double horizontalLength = velocity.horizontalLength();
        float pitch = (float) (MathHelper.atan2(velocity.getY(), horizontalLength) * (180.0 / Math.PI));
        float yaw = (float) (MathHelper.atan2(velocity.getX(), velocity.getZ()) * (180.0 / Math.PI));

        this.setRotation(yaw, pitch);

        this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), yaw, pitch);

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        this.hitOrDeflect(hitResult);
        if (this.getWorld().isClient) {
            this.getWorld().addParticle(
                    ParticleTypes.END_ROD,
                    this.getX(), this.getY(), this.getZ(),
                    0, 0, 0
            );
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
    }

    protected float getDrag() {
        return 0.95F;
    }

    protected float getDragInWater() {
        return 0.8F;
    }

    private void setVelocityWithAcceleration(Vec3d velocity, double accelerationPower) {
        this.setVelocity(velocity.normalize().multiply(accelerationPower));
        this.velocityDirty = true;
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
            vec3d = ((HitResult)hitResult).getPos();
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
    public boolean canHit() {
        return true;
    }
    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
    }
    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
    }

    protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
        super.onDeflected(deflector, fromAttack);
        if (fromAttack) {
            this.accelerationPower = 0.1;
        } else {
            this.accelerationPower *= 0.5;
        }
    }
}

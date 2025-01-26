package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WaterBlastEntity extends ProjectileEntity {

    public WaterBlastEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public WaterBlastEntity(LivingEntity user, World world, double x, double y, double z, Vec3d velocity) {
        super(ModEntities.WATER_BLAST, world);
        this.setPosition(x, y, z);
        this.setOwner(user);
        this.setVelocity(velocity);
    }

    @Override
    protected double getGravity() {
        return 0.07d;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        this.setPosition(this.getPos().add(this.getVelocity()));
        this.applyGravity();
        Vec3d d = this.getVelocity().normalize();
        float pitch = (float) Math.asin(-d.getY());
        //TODO something wrong with z axis / only faces completely correct facing south or at 0 degrees otherwise it rotates somewhat a few degrees towards 0 degrees, when first shot works and faces correct but on next tick it changes but it does try to readjust itself to its velocity try and fix me
        float yaw = (float) Math.atan2(d.getX(), d.getZ());
        this.setRotation(pitch, yaw);
        this.updateRotation();
        System.out.println("Pitch is, " + pitch + " with Yaw, " + yaw + " Velocity is, " + this.getVelocity() + " With Rotation, " + this.getRotationVector());
        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            }
            this.discard();
        } else {
            super.tick();
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.hitOrDeflect(hitResult);
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            }
        }
    }


    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        System.out.println("WaterBlastEntity hit a block at: " + blockHitResult.getBlockPos());
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }
        if (!this.getWorld().isClient) {
            this.discard();
        }
    }

    @Override
    public boolean canHit() {
        return true;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 2);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 100));
            livingEntity.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DROWNING), 200, 1));
        }
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }
        if (!this.getWorld().isClient()) {
            System.out.println("WaterBlastEntity hit an entity: " + entityHitResult.getEntity().getName().getString());
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        this.discard();
    }
}

package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.*;
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
        this.setRotation(user.headYaw, user.getPitch());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }


    @Override
    public void tick() {
        super.tick();
        this.setPosition(this.getPos().add(this.getVelocity()));
        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            this.discard();
        } else {
            super.tick();
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        if (hitResult.getType() != HitResult.Type.MISS) {
            this.hitOrDeflect(hitResult);
        }
    }


//TODO ADD PARTIALS
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        System.out.println("WaterBlastEntity hit a block at: " + blockHitResult.getBlockPos());

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
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 4);
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

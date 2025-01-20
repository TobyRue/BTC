package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
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

        Vec3d oldPos = this.getPos();
        this.move(MovementType.SELF, this.getVelocity());
        Vec3d newPos = this.getPos();

        // Check for block collision
        BlockHitResult blockHitResult = this.getWorld().raycast(new RaycastContext(
                oldPos, newPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this
        ));

//        if (blockHitResult.getType() == HitResult.Type.BLOCK) {
            this.onBlockHit(blockHitResult);
//            return;
//        }

        // Check for entity collision
        EntityHitResult entityHitResult = this.getEntityCollision(this.getPos(), this.getPos().add(this.getVelocity()));
        if (entityHitResult != null) {
            this.onEntityHit(entityHitResult);
            return;
        }

        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            this.discard();
        }
    }
    private EntityHitResult getEntityCollision(Vec3d start, Vec3d end) {
        return ProjectileUtil.getEntityCollision(
                this.getWorld(),
                this,
                start,
                end,
                this.getBoundingBox().stretch(this.getVelocity()).expand(1.0D),
                entity -> !entity.isSpectator() && entity.isAlive() && entity != this.getOwner()
        );
    }



    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        System.out.println("WaterBlastEntity hit a block at: " + blockHitResult.getBlockPos());
        // Add additional behavior if needed, e.g., destroy the entity or create effects
        if (!this.getWorld().isClient) {
            this.discard(); // Remove entity after collision with a block
        }
    }

    @Override
    public boolean canHit() {
        return true; // Ensures it can hit entities
    }
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.discard();
        }

    }

//    @Override
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
//    @Override
//    protected void onEntityHit(EntityHitResult entityHitResult) {
//        super.onEntityHit(entityHitResult);
//        System.out.println("WaterBlastEntity hit an entity: " + entityHitResult.getEntity().getName().getString());
//
//        // Create a projectile damage source
//        DamageSource damageSource = DamageSource.projectile(this, this.getOwner());
//        entityHitResult.getEntity().damage(damageSource, 5.0F); // Apply 5 damage
//
//        this.discard(); // Remove entity after collision
//    }

    @Override
    protected void onBlockCollision(BlockState state) {
        super.onBlockCollision(state);
        this.discard();
    }
}

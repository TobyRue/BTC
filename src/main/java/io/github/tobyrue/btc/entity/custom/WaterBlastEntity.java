package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.projectile.*;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WaterBlastEntity extends ProjectileEntity {

    private static double particalDelay = 10;

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
        World world = this.getWorld();

        Vec3d velocity = this.getVelocity();
        double horizontalLength = velocity.horizontalLength();

        float pitch = (float) (MathHelper.atan2(velocity.getY(), horizontalLength) * (180.0 / Math.PI));
        float yaw = (float) (MathHelper.atan2(velocity.getX(), velocity.getZ()) * (180.0 / Math.PI));

        this.setRotation(yaw, pitch);

        this.updatePositionAndAngles(this.getX(), this.getY(), this.getZ(), yaw, pitch);

        if (world.getRegistryKey() == World.NETHER) {
            if (particalDelay >= 1) {
                particalDelay--;
                world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F, 0.05000000074505806, (double) (MathHelper.nextBetween(random, -1.0F, 1.0F) * 0.083333336F));
            }
            this.discard();
        }
        if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            }
            this.discard();
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
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }
        BlockPos hitPos = blockHitResult.getBlockPos();
        Direction hitSide = blockHitResult.getSide();

        if (hitSide == Direction.UP) {  // Ensure it hits the top of the block
            World world = this.getWorld();
            BlockState state = world.getBlockState(hitPos);

            if (state.getBlock() instanceof AbstractCauldronBlock) {
                if (state.isOf(Blocks.WATER_CAULDRON)) {
                    int currentLevel = state.get(LeveledCauldronBlock.LEVEL);

                    if (currentLevel < 3) {
                        world.setBlockState(hitPos, state.with(LeveledCauldronBlock.LEVEL, currentLevel + 1));
                    }
                } else if (state.isOf(Blocks.CAULDRON)) {
                    world.setBlockState(hitPos, Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, 1));
                }

                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, hitPos);
            } else {
                BlockPos abovePos = hitPos.up();
                if (world.getBlockState(abovePos).isReplaceable() && world.getRegistryKey() != World.NETHER) {
                    BlockState water = Blocks.WATER.getDefaultState().with(FluidBlock.LEVEL, 8);
                    world.setBlockState(abovePos, water);
                }
            }
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
            livingEntity.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(ModStatusEffects.DROWNING), 200, 1));
            // Extinguish fire if the entity is burning
            if (livingEntity.isOnFire()) {
                livingEntity.extinguish();
            }
        }
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(BTC.WATER_BLAST, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }
        if (!this.getWorld().isClient()) {
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

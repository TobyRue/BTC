package io.github.tobyrue.btc.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static net.minecraft.block.PillarBlock.AXIS;

public class MineEntity extends Entity {
    private static final TrackedData<Float> PROXIMITY =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> POWER =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> DEFUSED =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private static final ExplosionBehavior TELEPORTED_EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return !state.isOf(Blocks.NETHER_PORTAL) && super.canDestroyBlock(explosion, world, pos, state, power);
        }

        public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            return blockState.isOf(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlastResistance(explosion, world, pos, blockState, fluidState);
        }
    };
    private boolean teleported;

    public MineEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(PROXIMITY, 1f);
        builder.add(POWER, 4f);
        builder.add(DEFUSED, false);
    }
    public float getPower() {
        return this.dataTracker.get(POWER);
    }
    public void setPower(float power) {
        this.dataTracker.set(POWER, power);
    }
    public float getProximity() {
        return this.dataTracker.get(PROXIMITY);
    }
    public void setProximity(float proximity) {
        this.dataTracker.set(PROXIMITY, proximity);
    }

    public boolean isDefused() {
        return this.dataTracker.get(DEFUSED);
    }

    public void setDefused(boolean defused) {
        this.dataTracker.set(DEFUSED, defused);
    }
    @Override
    public void tick() {
        super.tick();
        var pos = this.getBlockPos();
        this.setNoGravity(false);
        for(Direction direction : Direction.values()) {
            var blockOffset = this.getWorld().getBlockState(pos.offset(direction));
            if (blockOffset.getBlock() instanceof ChainBlock) {
                if (blockOffset.get(AXIS) == direction.getAxis()) {
                    this.setPosition(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5));
                    this.setNoGravity(true);
                    break;
                }
            }
        }


        FluidState fluidState = this.getWorld().getFluidState(pos);
        Vec3d velocity = this.getVelocity();

        if (fluidState.isIn(FluidTags.WATER) && !hasNoGravity()) {
            BlockPos.Mutable surfacePos = this.getBlockPos().mutableCopy();
            while (this.getWorld().getFluidState(surfacePos.up()).isIn(FluidTags.WATER)) {
                surfacePos.move(Direction.UP);
                if (surfacePos.getY() > this.getWorld().getTopY()) break;
            }
            float topWaterHeight = this.getWorld().getFluidState(surfacePos).getHeight(this.getWorld(), surfacePos);
            double actualSurfaceY = (double)surfacePos.getY() + topWaterHeight;
            double targetY = actualSurfaceY - 0.75;
            double springForce = (targetY - this.getY()) * 0.01;
            double currentDamping = 0.85;

            if (Math.abs(this.getY() - targetY) < 0.15 && velocity.y > 0) {
                currentDamping = 0.4;
            }

            this.setVelocity(
                    Math.signum(velocity.x) * Math.max(0, Math.abs(velocity.x) - 0.005),
                    (velocity.y + springForce) * currentDamping,
                    Math.signum(velocity.z) * Math.max(0, Math.abs(velocity.z) - 0.005)
            );
        } else if (!hasNoGravity()) {
            if (this.isOnGround()) {
                BlockState belowState = this.getWorld().getBlockState(this.getBlockPos().down());
                float friction = belowState.getBlock().getSlipperiness() * 0.91F;
                this.setVelocity(velocity.x * friction, velocity.y, velocity.z * friction);
            } else {
                this.applyGravity();
                this.setVelocity(this.getVelocity().multiply(0.98));
            }
        }

        this.move(MovementType.SELF, this.getVelocity());

        // Proximity/Explosion Logic
        if (!this.getWorld().isClient && !isDefused()) {
            Box proximityBox = this.getBoundingBox().expand(getProximity());
            List<? extends Entity> nearbyEntities = this.getWorld().getOtherEntities(this, proximityBox, MineEntity::doesEntityTrigger);
            if (!nearbyEntities.isEmpty()) {
                explode();
            }
        }
    }

    protected static boolean doesEntityTrigger(final Entity entity) {
        return !(entity instanceof FishingBobberEntity) && !(entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator())) && (
                entity instanceof LivingEntity
                || entity instanceof ProjectileEntity
                || entity instanceof ItemEntity
                || entity instanceof VehicleEntity
                || entity instanceof FallingBlockEntity
        );
    }

    private void applyWaterBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.9900000095367432, vec3d.y + (double)(vec3d.y < 0.05999999865889549 ? 5.0E-5F : 0.0F), vec3d.z * 0.9900000095367432);
    }

    private void applyLavaBuoyancy() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x * 0.949999988079071, vec3d.y + (double)(vec3d.y < 0.05999999865889549 ? 5.0E-4F : 0.0F), vec3d.z * 0.949999988079071);
    }

    private void explode() {
        this.discard();
        this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), this.teleported ? TELEPORTED_EXPLOSION_BEHAVIOR : null, this.getX(), this.getBodyY(0.0625), this.getZ(), getPower(), false, World.ExplosionSourceType.TNT);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Nullable
    @Override
    public ItemStack getPickBlockStack() {
        return super.getPickBlockStack();
    }

    @Override
    protected double getGravity() {
        return 0.07d;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Proximity")) {
            setProximity(nbt.getFloat("Proximity"));
        }
        if (nbt.contains("Power")) {
            setPower(nbt.getFloat("Power"));
        }
        if (nbt.contains("Defused")) {
            setDefused(nbt.getBoolean("Defused"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("Proximity", getProximity());
        nbt.putFloat("Power", getPower());
        nbt.putBoolean("Defused", isDefused());
    }



    private void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    @Nullable
    public Entity teleportTo(TeleportTarget teleportTarget) {
        Entity entity = super.teleportTo(teleportTarget);
        if (entity instanceof MineEntity tntEntity) {
            tntEntity.setTeleported(true);
        }
        return entity;
    }
}

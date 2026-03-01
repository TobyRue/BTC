package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChainBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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

import java.util.List;
import java.util.Optional;

import static net.minecraft.block.PillarBlock.AXIS;

public class MineEntity extends Entity {
    private int oxidationTicks = 0;
    private int nextOxidationDelay = -1;
    private static final int MINIMUM_OXIDATION_TICKS = 20 * 60 * 6; // 6 minutes
    private static final int RANDOM_MIN_DELAY = 20 * 30;        // 30 seconds
    private static final int RANDOM_MAX_DELAY = 20 * 60 * 4;       // 4 minutes

    private static final TrackedData<Float> PROXIMITY =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Float> POWER =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Boolean> DEFUSED =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Byte> OXIDATION_STATE =
            DataTracker.registerData(MineEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> WAXED_STATE =
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
        builder.add(OXIDATION_STATE, (byte) CopperGolemEntity.Oxidation.UNOXIDIZED.ordinal());
        builder.add(WAXED_STATE, false);
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

    public void setOxidation(CopperGolemEntity.Oxidation oxidation) {
        this.dataTracker.set(OXIDATION_STATE, (byte) oxidation.ordinal());
    }

    public CopperGolemEntity.Oxidation getOxidation() {
        return CopperGolemEntity.Oxidation.values()[this.dataTracker.get(OXIDATION_STATE)];
    }

    public boolean isWaxed() {
        return this.dataTracker.get(WAXED_STATE);
    }

    public void setWaxed(boolean waxed) {
        this.dataTracker.set(WAXED_STATE, waxed);
    }

    public float getProximityMultiplier() {
        return switch (this.getOxidation()) {
            case UNOXIDIZED -> 1.0f;
            case EXPOSED -> 0.9f;
            case WEATHERED -> 0.825f;
            case OXIDIZED -> 0.775f;
        };
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

        if (!this.getWorld().isClient && !isDefused()) {
            Box proximityBox = this.getBoundingBox().expand(getProximity() * getProximityMultiplier());
            List<? extends Entity> nearbyEntities = this.getWorld().getOtherEntities(this, proximityBox, MineEntity::doesEntityTrigger);
            if (!nearbyEntities.isEmpty()) {
                explode();
            }
        }
        if (!this.getWorld().isClient) {

            if (!isWaxed() && this.getOxidation() != CopperGolemEntity.Oxidation.OXIDIZED ) {
                oxidationTicks++;
                if (oxidationTicks >= MINIMUM_OXIDATION_TICKS) {
                    if (nextOxidationDelay == -1) {
                        nextOxidationDelay = RANDOM_MIN_DELAY + random.nextInt(RANDOM_MAX_DELAY - RANDOM_MIN_DELAY + 1);
                    }

                    if (oxidationTicks >= MINIMUM_OXIDATION_TICKS + nextOxidationDelay) {
                        advanceOxidation();

                        oxidationTicks = 0;
                        nextOxidationDelay = -1;
                    }
                }
            }
        }
    }

    private void advanceOxidation() {
        CopperGolemEntity.Oxidation currentState = this.getOxidation();
        CopperGolemEntity.Oxidation nextState = switch (currentState) {
            case UNOXIDIZED -> CopperGolemEntity.Oxidation.EXPOSED;
            case EXPOSED -> CopperGolemEntity.Oxidation.WEATHERED;
            case WEATHERED, OXIDIZED -> CopperGolemEntity.Oxidation.OXIDIZED;
        };
        this.setOxidation(nextState);
    }

    private void lowerOxidation() {
        CopperGolemEntity.Oxidation currentState = this.getOxidation();
        CopperGolemEntity.Oxidation nextState = switch (currentState) {
            case OXIDIZED -> CopperGolemEntity.Oxidation.WEATHERED;
            case WEATHERED -> CopperGolemEntity.Oxidation.EXPOSED;
            case EXPOSED, UNOXIDIZED -> CopperGolemEntity.Oxidation.UNOXIDIZED;
        };
        this.setOxidation(nextState);
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

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.HONEYCOMB)) {
            if (!this.isWaxed()) {
                this.setWaxed(true);
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.PLAYERS, 1.0F, 1.0F);

                if (!player.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }

                return ActionResult.SUCCESS;
            }
        } else if (itemStack.isIn(ItemTags.AXES)) {
            if (this.isWaxed()) {
                this.setWaxed(false);
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().creativeMode) {
                    itemStack.damage(3, player, EquipmentSlot.MAINHAND);
                }
                return ActionResult.SUCCESS;
            } else if (this.getOxidation() != CopperGolemEntity.Oxidation.UNOXIDIZED) {
                lowerOxidation();
                this.getWorld().playSound(this, this.getBlockPos(), SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                if (!player.getAbilities().creativeMode) {
                    itemStack.damage(3, player, EquipmentSlot.MAINHAND);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
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
        if (nbt.contains("OxidationState", NbtElement.BYTE_TYPE)) {
            this.setOxidation(CopperGolemEntity.Oxidation.values()[nbt.getByte("OxidationState")]);
        }
        if (nbt.contains("Waxed")) {
            this.setWaxed(nbt.getBoolean("Waxed"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("Proximity", getProximity());
        nbt.putFloat("Power", getPower());
        nbt.putBoolean("Defused", isDefused());
        nbt.putByte("OxidationState", (byte) this.getOxidation().ordinal());
        nbt.putBoolean("Waxed", this.isWaxed());
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

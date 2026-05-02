package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.WaxedCopperFanBlock;
import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FanBlockEntity extends BlockEntity implements BlockEntityTicker<FanBlockEntity> {
    private double FAR_RADIUS = 1;
    private double DEPTH = 3;
    private double BASE_RADIUS = 0.5;
    private float fanSpeed = 0f;
    public float visualRotation = 0f;

    public double getFAR_RADIUS() {
        return FAR_RADIUS;
    }

    private static final float MAX_SPEED = 0.5f;

    public double getDEPTH() {
        return DEPTH;
    }

    private static final float ACCELERATION = 0.02f;

    public FanBlockEntity(BlockPos pos, BlockState state) {
        super(switch (state.getBlock()) {
            case Block block when state.isOf(ModBlocks.COPPER_TRIAL_FAN) -> ModBlockEntities.COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.EXPOSED_COPPER_TRIAL_FAN) -> ModBlockEntities.EXPOSED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.WEATHERED_COPPER_TRIAL_FAN) -> ModBlockEntities.WEATHERED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN) -> ModBlockEntities.OXIDIZED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.WAXED_COPPER_TRIAL_FAN) -> ModBlockEntities.WAXED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN) -> ModBlockEntities.WAXED_EXPOSED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN) -> ModBlockEntities.WAXED_WEATHERED_COPPER_FAN_BLOCK_ENTITY;
            case Block block when state.isOf(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN) -> ModBlockEntities.WAXED_OXIDIZED_COPPER_FAN_BLOCK_ENTITY;
            default -> throw new IllegalStateException("Unexpected value: " + state.getBlock());
        }, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FanBlockEntity blockEntity) {
        var percentSpeed = fanSpeed / MAX_SPEED;
        if (state.get(WaxedCopperFanBlock.POWERED)) {
            fanSpeed = Math.min(MAX_SPEED, fanSpeed + ACCELERATION);
        } else {
            fanSpeed = Math.max(0f, fanSpeed - (ACCELERATION * 0.5f));
        }
        if (state.get(WaxedCopperFanBlock.POWERED) || fanSpeed > 0.1f) {
            Direction facing = state.get(WaxedCopperFanBlock.FACING);
            Vec3d direction = Vec3d.of(facing.getVector());
            boolean isPulling = state.get(WaxedCopperFanBlock.MODE) == WaxedCopperFanBlock.FanMode.PULL;
            double forceStrength = 0.5 * percentSpeed;
            Vec3d forceVec = isPulling ? direction.multiply(-forceStrength) : direction.multiply(forceStrength);
            double maxSpeed = 0.6;

            Vec3d fanFaceCenter = pos.toCenterPos().add(direction.multiply(0.5));

            for (Entity entity : WaxedCopperFanBlock.getEntitiesInCone(state, world, pos, BASE_RADIUS, FAR_RADIUS, DEPTH * percentSpeed)) {
                if ((entity instanceof PlayerEntity player && !(player.isCreative() || player.isSpectator())) || (entity instanceof Entity && !(entity instanceof PlayerEntity))) {
                    Vec3d currentVel = entity.getVelocity();
                    double speedInDirection = currentVel.dotProduct(direction);
                    boolean canApply = isPulling ? speedInDirection > -maxSpeed : speedInDirection < maxSpeed;

                    if (canApply) {
                        entity.setVelocity(currentVel.add(forceVec));
                        entity.velocityModified = true;
                    }

                    Vec3d entityCenter = entity.getBoundingBox().getCenter();

                    if (isPulling) {
                        Vec3d coneEnd = fanFaceCenter.add(direction.multiply(DEPTH * percentSpeed));
                        scanForEffects(world, entity, entityCenter, coneEnd);
                    } else {
                        scanForEffects(world, entity, fanFaceCenter, entityCenter);
                    }
                }
            }
            for (int x = 0; x < ((this.BASE_RADIUS * this.getFAR_RADIUS()) * 2); x++) {
                spawnGustParticles(world, pos, direction, state.get(WaxedCopperFanBlock.MODE));
            }
        }

        if (world.isClient) {
            visualRotation += fanSpeed;
        }
    }

    private void scanForEffects(World world, Entity entity, Vec3d source, Vec3d target) {
        Vec3d path = target.subtract(source);
        double distance = path.length();
        Vec3d unitDir = path.normalize();

        for (double d = 0; d < distance; d += 0.5) {
            BlockPos checkPos = BlockPos.ofFloored(source.add(unitDir.multiply(d)));
            BlockState checkState = world.getBlockState(checkPos);

            if (applyElementalEffect(entity, checkState)) {
                break;
            }
        }
    }

    private boolean applyElementalEffect(Entity entity, BlockState state) {
        if (state.isOf(Blocks.FIRE) ||
                state.isOf(Blocks.SOUL_FIRE) ||
                state.getFluidState().isIn(FluidTags.LAVA)) {
            entity.setOnFireFor(5);
            return true;
        }
        else if (state.getFluidState().isIn(FluidTags.WATER)) {
            entity.extinguish();
            if (entity instanceof LivingEntity living && living.hurtByWater()) {
                living.damage(entity.getDamageSources().magic(), 1.0f);
            }
            return true;
        }
        return false;
    }

    private void spawnGustParticles(World world, BlockPos pos, Vec3d direction, WaxedCopperFanBlock.FanMode mode) {
        var percentSpeed = fanSpeed / MAX_SPEED;
        Vec3d start = pos.toCenterPos().add(direction.multiply(0.5));
        Vec3d ortho = getOrthogonalVector(direction);
        Vec3d secondaryOrtho = direction.crossProduct(ortho);

        double angle = world.random.nextDouble() * Math.PI * 2;
        double rStartMult = Math.sqrt(world.random.nextDouble());
        double rStart = rStartMult * BASE_RADIUS;

        Vec3d startOffset = ortho.multiply(Math.cos(angle) * rStart).add(secondaryOrtho.multiply(Math.sin(angle) * rStart));
        double rEnd = rStartMult * FAR_RADIUS;
        Vec3d endOffset = ortho.multiply(Math.cos(angle) * rEnd).add(secondaryOrtho.multiply(Math.sin(angle) * rEnd));

        Vec3d targetPoint = start.add(direction.multiply(DEPTH)).add(endOffset);
        Vec3d actualStart = start.add(startOffset);

        BlockHitResult hit = world.raycast(new RaycastContext(
                actualStart, targetPoint,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                ShapeContext.absent()
        ));

        double fullPathLength = targetPoint.distanceTo(actualStart);
        double maxTravel = hit.getType() == HitResult.Type.MISS ? fullPathLength : hit.getPos().distanceTo(actualStart);
        if (maxTravel < 0.1) return;

        ParticleEffect elementalParticle = null;
        double elementDist = -1;
        Vec3d pathDir = targetPoint.subtract(actualStart).normalize();

        for (double d = 0; d < maxTravel; d += 0.5) {
            BlockPos checkPos = BlockPos.ofFloored(actualStart.add(pathDir.multiply(d)));
            BlockState checkState = world.getBlockState(checkPos);
            elementalParticle = getElementalParticle(checkState);
            if (elementalParticle != null) {
                elementDist = d;
                break;
            }
        }

        int maxAge = 25;
        double dragCompensation = 1.1;
        double finalMultiplier = ((1.0 / (double) maxAge) * dragCompensation) * percentSpeed;

        Vec3d windSpawn, windTravel;
        if (mode == WaxedCopperFanBlock.FanMode.PULL) {
            windSpawn = actualStart.add(pathDir.multiply(maxTravel));
            windTravel = actualStart.subtract(windSpawn);
        } else {
            windSpawn = actualStart;
            windTravel = pathDir.multiply(maxTravel);
        }
        Vec3d windVel = windTravel.multiply(finalMultiplier);
        world.addParticle(ParticleTypes.CLOUD, windSpawn.x, windSpawn.y, windSpawn.z, windVel.x, windVel.y, windVel.z);

        if (elementalParticle != null) {
            Vec3d elemSpawn, elemTravel;
            if (mode == WaxedCopperFanBlock.FanMode.PULL) {
                elemSpawn = actualStart.add(pathDir.multiply(elementDist));
                elemTravel = actualStart.subtract(elemSpawn);
            } else {
                elemSpawn = actualStart.add(pathDir.multiply(elementDist));
                elemTravel = pathDir.multiply(maxTravel - elementDist);
            }
            Vec3d elemVel = elemTravel.multiply(finalMultiplier);
            world.addParticle(elementalParticle, elemSpawn.x, elemSpawn.y, elemSpawn.z, elemVel.x, elemVel.y, elemVel.z);
        }
    }

    private ParticleEffect getElementalParticle(BlockState state) {
        if (!state.getFluidState().isEmpty()) {
            if (state.getFluidState().isIn(FluidTags.LAVA)) return ParticleTypes.FLAME;
            if (state.getFluidState().isIn(FluidTags.WATER)) return ParticleTypes.FISHING;
        }
        if (state.isOf(Blocks.FIRE)) return ParticleTypes.FLAME;
        if (state.isOf(Blocks.SOUL_FIRE)) return ParticleTypes.SOUL_FIRE_FLAME;
        return null;
    }

    private Vec3d getOrthogonalVector(Vec3d dir) {
        Vec3d helper = Math.abs(dir.y) < 0.9 ? new Vec3d(0, 1, 0) : new Vec3d(1, 0, 0);
        return dir.crossProduct(helper).normalize();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putDouble("FarRadius", FAR_RADIUS);
        nbt.putDouble("BaseRadius", BASE_RADIUS);
        nbt.putDouble("Depth", DEPTH);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("FarRadius", NbtElement.DOUBLE_TYPE)) {
            FAR_RADIUS = nbt.getDouble("FarRadius");
            BASE_RADIUS = nbt.getDouble("BaseRadius");
            DEPTH = nbt.getDouble("Depth");

            if (this.world != null && !this.world.isClient) {
                this.world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public float getFanSpeed() {
        return fanSpeed;
    }

    public void setFanSpeed(float fanSpeed) {
        this.fanSpeed = fanSpeed;
    }
}

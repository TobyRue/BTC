package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.DungeonDoorBlock;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import io.github.tobyrue.btc.block.OminousBeaconBlock.BeaconMode;
import io.github.tobyrue.btc.block.OminousReflectorBlock;
import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OminousBeaconBlockEntity extends BlockEntity implements BlockEntityTicker<OminousBeaconBlockEntity> {

    public static class BeamSegmentPath {
        private final BlockPos startPos;
        private final Direction direction;
        private final int length;
        private final BeaconBlockEntity.BeamSegment segment;

        public BeamSegmentPath(BlockPos startPos, Direction direction, int length, BeaconBlockEntity.BeamSegment segment) {
            this.startPos = startPos;
            this.direction = direction;
            this.length = length;
            this.segment = segment;
        }

        public BlockPos getStartPos() {
            return startPos;
        }

        public Direction getDirection() {
            return direction;
        }

        public int getLength() {
            return length;
        }

        public BeaconBlockEntity.BeamSegment getSegment() {
            return segment;
        }
    }

    private int maxDistance = 16;
    private int propagationDelay = 0;
    private int tickCounter = 0;

    private final List<BeamSegmentPath> beamPaths = new ArrayList<>();

    public OminousBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, pos, state);
    }

    public List<BeamSegmentPath> getBeamPaths() {
        return this.beamPaths;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getPropagationDelay() {
        return this.propagationDelay;
    }

    public void setPropagationDelay(int propagationDelay) {
        this.propagationDelay = propagationDelay;
    }

    private void updateBeam(World world, BlockPos pos, BlockState state) {
        this.beamPaths.clear();

        BeaconMode myMode = state.get(OminousBeaconBlock.BEACON_MODE);
        boolean myPower = state.get(OminousBeaconBlock.POWERED);
        Direction initialDirection = state.get(OminousBeaconBlock.FACING);

        if (myMode == BeaconMode.SENDER && !myPower) {
            return;
        }

        boolean[] receiverPoweredState = new boolean[]{false};
        Set<BlockPos> visitedReflectors = new HashSet<>();

        traceBeam(world, pos, initialDirection, this.maxDistance, myMode, myPower, visitedReflectors, receiverPoweredState);

        if (!world.isClient) {
            if (myMode == BeaconMode.RECEIVER) {
                if (receiverPoweredState[0] != myPower) {
                    world.setBlockState(pos, state.with(OminousBeaconBlock.POWERED, receiverPoweredState[0]), 3);
                    world.updateNeighbors(pos, state.getBlock());
                }
            } else if (myMode == BeaconMode.DECORATIVE && !myPower) {
                world.setBlockState(pos, state.with(OminousBeaconBlock.POWERED, true), 3);
            }
        }
    }

    private void traceBeam(World world, BlockPos startPos, Direction dir, int remainingRange, BeaconMode originalMode, boolean originalPower, Set<BlockPos> visitedReflectors, boolean[] receiverPoweredState) {
        int length = 0;

        for (int l = 1; l <= remainingRange; l++) {
            BlockPos currentPos = startPos.offset(dir, l);
            BlockState currentState = world.getBlockState(currentPos);

            if (currentState.isIn(BTC.STOPS_OMINOUS_BEACON) || (currentState.getBlock() instanceof DungeonDoorBlock door && !currentState.get(DungeonDoorBlock.OPEN))) {
                break;
            }

            length = l;

            if (currentState.getBlock() == ModBlocks.OMINOUS_REFLECTOR) {
                OminousReflectorBlock.ReflectorMode reflectorMode = currentState.get(OminousReflectorBlock.REFLECTOR_MODE);
                Direction reflectorFacing = currentState.get(OminousReflectorBlock.FACING);

                if (!visitedReflectors.contains(currentPos)) {
                    visitedReflectors.add(currentPos);
                    int updatedRange = (remainingRange - l) + this.maxDistance;

                    if (reflectorMode == OminousReflectorBlock.ReflectorMode.ALL_IN) {
                        if (dir != reflectorFacing) {
                            this.beamPaths.add(new BeamSegmentPath(startPos, dir, length, new BeaconBlockEntity.BeamSegment(0xFFFFFF)));
                            applyBeamEffects(world, startPos, dir, length);
                            return;
                        }
                    } else if (reflectorMode == OminousReflectorBlock.ReflectorMode.ALL_OUT) {
                        if (dir.getOpposite() == reflectorFacing) {
                            this.beamPaths.add(new BeamSegmentPath(startPos, dir, length, new BeaconBlockEntity.BeamSegment(0xFFFFFF)));
                            applyBeamEffects(world, startPos, dir, length);

                            for (Direction outDir : Direction.values()) {
                                if (outDir != reflectorFacing.getOpposite()) {
                                    BlockPos targetCheck = currentPos.offset(outDir);
                                    BlockState targetState = world.getBlockState(targetCheck);

                                    if (!targetState.isIn(BTC.STOPS_OMINOUS_BEACON)) {
                                        traceBeam(world, currentPos, outDir, updatedRange, originalMode, originalPower, visitedReflectors, receiverPoweredState);
                                    }
                                }
                            }
                            return;
                        }
                    }
                }
                break;
            }

            if (currentState.getBlock() == ModBlocks.OMINOUS_BEACON && currentState.get(OminousBeaconBlock.FACING) == dir.getOpposite()) {
                BeaconMode targetMode = currentState.get(OminousBeaconBlock.BEACON_MODE);
                boolean targetPower = currentState.get(OminousBeaconBlock.POWERED);

                if (originalMode == BeaconMode.RECEIVER && targetMode == BeaconMode.SENDER) {
                    receiverPoweredState[0] = targetPower;
                } else if (originalMode == BeaconMode.SENDER && targetMode == BeaconMode.RECEIVER) {
                    receiverPoweredState[0] = originalPower;
                }

                break;
            }
        }

        if (length > 0) {
            this.beamPaths.add(new BeamSegmentPath(startPos, dir, length, new BeaconBlockEntity.BeamSegment(0xFFFFFF)));
            applyBeamEffects(world, startPos, dir, length);
        }
    }

    private void applyBeamEffects(World world, BlockPos startPos, Direction dir, int length) {
        if (world.isClient) return;

        for (int l = 1; l < length; l++) {
            BlockPos offsetPos = startPos.offset(dir, l);
            BlockState offsetState = world.getBlockState(offsetPos);
            if (offsetState.getOpacity(world, offsetPos) < 15 || offsetState.isIn(BTC.OMINOUS_BEACON_IGNORES)) {
                continue;
            } else if (!offsetState.isIn(BTC.STOPS_OMINOUS_BEACON)) {
                world.breakBlock(offsetPos, true);
            }
        }

        Box beamBox = new Box(startPos.toCenterPos(), startPos.offset(dir, length).toCenterPos()).expand(0.5);
        world.getNonSpectatingEntities(TrialCubeEntity.class, beamBox).forEach(Entity::kill);

        world.getNonSpectatingEntities(LivingEntity.class, beamBox)
                .forEach(entity -> {
                    entity.damage(ModDamageTypes.of(world, ModDamageTypes.BEACON_BURN), 2.0f);
                    entity.setOnFireFor(3);
                });
    }

    public void tick(World world, BlockPos pos, BlockState state, OminousBeaconBlockEntity blockEntity) {
        if (this.propagationDelay > 0) {
            this.tickCounter++;
            if (this.tickCounter < this.propagationDelay) {
                return;
            }
            this.tickCounter = 0;
        }

        blockEntity.updateBeam(world, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("MaxDistance")) {
            this.maxDistance = nbt.getInt("MaxDistance");
        }
        if (nbt.contains("PropagationDelay")) {
            this.propagationDelay = nbt.getInt("PropagationDelay");
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("MaxDistance", this.maxDistance);
        nbt.putInt("PropagationDelay", this.propagationDelay);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }
}
package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.DungeonDoorBlock;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import io.github.tobyrue.btc.block.OminousBeaconBlock.BeaconMode;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlockEntityRenderer;
import net.minecraft.block.Block;
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
import java.util.List;

public class OminousBeaconBlockEntity extends BlockEntity implements BlockEntityTicker<OminousBeaconBlockEntity> {
    static final int MAX_LENGTH = 32;
    private int beamLength = 0;
    private final List<BeaconBlockEntity.BeamSegment> beamSegments = new ArrayList<>();

    public OminousBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, pos, state);
    }

    public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
        return this.beamSegments;
    }

    public int getBeamLength() {
        return this.beamLength;
    }

    private void updateBeam(World world, BlockPos pos, BlockState state) {
        this.beamSegments.clear();

        BeaconMode myMode = state.get(OminousBeaconBlock.BEACON_MODE);
        boolean myPower = state.get(OminousBeaconBlock.POWERED);
        var direction = state.get(OminousBeaconBlock.FACING);

        this.beamLength = 0;
        boolean shouldBePoweredBySender = false;

        for (int l = 1; l < MAX_LENGTH + 2; l++) {
            var offsetPos = pos.offset(direction, l);
            var offsetState = world.getBlockState(offsetPos);

            if (offsetState.isIn(BTC.STOPS_OMINOUS_BEACON) || (offsetState.getBlock() instanceof DungeonDoorBlock door && !offsetState.get(DungeonDoorBlock.OPEN))) {
                this.beamLength = 0;
                break;
            }

            if (offsetState.getBlock() == ModBlocks.OMINOUS_BEACON && offsetState.get(OminousBeaconBlock.FACING) == direction.getOpposite()) {
                BeaconMode targetMode = offsetState.get(OminousBeaconBlock.BEACON_MODE);
                boolean targetPower = offsetState.get(OminousBeaconBlock.POWERED);

                if (canConnect(myMode, myPower, targetMode, targetPower)) {
                    this.beamLength = l;

                    boolean isMaster = false;
                    if (myMode == BeaconMode.SENDER && targetMode == BeaconMode.RECEIVER) {
                        isMaster = true;
                    } else if (myMode == targetMode) {
                        isMaster = (pos.getX() < offsetPos.getX() || pos.getY() < offsetPos.getY() || pos.getZ() < offsetPos.getZ());
                    }

                    if (isMaster && this.beamLength > 0) {
                        this.beamSegments.add(new BeaconBlockEntity.BeamSegment(0xFFFFFF));
                    }

                    if (myMode == BeaconMode.RECEIVER && targetMode == BeaconMode.SENDER) {
                        shouldBePoweredBySender = targetPower;
                    }
                }
                break;
            }
        }

        if (myMode == BeaconMode.RECEIVER) {
            if (shouldBePoweredBySender != myPower) {
                world.setBlockState(pos, state.with(OminousBeaconBlock.POWERED, shouldBePoweredBySender), 3);
                world.updateNeighbors(pos, state.getBlock());
            }
        } else if (myMode == BeaconMode.DECORATIVE && !myPower) {
            world.setBlockState(pos, state.with(OminousBeaconBlock.POWERED, true), 3);
        }

        for (int l = 1; l < this.beamLength; l++) {
            var offsetPos = pos.offset(direction, l);
            var offsetState = world.getBlockState(offsetPos);
            if (offsetState.getOpacity(world, offsetPos) < 15 || offsetState.isIn(BTC.OMINOUS_BEACON_IGNORES)) {
                continue;
            } else if (!offsetState.isIn(BTC.STOPS_OMINOUS_BEACON)) {
                world.breakBlock(offsetPos, true);
            }
        }

        if (this.beamLength > 0) {
            Box beamBox = new Box(pos.toCenterPos(), pos.offset(direction, this.beamLength).toCenterPos()).expand(0.5);
            world.getNonSpectatingEntities(TrialCubeEntity.class, beamBox).forEach(Entity::kill);
            world.getNonSpectatingEntities(LivingEntity.class, beamBox)
                    .forEach(entity -> entity.damage(ModDamageTypes.of(world, ModDamageTypes.BEACON_BURN), 2.0f));

            world.getNonSpectatingEntities(LivingEntity.class, beamBox)
                    .forEach(entity -> entity.setOnFireFor(3));
        }
    }

    private boolean canConnect(BeaconMode self, boolean selfPowered, BeaconMode target, boolean targetPowered) {
        if (self == BeaconMode.DECORATIVE && target == BeaconMode.DECORATIVE) {
            return true;
        }
        boolean effectiveSelfPowered = (self == BeaconMode.DECORATIVE) || selfPowered;
        boolean effectiveTargetPowered = (target == BeaconMode.DECORATIVE) || targetPowered;

        if (self == BeaconMode.SENDER && target == BeaconMode.SENDER) {
            return effectiveSelfPowered || effectiveTargetPowered;
        }
        if (self == BeaconMode.RECEIVER && target == BeaconMode.RECEIVER) {
            return true;
        }
        if (self == BeaconMode.SENDER) {
            return effectiveSelfPowered;
        } else {
            return effectiveTargetPowered;
        }
    }

    public void tick(World world, BlockPos pos, BlockState state, OminousBeaconBlockEntity blockEntity) {
        blockEntity.updateBeam(world, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
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
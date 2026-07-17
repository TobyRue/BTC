package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireOperatorHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WireBlockEntity extends BlockEntity implements IDungeonWire, IWireDelayHelper, IWireConnectionHelper, IWireOperatorHelper, IOnBlockUpdate {
    private WireBlock.Operator operator = WireBlock.Operator.OR;
    private int delay = 0;
    private final Map<Direction, WireBlock.ConnectionType> connections = new HashMap<>();
    private boolean scheduledPower = false;

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.NONE);
        }
    }

    public void setPower(boolean powered) {
        if (world == null || world.isClient) return;
        var state = this.getWorld().getBlockState(this.getPos());

        final boolean currentlyPowered = isPowered(state);


        if (currentlyPowered != powered) {
            world.setBlockState(pos, getCachedState().with(WireBlock.POWERED, powered), Block.NOTIFY_ALL);
            for (Direction dir : Direction.values()) {
                WireBlock.ConnectionType conn = connections.get(dir);
                BlockPos neighborPos = pos.offset(dir);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (conn == WireBlock.ConnectionType.OUTPUT || conn == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                    if (neighborState.getBlock() instanceof IOnBlockUpdate updater) {
                        updater.onUpdate(world, neighborPos, neighborState);
                    }
                    world.updateNeighbor(neighborPos, world.getBlockState(neighborPos).getBlock(), pos);
                }
            }
        }
    }

    public boolean compute(World world, BlockPos pos, BlockState state) {
        return operator.apply(
                connections.entrySet().stream()
                        .filter(e -> e.getValue() == WireBlock.ConnectionType.INPUT || e.getValue() == WireBlock.ConnectionType.REDSTONE_INPUT)
                        .map(e -> {
                            final Direction direction = e.getKey();
                            final BlockPos neighborPos = pos.offset(direction);
                            final BlockState neighborState = world.getBlockState(neighborPos);
                            return (e.getValue() == WireBlock.ConnectionType.INPUT && neighborState.getBlock() instanceof IDungeonWire wire && wire.isEmittingDungeonWirePower(neighborState, world, neighborPos, direction.getOpposite()))
                                    || (e.getValue() == WireBlock.ConnectionType.REDSTONE_INPUT && world.getEmittedRedstonePower(neighborPos, direction) > 0);
                        }).toArray(Boolean[]::new)
        );
    }

    public void rotateConnections(BlockRotation rotation) {
        if (rotation == BlockRotation.NONE) return;
        Map<Direction, WireBlock.ConnectionType> newMap = new HashMap<>();
        for (Direction dir : Direction.values()) {
            newMap.put(rotation.rotate(dir), connections.get(dir));
        }
        this.connections.clear();
        this.connections.putAll(newMap);
        this.markDirty();
    }

    public void mirrorConnections(BlockMirror mirror) {
        if (mirror == BlockMirror.NONE) return;
        Map<Direction, WireBlock.ConnectionType> newMap = new HashMap<>();
        for (Direction dir : Direction.values()) {
            newMap.put(mirror.apply(dir), connections.get(dir));
        }
        this.connections.clear();
        this.connections.putAll(newMap);
        this.markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString("operator", operator.asString());
        nbt.putInt("delay", delay);
        nbt.putBoolean("scheduledPower", scheduledPower);
        for (Direction dir : Direction.values()) {
            nbt.putString("connection_" + dir.getName(), connections.get(dir).asString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.operator = WireBlock.Operator.valueOf(nbt.getString("operator").toUpperCase());
        this.delay = nbt.getInt("delay");
        this.scheduledPower = nbt.getBoolean("scheduledPower");
        for (Direction dir : Direction.values()) {
            if (nbt.contains("connection_" + dir.getName())) {
                connections.put(dir, WireBlock.ConnectionType.valueOf(nbt.getString("connection_" + dir.getName()).toUpperCase()));
            }
        }
    }

    @Override
    public void setOperator(WireBlock.Operator op, World world, BlockState state, BlockPos pos) {
        this.operator = op;
        markDirty();
        setPower(compute(world, pos, state));
        if (!world.isClient()) {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public WireBlock.Operator getOperator(World world, BlockState state, BlockPos pos) {
        return this.operator;
    }

    @Override
    public void setDelay(int delay,World world, BlockState state, BlockPos pos) {
        this.delay = delay;
        markDirty();
        setPower(compute(world, pos, state));
        if (!world.isClient()) {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public int getDelay(World world, BlockState state, BlockPos pos) {
        return this.delay;
    }

    @Override
    public WireBlock.Operator cycleOperator(World world, BlockState state, BlockPos pos) {
        WireBlock.Operator next = WireBlock.Operator.values()[(this.operator.ordinal() + 1) % WireBlock.Operator.values().length];
        this.setOperator(next, world, state, pos);
        return next;
    }

    @Override
    public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) {
        WireBlock.ConnectionType next = WireBlock.ConnectionType.values()[(connections.get(face).ordinal() + 1) % WireBlock.ConnectionType.values().length];
        this.setConnection(face, next, world, state, pos);
        return next;
    }

    @Override
    public void setConnection(Direction face, WireBlock.ConnectionType connectionType, World world, BlockState state, BlockPos pos) {
        connections.put(face, connectionType);
        markDirty();
        setPower(compute(world, pos, state));
        if (!world.isClient()) {
            world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return this.connections.get(face); }

    @Override
    public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return this.connections; }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return isPowered(state) && this.getConnection(face, world, state, pos) == WireBlock.ConnectionType.OUTPUT;
    }

    public boolean isEmittingRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction face) {
        return isPowered(state) && this.getConnection(face, (World) world, state, pos) == WireBlock.ConnectionType.REDSTONE_OUTPUT;
    }

    @Override
    public void onUpdate(World world, BlockPos pos, BlockState state) {
        final boolean value = this.compute(world, pos, state);
        if (delay == 0) {
            this.setPower(value);
        } else {
            if (isPowered(state) != value && world != null && !world.getBlockTickScheduler().isQueued(pos, world.getBlockState(pos).getBlock())) {
                world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), delay);
                this.scheduledPower = value;
            }
        }
    }

    public boolean isPowered(BlockState state) {
        return state.get(WireBlock.POWERED);
    }

    public boolean getScheduledPower() {
        return scheduledPower;
    }
}
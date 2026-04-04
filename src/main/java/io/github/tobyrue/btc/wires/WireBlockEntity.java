package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.PowerPillarBlock;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class WireBlockEntity extends BlockEntity implements IDungeonWire {
    private WireBlock.Operator operator = WireBlock.Operator.OR;
    private int delay = 0;
    private final Map<Direction, WireBlock.ConnectionType> connections = new HashMap<>();

    public WireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WIRE_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.INPUT);
        }
    }

    public void updatePower() {
        if (world == null || world.isClient) return;
        var state = this.getWorld().getBlockState(this.getPos());

        final boolean currentlyPowered = state.get(WireBlock.POWERED);

        final boolean shouldBePowered = operator.apply(
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

        if (currentlyPowered != shouldBePowered) {
            world.setBlockState(pos, getCachedState().with(WireBlock.POWERED, shouldBePowered), Block.NOTIFY_ALL);
            for (Direction dir : Direction.values()) {
                if (connections.get(dir) == WireBlock.ConnectionType.OUTPUT || connections.get(dir) == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                    world.updateNeighbor(pos.offset(dir), world.getBlockState(pos.offset(dir)).getBlock(), pos);
                }
            }
        }
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
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString("operator", operator.asString());
        nbt.putInt("delay", delay);
        for (Direction dir : Direction.values()) {
            nbt.putString("connection_" + dir.getName(), connections.get(dir).asString());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.operator = WireBlock.Operator.valueOf(nbt.getString("operator").toUpperCase());
        this.delay = nbt.getInt("delay");
        for (Direction dir : Direction.values()) {
            if (nbt.contains("connection_" + dir.getName())) {
                connections.put(dir, WireBlock.ConnectionType.valueOf(nbt.getString("connection_" + dir.getName()).toUpperCase()));
            }
        }
    }

    public void setOperator(WireBlock.Operator op) {
        this.operator = op;
        markDirty();
        updatePower();
    }

    public WireBlock.Operator getOperator() {
        return this.operator;
    }

    public void setDelay(int delay) {
        this.delay = delay;
        markDirty();
        updatePower();
    }

    public int getDelay() {
        return this.delay;
    }


    public WireBlock.Operator cycleOperator() {
        WireBlock.Operator next = WireBlock.Operator.values()[(this.operator.ordinal() + 1) % WireBlock.Operator.values().length];
        this.setOperator(next);
        return next;
    }

    public WireBlock.ConnectionType cycleConnection(Direction face) {
        WireBlock.ConnectionType next = WireBlock.ConnectionType.values()[(connections.get(face).ordinal() + 1) % WireBlock.ConnectionType.values().length];
        this.setConnection(face, next);
        return next;
    }

    public void setConnection(Direction face, WireBlock.ConnectionType connectionType) {
        connections.put(face, connectionType);
        markDirty();
        updatePower();
    }

    public WireBlock.ConnectionType getConnection(Direction face) { return this.connections.get(face); }
    public Map<Direction, WireBlock.ConnectionType> getConnections() { return this.connections; }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return state.get(WireBlock.POWERED) && this.getConnection(face) == WireBlock.ConnectionType.OUTPUT;
    }

    public boolean isEmittingRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction face) {
        return state.get(WireBlock.POWERED) && this.getConnection(face) == WireBlock.ConnectionType.REDSTONE_OUTPUT;
    }
}
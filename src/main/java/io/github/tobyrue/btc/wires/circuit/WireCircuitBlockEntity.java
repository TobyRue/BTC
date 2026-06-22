package io.github.tobyrue.btc.wires.circuit;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
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
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WireCircuitBlockEntity extends BlockEntity implements IDungeonWire, IWireConnectionHelper {

    private String scriptCode = "";
    private final Map<Direction, WireBlock.ConnectionType> connections = new HashMap<>();
    private final Map<Direction, Boolean> outputPowerStates = new HashMap<>();

    public WireCircuitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.INPUT);
            outputPowerStates.put(dir, false);
        }
    }

    public String getScriptCode() {
        return this.scriptCode;
    }

    public void setScriptCode(String code) {
        this.scriptCode = code;
        this.markDirty();
        this.updateCircuitLogic();
    }

    public boolean getOutputPowerState(Direction direction) {
        return this.outputPowerStates.getOrDefault(direction, false);
    }

    public void updateCircuitLogic() {
        if (world == null || world.isClient) return;

        Map<String, Boolean> environment = new HashMap<>();

        for (Direction dir : Direction.values()) {
            String name = dir.getName().toLowerCase();
            WireBlock.ConnectionType type = connections.get(dir);

            boolean isInputPowered = false;
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (type == WireBlock.ConnectionType.INPUT && neighborState.getBlock() instanceof IDungeonWire wire) {
                isInputPowered = wire.isEmittingDungeonWirePower(neighborState, world, neighborPos, dir.getOpposite());
            } else if (type == WireBlock.ConnectionType.REDSTONE_INPUT) {
                isInputPowered = world.getEmittedRedstonePower(neighborPos, dir) > 0;
            }

            environment.put(name, isInputPowered);
        }

        executeMiniScript(environment);

        boolean powerFlowUpdated = false;
        boolean globalPowerBlockState = false;

        for (Direction dir : Direction.values()) {
            WireBlock.ConnectionType type = connections.get(dir);
            boolean isOutput = (type == WireBlock.ConnectionType.OUTPUT || type == WireBlock.ConnectionType.REDSTONE_OUTPUT);

            if (isOutput) {
                boolean nextState = environment.getOrDefault(dir.getName().toLowerCase(), false);
                if (nextState) {
                    globalPowerBlockState = true;
                }

                if (outputPowerStates.get(dir) != nextState) {
                    outputPowerStates.put(dir, nextState);
                    powerFlowUpdated = true;

                    BlockPos neighborPos = pos.offset(dir);
                    world.updateNeighbor(neighborPos, world.getBlockState(neighborPos).getBlock(), pos);
                    if (type == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                        world.updateNeighborsAlways(pos, getCachedState().getBlock());
                    }
                }
            }
        }

        if (getCachedState().contains(WireCircuitBlock.POWERED) && getCachedState().get(WireCircuitBlock.POWERED) != globalPowerBlockState) {
            world.setBlockState(pos, getCachedState().with(WireCircuitBlock.POWERED, globalPowerBlockState), Block.NOTIFY_ALL);
        }

        if (powerFlowUpdated) {
            this.markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    private void executeMiniScript(Map<String, Boolean> env) {
        if (scriptCode == null || scriptCode.isEmpty()) return;

        Map<String, Boolean> logicMap = new HashMap<>();
        for (Direction dir : Direction.values()) {
            logicMap.put("in_" + dir.getName().toLowerCase(), env.getOrDefault(dir.getName().toLowerCase(), false));
            logicMap.put("out_" + dir.getName().toLowerCase(), false);
        }

        String[] lines = scriptCode.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) continue;

            if (line.startsWith("set_type(") && line.endsWith(")")) {
                String inner = line.substring(9, line.length() - 1);
                if (inner.contains(",")) {
                    String[] args = inner.split(",", 2);
                    Direction d = Direction.byName(args[0].trim().toLowerCase());
                    String typeStr = args[1].trim().toLowerCase();

                    if (d != null) {
                        switch (typeStr) {
                            case "input" -> connections.put(d, WireBlock.ConnectionType.INPUT);
                            case "output" -> connections.put(d, WireBlock.ConnectionType.OUTPUT);
                            case "redstone_input" -> connections.put(d, WireBlock.ConnectionType.REDSTONE_INPUT);
                            case "redstone_output" -> connections.put(d, WireBlock.ConnectionType.REDSTONE_OUTPUT);
                            case "none" -> connections.put(d, WireBlock.ConnectionType.NONE);
                        }
                    }
                }
                continue;
            }

            if (line.contains("=")) {
                String[] parts = line.split("=", 2);
                String target = parts[0].trim().toLowerCase();
                String expression = parts[1].trim();

                boolean result = evaluateExpression(expression, logicMap);
                logicMap.put(target, result);
            }
        }

        for (Direction dir : Direction.values()) {
            String name = dir.getName().toLowerCase();
            env.put(name, logicMap.getOrDefault("out_" + name, false));
        }
    }

    private boolean evaluateExpression(String expr, Map<String, Boolean> env) {
        if (expr.equalsIgnoreCase("true") || expr.equals("1")) return true;
        if (expr.equalsIgnoreCase("false") || expr.equals("0")) return false;
        if (env.containsKey(expr)) return env.get(expr);

        if (expr.startsWith("!")) {
            return !evaluateExpression(expr.substring(1).trim(), env);
        }

        if (expr.contains("&&")) {
            String[] tokens = expr.split("&&", 2);
            return evaluateExpression(tokens[0].trim(), env) && evaluateExpression(tokens[1].trim(), env);
        }

        if (expr.contains("||")) {
            String[] tokens = expr.split("\\|\\|", 2);
            return evaluateExpression(tokens[0].trim(), env) || evaluateExpression(tokens[1].trim(), env);
        }

        return false;
    }

    public void rotateConnections(BlockRotation rotation) {
        if (rotation == BlockRotation.NONE) return;
        Map<Direction, WireBlock.ConnectionType> rotatedConnections = new HashMap<>();
        Map<Direction, Boolean> rotatedStates = new HashMap<>();

        for (Direction dir : Direction.values()) {
            Direction newDir = rotation.rotate(dir);
            rotatedConnections.put(newDir, connections.get(dir));
            rotatedStates.put(newDir, outputPowerStates.get(dir));
        }

        this.connections.clear();
        this.connections.putAll(rotatedConnections);
        this.outputPowerStates.clear();
        this.outputPowerStates.putAll(rotatedStates);
        this.markDirty();
    }

    public void mirrorConnections(BlockMirror mirror) {
        if (mirror == BlockMirror.NONE) return;
        Map<Direction, WireBlock.ConnectionType> mirroredConnections = new HashMap<>();
        Map<Direction, Boolean> mirroredStates = new HashMap<>();

        for (Direction dir : Direction.values()) {
            Direction newDir = mirror.apply(dir);
            mirroredConnections.put(newDir, connections.get(dir));
            mirroredStates.put(newDir, outputPowerStates.get(dir));
        }

        this.connections.clear();
        this.connections.putAll(mirroredConnections);
        this.outputPowerStates.clear();
        this.outputPowerStates.putAll(mirroredStates);
        this.markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString("ScriptCode", this.scriptCode);
        for (Direction dir : Direction.values()) {
            nbt.putString("connection_" + dir.getName(), connections.get(dir).asString());
            nbt.putBoolean("powered_" + dir.getName(), outputPowerStates.get(dir));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.scriptCode = nbt.getString("ScriptCode");
        for (Direction dir : Direction.values()) {
            if (nbt.contains("connection_" + dir.getName())) {
                connections.put(dir, WireBlock.ConnectionType.valueOf(nbt.getString("connection_" + dir.getName()).toUpperCase()));
            }
            if (nbt.contains("powered_" + dir.getName())) {
                outputPowerStates.put(dir, nbt.getBoolean("powered_" + dir.getName()));
            }
        }
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return this.connections.get(face) == WireBlock.ConnectionType.OUTPUT && this.outputPowerStates.getOrDefault(face, false);
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
        updateCircuitLogic();
    }

    @Override
    public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return this.connections.get(face); }
    @Override
    public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return this.connections; }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}
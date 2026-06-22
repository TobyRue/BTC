package io.github.tobyrue.btc.wires.circuit;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPGABlockEntity extends BlockEntity implements IDungeonWire, IWireConnectionHelper {

    private ItemStack bookStack = ItemStack.EMPTY;
    private final Map<Direction, WireBlock.ConnectionType> connections = new HashMap<>();
    private final Map<Direction, Boolean> outputPowerStates = new HashMap<>();

    // Tracks the current state of variables across evaluation ticks (enables loop feedback)
    private final Map<String, Boolean> variableMemoryRegistry = new HashMap<>();

    public FPGABlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.NONE);
            outputPowerStates.put(dir, false);
        }
    }

    public boolean hasBook() { return !this.bookStack.isEmpty(); }
    public void setBook(ItemStack stack) { this.bookStack = stack; this.markDirty(); this.updateCircuitLogic(); }
    public ItemStack removeBook() { ItemStack s = this.bookStack; this.bookStack = ItemStack.EMPTY; this.markDirty(); this.updateCircuitLogic(); return s; }
    public boolean getOutputPowerState(Direction direction) { return this.outputPowerStates.getOrDefault(direction, false); }

    private String extractScriptFromBook() {
        if (!hasBook()) return "";
        List<String> pages = new ArrayList<>();
        var writable = bookStack.get(DataComponentTypes.WRITABLE_BOOK_CONTENT);
        if (writable != null) {
            for (RawFilteredPair<String> page : writable.pages()) pages.add(page.raw());
        } else {
            var written = bookStack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT);
            if (written != null) {
                for (RawFilteredPair<Text> page : written.pages()) pages.add(page.raw().getString());
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String p : pages) sb.append(p).append("\n");
        return sb.toString();
    }

    public void updateCircuitLogic() {
        if (world == null || world.isClient) return;

        String script = extractScriptFromBook();
        if (script.isEmpty()) {
            return;
        }

        executeExpressionScript(script);

        boolean updatesTriggered = false;
        boolean globalBlockGlow = false;

        for (Direction dir : Direction.values()) {
            WireBlock.ConnectionType type = connections.get(dir);
            boolean isOutput = (type == WireBlock.ConnectionType.OUTPUT || type == WireBlock.ConnectionType.REDSTONE_OUTPUT);

            if (isOutput) {
                boolean nextState = outputPowerStates.getOrDefault(dir, false);
                if (nextState) globalBlockGlow = true;

                if (outputPowerStates.get(dir) != nextState) {
                    outputPowerStates.put(dir, nextState);
                    updatesTriggered = true;

                    BlockPos nPos = pos.offset(dir);
                    world.updateNeighbor(nPos, world.getBlockState(nPos).getBlock(), pos);
                    if (type == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                        world.updateNeighborsAlways(pos, getCachedState().getBlock());
                    }
                }
            }
        }

        if (getCachedState().contains(FPGABlock.POWERED) && getCachedState().get(FPGABlock.POWERED) != globalBlockGlow) {
            world.setBlockState(pos, getCachedState().with(FPGABlock.POWERED, globalBlockGlow), Block.NOTIFY_ALL);
        }

        if (updatesTriggered) {
            this.markDirty();
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    private void executeExpressionScript(String script) {
        Map<String, Direction> varToDir = new HashMap<>();
        Map<String, Boolean> isOutputPin = new HashMap<>();

        Map<String, Boolean> localEvaluationMap = new HashMap<>(variableMemoryRegistry);

        String cleanScript = script.replace("\\n", "\n");
        String[] lines = cleanScript.split("\n");

        boolean insideSwitchBlock = false;
        List<String> switchVariables = new ArrayList<>();
        boolean switchCaseMatched = false;

        for (String line : lines) {
            line = line.trim();
            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1).trim();
            }
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("///")) continue;


            if (line.startsWith("input ") || line.startsWith("output ")) {
                boolean isOut = line.startsWith("output ");
                String definition = line.substring(6).trim();

                if (definition.contains(" on ")) {
                    String[] parts = definition.split(" on ", 2);
                    String varName = parts[0].trim().toLowerCase();
                    String remaining = parts[1].trim();

                    Direction targetDir = null;
                    String typeToken = "wire";

                    if (remaining.contains(" as ")) {
                        String[] typeSplit = remaining.split(" as ", 2);
                        targetDir = parseDirection(typeSplit[0].trim());
                        typeToken = typeSplit[1].trim().toLowerCase();
                    } else {
                        targetDir = parseDirection(remaining);
                    }

                    if (targetDir != null) {
                        String ioType = (isOut ? "output" : "input");
                        if (typeToken.contains("redstone")) ioType = "redstone_" + ioType;
                        configureHardwarePin(targetDir, ioType);

                        varToDir.put(varName, targetDir);
                        isOutputPin.put(varName, isOut);

                        if (!isOut) {
                            boolean activeSignal = readLiveInput(targetDir, connections.get(targetDir));
                            localEvaluationMap.put(varName, activeSignal);
                        } else {
                            boolean lastState = variableMemoryRegistry.getOrDefault(varName, false);
                            localEvaluationMap.put(varName, lastState);
                        }
                    }
                }
                continue;
            }

            if (line.startsWith("switch ")) {
                insideSwitchBlock = true;
                switchCaseMatched = false;
                switchVariables.clear();

                String[] tokens = line.substring(7).trim().split(",");
                for (String t : tokens) {
                    switchVariables.add(t.trim().toLowerCase());
                }
                continue;
            }

            if (insideSwitchBlock && line.equals("end")) {
                insideSwitchBlock = false;
                continue;
            }

            if (insideSwitchBlock) {
                if (line.startsWith("case ") && line.contains(":")) {
                    if (switchCaseMatched) continue;

                    String[] split = line.substring(5).split(":", 2);
                    String pattern = split[0].trim();
                    String action = split[1].trim();

                    if (pattern.length() == switchVariables.size()) {
                        boolean patternMatchesEnv = true;

                        for (int i = 0; i < pattern.length(); i++) {
                            char bit = pattern.charAt(i);
                            String variableName = switchVariables.get(i);
                            boolean currentActualValue = localEvaluationMap.getOrDefault(variableName, false);
                            boolean expectedBitValue = (bit == '1');

                            if (currentActualValue != expectedBitValue) {
                                patternMatchesEnv = false;
                                break;
                            }
                        }

                        if (patternMatchesEnv) {
                            switchCaseMatched = true;

                            if (action.contains("=")) {
                                String[] assignment = action.split("=", 2);
                                String targetVar = assignment[0].trim().toLowerCase();
                                boolean targetValue = assignment[1].trim().equals("1") || assignment[1].trim().equalsIgnoreCase("true");

                                localEvaluationMap.put(targetVar, targetValue);
                                variableMemoryRegistry.put(targetVar, targetValue);

                                if (varToDir.containsKey(targetVar) && isOutputPin.getOrDefault(targetVar, false)) {
                                    Direction targetDir = varToDir.get(targetVar);
                                    outputPowerStates.put(targetDir, targetValue);
                                }
                            }
                        }
                    }
                }
                continue;
            }


            if (line.contains("=")) {
                String leftVar = "";
                String rightExpr = "";

                if (line.startsWith("reg ") || line.startsWith("assign ")) {
                    String assignmentContent = line.substring(line.indexOf(" ") + 1).trim();
                    String[] parts = assignmentContent.split("=", 2);
                    leftVar = parts[0].trim().toLowerCase();
                    rightExpr = parts[1].trim();

                    if (leftVar.contains("[")) {
                        leftVar = leftVar.substring(0, leftVar.indexOf("[")).trim();
                    }
                } else {
                    String[] parts = line.split("=", 2);
                    leftVar = parts[0].trim().toLowerCase();
                    rightExpr = parts[1].trim();
                }


                boolean evaluatedValue = evaluateBooleanAlgebra(rightExpr, localEvaluationMap);

                localEvaluationMap.put(leftVar, evaluatedValue);
                variableMemoryRegistry.put(leftVar, evaluatedValue);

                if (varToDir.containsKey(leftVar) && isOutputPin.getOrDefault(leftVar, false)) {
                    Direction targetDir = varToDir.get(leftVar);
                    outputPowerStates.put(targetDir, evaluatedValue);
                }
            }
        }
    }

    private boolean readLiveInput(Direction dir, WireBlock.ConnectionType type) {
        if (world == null) return false;
        BlockPos nPos = pos.offset(dir);
        BlockState nState = world.getBlockState(nPos);
        if (type == WireBlock.ConnectionType.INPUT && nState.getBlock() instanceof IDungeonWire wire) {
            return wire.isEmittingDungeonWirePower(nState, world, nPos, dir.getOpposite());
        } else if (type == WireBlock.ConnectionType.REDSTONE_INPUT) {
            return world.getEmittedRedstonePower(nPos, dir) > 0;
        }
        return false;
    }

    private boolean evaluateBooleanAlgebra(String expr, Map<String, Boolean> context) {
        expr = expr.trim();

        while (expr.startsWith("(") && expr.endsWith(")")) {
            int openCount = 0;
            boolean matchingPairs = true;
            for (int i = 0; i < expr.length() - 1; i++) {
                if (expr.charAt(i) == '(') openCount++;
                if (expr.charAt(i) == ')') openCount--;
                if (openCount == 0 && i > 0) {
                    matchingPairs = false;
                    break;
                }
            }
            if (matchingPairs) {
                expr = expr.substring(1, expr.length() - 1).trim();
            } else {
                break;
            }
        }

        if (expr.equalsIgnoreCase("true") || expr.equals("1")) return true;
        if (expr.equalsIgnoreCase("false") || expr.equals("0")) return false;

        int splitIdx = findTopLevelOperator(expr, "|");
        if (splitIdx != -1) {
            return evaluateBooleanAlgebra(expr.substring(0, splitIdx), context)
                    || evaluateBooleanAlgebra(expr.substring(splitIdx + 1), context);
        }

        splitIdx = findTopLevelOperator(expr, "&");
        if (splitIdx != -1) {
            return evaluateBooleanAlgebra(expr.substring(0, splitIdx), context)
                    && evaluateBooleanAlgebra(expr.substring(splitIdx + 1), context);
        }

        if (expr.startsWith("~")) {
            return !evaluateBooleanAlgebra(expr.substring(1), context);
        }

        if (context.containsKey(expr.toLowerCase())) {
            return context.get(expr.toLowerCase());
        }

        return false;
    }


    private int findTopLevelOperator(String expr, String targetOp) {
        int bracketDepth = 0;
        int opLen = targetOp.length();

        for (int i = 0; i < expr.length() - opLen + 1; i++) {
            char c = expr.charAt(i);
            if (c == '(') bracketDepth++;
            else if (c == ')') bracketDepth--;
            else if (bracketDepth == 0) {
                if (expr.substring(i, i + opLen).equals(targetOp)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void configureHardwarePin(Direction dir, String typeToken) {
        WireBlock.ConnectionType target;
        if (typeToken.contains("redstone")) {
            target = typeToken.contains("input") ? WireBlock.ConnectionType.REDSTONE_INPUT : WireBlock.ConnectionType.REDSTONE_OUTPUT;
        } else {
            target = typeToken.contains("input") ? WireBlock.ConnectionType.INPUT : WireBlock.ConnectionType.OUTPUT;
        }
        connections.put(dir, target);
    }

    private Direction parseDirection(String input) {
        return switch (input.toLowerCase()) {
            case "north", "n" -> Direction.NORTH;
            case "east", "e" -> Direction.EAST;
            case "south", "s" -> Direction.SOUTH;
            case "west", "w" -> Direction.WEST;
            case "up", "u" -> Direction.UP;
            case "down", "d" -> Direction.DOWN;
            default -> null;
        };
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.bookStack.isEmpty()) nbt.put("CircuitBook", this.bookStack.encode(registryLookup));
        for (Direction dir : Direction.values()) {
            nbt.putString("connection_" + dir.getName(), connections.get(dir).asString());
            nbt.putBoolean("powered_" + dir.getName(), outputPowerStates.get(dir));
        }
        NbtCompound mem = new NbtCompound();
        variableMemoryRegistry.forEach(mem::putBoolean);
        nbt.put("LoopMemory", mem);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.bookStack = nbt.contains("CircuitBook") ? ItemStack.fromNbt(registryLookup, nbt.getCompound("CircuitBook")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        for (Direction dir : Direction.values()) {
            if (nbt.contains("connection_" + dir.getName())) {
                connections.put(dir, WireBlock.ConnectionType.valueOf(nbt.getString("connection_" + dir.getName()).toUpperCase()));
            }
            if (nbt.contains("powered_" + dir.getName())) {
                outputPowerStates.put(dir, nbt.getBoolean("powered_" + dir.getName()));
            }
        }
        if (nbt.contains("LoopMemory")) {
            NbtCompound mem = nbt.getCompound("LoopMemory");
            for (String key : mem.getKeys()) variableMemoryRegistry.put(key, mem.getBoolean(key));
        }
    }

    @Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return this.connections.get(face) == WireBlock.ConnectionType.OUTPUT && this.outputPowerStates.getOrDefault(face, false); }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { WireBlock.ConnectionType next = WireBlock.ConnectionType.values()[(connections.get(face).ordinal() + 1) % WireBlock.ConnectionType.values().length]; this.setConnection(face, next, world, state, pos); return next; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { connections.put(face, type); markDirty(); updateCircuitLogic(); }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return this.connections.get(face); }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return this.connections; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}
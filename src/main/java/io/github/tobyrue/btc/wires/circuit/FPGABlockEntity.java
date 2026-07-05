package io.github.tobyrue.btc.wires.circuit;

import com.mojang.serialization.Codec;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.rsl.BitString;
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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FPGABlockEntity extends BlockEntity implements IDungeonWire, IWireConnectionHelper {

    private ItemStack bookStack = ItemStack.EMPTY;
    private final Map<Direction, WireBlock.ConnectionType> connections = new HashMap<>();
    private final Map<Direction, Boolean> outputPowerStates = new HashMap<>();
    private final Map<String, Boolean> variableMemoryRegistry = new HashMap<>();

    private final Map<String, String> cachedDeclarations = new HashMap<>();
    private final List<String> cachedExecutionLines = new ArrayList<>();
    private boolean isScriptCompiled = false;

    public static final Codec<BitString> BIT_STRING_CODEC =
            Codec.STRING.xmap(
                    BitString::valueOf,
                    BitString::toString
            );

    public static final Codec<HashMap<String, BitString>> BIT_STRING_MAP_CODEC =
            Codec.unboundedMap(
                    Codec.STRING,
                    BIT_STRING_CODEC
            ).xmap(
                    HashMap::new,
                    map -> map
            );



    private final Map<Direction, Integer> renderedFaceNumbers = new HashMap<>();

    public FPGABlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY, pos, state);
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.NONE);
            outputPowerStates.put(dir, false);
            renderedFaceNumbers.put(dir, 0);
        }
    }

    public boolean hasBook() { return !this.bookStack.isEmpty(); }

    public void setBook(ItemStack stack) {
        this.bookStack = stack;
        this.isScriptCompiled = false;
        this.markDirty();
        this.compileBookScript();
    }

    public ItemStack removeBook() {
        ItemStack s = this.bookStack;
        this.bookStack = ItemStack.EMPTY;
        this.isScriptCompiled = false;
        this.cachedDeclarations.clear();
        this.cachedExecutionLines.clear();
        clearRenderFaceNumbers();
        clearHardwarePins();
        this.markDirty();
        this.updateCircuitLogic();
        return s;
    }

    public boolean getOutputPowerState(Direction direction) { return this.outputPowerStates.getOrDefault(direction, false); }

    public int getRenderedNumberForFace(Direction face) {
        return this.renderedFaceNumbers.getOrDefault(face, 0);
    }

    private void clearRenderFaceNumbers() {
        for (Direction dir : Direction.values()) {
            renderedFaceNumbers.put(dir, 0);
        }
    }

    private void clearHardwarePins() {
        for (Direction dir : Direction.values()) {
            connections.put(dir, WireBlock.ConnectionType.NONE);
        }
    }

    /**
     * Forces logic execution to recompute hardware orientations immediately when rotated or mirrored.
     */
    public void forceLogicRecomputation() {
        clearHardwarePins();
        clearRenderFaceNumbers();
        if (world != null && !world.isClient) {
            updateCircuitLogic();
        }
    }

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

    public void compileBookScript() {
        if (this.isScriptCompiled) return;

        this.cachedDeclarations.clear();
        this.cachedExecutionLines.clear();

        String script = extractScriptFromBook();
        if (script.isEmpty()) {
            this.isScriptCompiled = false;
            return;
        }

        String cleanScript = script.replace("\\n", "\n");
        String[] lines = cleanScript.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1).trim();
            }
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("///")) continue;

            if (line.startsWith("input ") || line.startsWith("output ")) {
                String type = line.startsWith("input ") ? "input" : "output";
                this.cachedDeclarations.put(line.substring(type.length() + 1).trim(), type);
            } else {
                this.cachedExecutionLines.add(line);
            }
        }
        this.isScriptCompiled = true;
        this.updateCircuitLogic();
    }

    private Direction getRotatedDirection(int nativeSideIndex) {
        Direction baseDir = switch (nativeSideIndex) {
            case 1 -> Direction.NORTH;
            case 2 -> Direction.EAST;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.WEST;
            case 5 -> Direction.UP;
            case 6 -> Direction.DOWN;
            default -> null;
        };

        if (world == null || baseDir == null || baseDir == Direction.UP || baseDir == Direction.DOWN) {
            return baseDir;
        }

        BlockState state = getCachedState();
        Direction facing = state.contains(FPGABlock.FACING) ? state.get(FPGABlock.FACING) : Direction.NORTH;
        BlockMirror mirror = state.contains(FPGABlock.MIRRORED) ? state.get(FPGABlock.MIRRORED) : BlockMirror.NONE;

        Direction mirrored = mirror.apply(baseDir);
        int horizontalOffset = (mirrored.getHorizontal() + facing.getHorizontal()) % 4;
        return Direction.fromHorizontal(horizontalOffset);
    }

    public void updateCircuitLogic() {
        if (world == null || world.isClient) return;

        if (!isScriptCompiled) {
            compileBookScript();
            if (!isScriptCompiled) return;
        }

        clearRenderFaceNumbers();
        Map<String, Direction> varToDir = new HashMap<>();
        Map<String, Boolean> isOutputPin = new HashMap<>();
        Map<String, Boolean> localEvaluationMap = new HashMap<>(variableMemoryRegistry);

        for (Map.Entry<String, String> decl : cachedDeclarations.entrySet()) {
            String definition = decl.getKey();
            boolean isOut = decl.getValue().equals("output");

            if (definition.contains(" on ")) {
                String[] parts = definition.split(" on ", 2);
                String varName = parts[0].trim().toLowerCase();
                String remaining = parts[1].trim();

                int nativeSideIndex = -1;
                String typeToken = "wire";

                if (remaining.contains(" as ")) {
                    String[] typeSplit = remaining.split(" as ", 2);
                    try { nativeSideIndex = Integer.parseInt(typeSplit[0].trim()); } catch (NumberFormatException ignored) {}
                    typeToken = typeSplit[1].trim().toLowerCase();
                } else {
                    try { nativeSideIndex = Integer.parseInt(remaining); } catch (NumberFormatException ignored) {}
                }

                if (nativeSideIndex >= 1 && nativeSideIndex <= 6) {
                    Direction targetDir = getRotatedDirection(nativeSideIndex);

                    if (targetDir != null) {
                        this.renderedFaceNumbers.put(targetDir, nativeSideIndex);

                        String ioType = (isOut ? "output" : "input");
                        if (typeToken.contains("redstone")) ioType = "redstone_" + ioType;
                        configureHardwarePin(targetDir, ioType);

                        varToDir.put(varName, targetDir);
                        isOutputPin.put(varName, isOut);

                        if (!isOut) {
                            localEvaluationMap.put(varName, readLiveInput(targetDir, connections.get(targetDir)));
                        } else {
                            localEvaluationMap.put(varName, variableMemoryRegistry.getOrDefault(varName, false));
                        }
                    }
                }
            }
        }

        boolean insideSwitchBlock = false;
        List<String> switchVariables = new ArrayList<>();
        boolean switchCaseMatched = false;

        for (String line : cachedExecutionLines) {
            if (line.startsWith("switch ")) {
                insideSwitchBlock = true;
                switchCaseMatched = false;
                switchVariables.clear();
                String[] tokens = line.substring(7).trim().split(",");
                for (String t : tokens) switchVariables.add(t.trim().toLowerCase());
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
                            if (localEvaluationMap.getOrDefault(switchVariables.get(i), false) != (bit == '1')) {
                                patternMatchesEnv = false;
                                break;
                            }
                        }

                        if (patternMatchesEnv && action.contains("=")) {
                            switchCaseMatched = true;
                            String[] assignment = action.split("=", 2);
                            String targetVar = assignment[0].trim().toLowerCase();
                            boolean targetValue = assignment[1].trim().equals("1") || assignment[1].trim().equalsIgnoreCase("true");

                            localEvaluationMap.put(targetVar, targetValue);
                            variableMemoryRegistry.put(targetVar, targetValue);

                            if (varToDir.containsKey(targetVar) && isOutputPin.getOrDefault(targetVar, false)) {
                                outputPowerStates.put(varToDir.get(targetVar), targetValue);
                            }
                        }
                    }
                }
                continue;
            }

            if (line.contains("=")) {
                String leftVar;
                String rightExpr;

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
                    outputPowerStates.put(varToDir.get(leftVar), evaluatedValue);
                }
            }
        }

        boolean updatesTriggered = false;
        boolean globalBlockGlow = false;

        for (Direction dir : Direction.values()) {
            WireBlock.ConnectionType type = connections.get(dir);
            if (type == WireBlock.ConnectionType.OUTPUT || type == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                boolean nextState = outputPowerStates.getOrDefault(dir, false);
                if (nextState) globalBlockGlow = true;

                if (outputPowerStates.getOrDefault(dir, false) != nextState) {
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
            if (matchingPairs) expr = expr.substring(1, expr.length() - 1).trim();
            else break;
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

        if (expr.startsWith("~")) return !evaluateBooleanAlgebra(expr.substring(1), context);
        if (context.containsKey(expr.toLowerCase())) return context.get(expr.toLowerCase());
        return false;
    }

    private int findTopLevelOperator(String expr, String targetOp) {
        int bracketDepth = 0;
        int opLen = targetOp.length();
        for (int i = 0; i < expr.length() - opLen + 1; i++) {
            char c = expr.charAt(i);
            if (c == '(') bracketDepth++;
            else if (c == ')') bracketDepth--;
            else if (bracketDepth == 0 && expr.substring(i, i + opLen).equals(targetOp)) {
                return i;
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
        this.isScriptCompiled = false;
    }

    @Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return this.connections.get(face) == WireBlock.ConnectionType.OUTPUT && this.outputPowerStates.getOrDefault(face, false); }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { WireBlock.ConnectionType next = WireBlock.ConnectionType.values()[(connections.get(face).ordinal() + 1) % WireBlock.ConnectionType.values().length]; this.setConnection(face, next, world, state, pos); return next; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { connections.put(face, type); markDirty(); updateCircuitLogic(); }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return this.connections.get(face); }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return this.connections; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }
}
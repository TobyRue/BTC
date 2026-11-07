package io.github.tobyrue.btc.wires.circuit;

import com.mojang.serialization.Codec;
import io.github.sianabeeblebrox.rhdl.*;
import io.github.tobyrue.btc.block.MobDetectorBlock;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FPGABlockEntity extends BlockEntity implements BlockEntityTicker<FPGABlockEntity>, IDungeonWire /* , IWireConnectionHelper */ {

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

    public static final Codec<HashMap<Direction, String>> FACE_MAP_CODEC =
            Codec.unboundedMap(
                    Direction.CODEC,
                    Codec.STRING
            ).xmap(
                    HashMap::new,
                    map -> map
            );

    public static final Codec<List<BitString>> BIT_STRING_LIST_CODEC = BIT_STRING_CODEC.listOf();
    private Map<Direction, Integer> perFaceIndex = HashMap.newHashMap(6);

    public Script getScript() {
        return script;
    }
    private Script script;
    private ItemStack bookStack = ItemStack.EMPTY;
    private final Map<Direction, Integer> lastOutputPowers = new HashMap<>();

    public void setFace(Direction direction, String state) {
        this.faces.put(direction, state);
        this.markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }


    public @Nullable State getState(Direction direction) {
        if (script == null) return null;
        return Optional.ofNullable(faces.get(direction)).map(script::getState).orElse(null);
    }

    public @Nullable State.Type getFaceType(Direction direction) {
        if (getState(direction) instanceof final State.ValueBackedState state) {
            return state.getType();
        } else {
            return null;
        }
    }

    private HashMap<Direction, String> faces = HashMap.newHashMap(6);

    public FPGABlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FPGA_BLOCK_ENTITY, pos, state);
    }

    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isCreativeLevelTwoOp() && !getBlockApplicableStateNames().isEmpty()) {
            Direction direction = player.isSneaking() ? hit.getSide().getOpposite() : hit.getSide();
            perFaceIndex.putIfAbsent(direction, getBlockApplicableStateNames().isEmpty() ? 0 : 1);
            int numb = getFaceIndex(direction) % (getBlockApplicableStateNames().size() + 1);
            if (numb == 0) {
                faces.remove(direction);
                setFaceIndex(direction, 0);
                player.sendMessage(Text.translatable("block.btc.fpga_wire.set_face", Text.translatable("block.btc.wire.face." + (player.isSneaking() ? hit.getSide().getOpposite().asString() : hit.getSide().asString())), "null"), true);
                this.markDirty();
                if (world != null) {
                    world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                }
            } else {
                String face = getBlockApplicableStateNames().get(numb - 1);
                player.sendMessage(Text.translatable("block.btc.fpga_wire.set_face", Text.translatable("block.btc.wire.face." + (player.isSneaking() ? hit.getSide().getOpposite().asString() : hit.getSide().asString())), face), true);
                setFace(direction, face);
            }
            setFaceIndex(direction, getFaceIndex(direction) + 1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isCreativeLevelTwoOp()) {
            if (loadScript(stack) instanceof final ScriptException scriptException) {
                player.sendMessage(Text.literal(scriptException.getMessage()).formatted(Formatting.RED));
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
                this.markDirty();
                if (world != null && !world.isClient) {
                    world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                }
                return ItemActionResult.SUCCESS;
            }
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public List<String> getStateNames() {
        return this.script != null ? this.script.getStates().keySet().stream().toList() : List.of();
    }
    public List<String> getBlockApplicableStateNames() {
        return this.script != null ? this.script.getStates().entrySet().stream().flatMap(entry -> entry.getValue() instanceof State.ValueBackedState state && state.getType() != State.Type.STATE ? Stream.of(state.getName()) : Stream.empty()).toList(): List.of();
    }

    private @Nullable ScriptException loadScript(final ItemStack stack) {
        if (stack.isOf(Items.WRITABLE_BOOK) || stack.isOf(Items.WRITTEN_BOOK)) {
            try {
                this.bookStack = stack;
                String scriptText = extractScriptFromBook(bookStack);
                if (!scriptText.isEmpty()) {
                    this.script = RHDL.parse(scriptText, State::create, Stream.concat(ScriptFunction.builtins().stream(), List.of(
                        new ScriptFunction("excc", (script, args) -> {
                            if (args.size() == 1 && args.getFirst() instanceof String) {
                                return 0;
                            } else if (args.size() == 2 && args.getFirst() instanceof String && args.get(1) instanceof String) {
                                return 0;
                            } else {
                                throw new ScriptException("Function 'excc' expects a single/dual string value");
                            }
                        }, (script, ctx, args) -> {
                            if (this.getWorld() != null && this.getWorld().getServer() != null) {
                                if (args.size() == 2 && args.get(1) instanceof String stateName) {
                                    State targetState = script.getState(stateName);
                                    if (targetState != null) {
                                        for (Map.Entry<Direction, String> entry : faces.entrySet()) {
                                            Direction dir = entry.getKey();
                                            if (entry.getValue().equals(stateName)) {
                                                System.out.println("Dir: " + dir);
                                                float yaw = dir.asRotation();
                                                System.out.println("Yaw: " + yaw);

                                                if (this.getWorld() instanceof ServerWorld serverWorld) {
                                                    ServerCommandSource source = serverWorld.getServer().getCommandSource()
                                                            .withWorld(serverWorld)
                                                            .withPosition(Vec3d.ofCenter(pos))
                                                            .withRotation(getRotation(dir))
                                                            .withLevel(2)
                                                            .withSilent();

                                                    serverWorld.getServer().getCommandManager().executeWithPrefix(source, (String) args.getFirst());
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (this.getWorld() instanceof ServerWorld serverWorld) {
                                        ServerCommandSource source = serverWorld.getServer().getCommandSource()
                                                .withWorld(serverWorld)
                                                .withPosition(Vec3d.ofCenter(pos))
                                                .withRotation(new Vec2f(-90, 0))
                                                .withLevel(2)
                                                .withSilent();

                                        serverWorld.getServer().getCommandManager().executeWithPrefix(source, (String) args.getFirst());
                                    }
                                }


                            }
                            return BitString.zero(0);
                        })
                    ).stream()).toList(), Map.of());
                }
                return null;
            } catch (ScriptException throwable) {
                throwable.printStackTrace();
                return throwable;
            }
        }
        return null;
    }

    public static Vec2f getRotation(Direction facing) {
        return switch (facing) {
            case DOWN -> new Vec2f(90, 0);
            case UP -> new Vec2f(-90, 0);
            default -> new Vec2f(0, facing.asRotation());
        };
    }

    private static String extractScriptFromBook(ItemStack bookStack) {
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


    public void forceLogicRecomputation() {
        if (world != null && !world.isClient) {
            BlockState state = this.getCachedState();
            boolean changed = false;

            boolean hasFacing = state.contains(FPGABlock.FACING);
            boolean hasMirrored = state.contains(FPGABlock.MIRRORED);

            if (hasMirrored && state.get(FPGABlock.MIRRORED) != BlockMirror.NONE) {
                BlockMirror mirror = state.get(FPGABlock.MIRRORED);
                if (mirror == BlockMirror.LEFT_RIGHT) {
                    String north = faces.get(Direction.NORTH);
                    String south = faces.get(Direction.SOUTH);
                    updateFace(Direction.NORTH, south);
                    updateFace(Direction.SOUTH, north);
                } else if (mirror == BlockMirror.FRONT_BACK) {
                    String east = faces.get(Direction.EAST);
                    String west = faces.get(Direction.WEST);
                    updateFace(Direction.EAST, west);
                    updateFace(Direction.WEST, east);
                }

                changed = true;
            }

            if (hasFacing) {
                Direction currentDirection = state.get(FPGABlock.FACING);
                if (currentDirection != Direction.NORTH && !currentDirection.getAxis().isVertical()) {

                    String north = faces.get(Direction.NORTH);
                    String east = faces.get(Direction.EAST);
                    String south = faces.get(Direction.SOUTH);
                    String west = faces.get(Direction.WEST);

                    String newNorth, newEast, newSouth, newWest;

                    switch (currentDirection) {
                        case EAST -> {
                            newNorth = west;
                            newEast = north;
                            newSouth = east;
                            newWest = south;
                        }
                        case SOUTH -> {
                            newNorth = south;
                            newEast = west;
                            newSouth = north;
                            newWest = east;
                        }
                        case WEST -> {
                            newNorth = east;
                            newEast = south;
                            newSouth = west;
                            newWest = north;
                        }
                        default -> {
                            newNorth = north;
                            newEast = east;
                            newSouth = south;
                            newWest = west;
                        }
                    }

                    updateFace(Direction.NORTH, newNorth);
                    updateFace(Direction.EAST, newEast);
                    updateFace(Direction.SOUTH, newSouth);
                    updateFace(Direction.WEST, newWest);

                    changed = true;
                }
            }





            if (changed) {
                state = state.with(FPGABlock.FACING, Direction.NORTH);
                state = state.with(FPGABlock.MIRRORED, BlockMirror.NONE);
                world.setBlockState(
                        pos,
                        state,
                        Block.NOTIFY_LISTENERS | Block.NO_REDRAW
                );
                this.markDirty();
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }
        }
    }

    private void updateFace(Direction dir, String faceName) {
        if (faceName == null) {
            faces.remove(dir);
        } else {
            faces.put(dir, faceName);
        }
    }


    @Override
    public void tick(World world, BlockPos pos, BlockState state, FPGABlockEntity blockEntity) {
        if (this.script != null) {
            forceLogicRecomputation();
            if (!world.isClient) {
                this.script.evaluate();
            }

            boolean stateChanged = false;
            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.IN && getState(direction) instanceof State.ValueBackedState valueState) {
                    valueState.set(BitString.zero(valueState.size()));
                }
            }
            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.IN && getState(direction) instanceof State.ValueBackedState valueState) {
                    BitString newValue;
                    if (valueState.size() == 1) {
                        newValue = IDungeonWire.isReceivingDungeonWirePower(state, world, pos, direction) ? BitString.one() : BitString.zero();
                    } else {
                        int power = world.getEmittedRedstonePower(pos.offset(direction), direction.getOpposite());
                        newValue = getBitString(power);
                    }

                    if (!valueState.get().equals(newValue)) {
                        valueState.set(valueState.get().or(newValue));
                        stateChanged = true;
                    }
                }
            }

            if (stateChanged) {
                this.markDirty();
                world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            }

            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.OUT && getState(direction) instanceof State.ValueBackedState valueState) {
                    int currentPower = (valueState.size() == 1) ? (!valueState.get().isEmpty() ? 15 : 0) : getPower(valueState.get());

                    int previousPower = lastOutputPowers.getOrDefault(direction, -1);
                    if (previousPower != currentPower) {
                        lastOutputPowers.put(direction, currentPower);
                        BlockPos neighborPos = pos.offset(direction);
                        world.updateNeighbor(neighborPos, state.getBlock(), pos);
                    }
                }
            }
        }
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        if (this.script != null && !world.isClient) {
            if (getFaceType(face) == State.Type.OUT && getState(face) instanceof State.ValueBackedState valueState) {
                if (valueState.size() == 1) {
                    return !valueState.get().isEmpty();
                }
            }
        }
        return false;
    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (this.script != null) {
            if (getFaceType(face) == State.Type.OUT && getState(face) instanceof State.ValueBackedState valueState) {
                if (valueState.size() == 4) {
                    return getPower(valueState.get());
                }
            }
        }
        return 0;
    }

    public static int getPower(final BitString bitString) {
        int value = 0;
        if (bitString.get(0)) {
            value |= 0b0001;
        }
        if (bitString.get(1)) {
            value |= 0b0010;
        }
        if (bitString.get(2)) {
            value |= 0b0100;
        }
        if (bitString.get(3)) {
            value |= 0b1000;
        }
        return value;
    }

    private static @NotNull BitString getBitString(int power) {
        final BitString value = new BitString(4);
        if ((power & 0b0001) != 0) {
            value.set(0);
        }
        if ((power & 0b0010) != 0) {
            value.set(1);
        }
        if ((power & 0b0100) != 0) {
            value.set(2);
        }
        if ((power & 0b1000) != 0) {
            value.set(3);
        }
        return value;
    }


    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.bookStack.isEmpty()) {
            nbt.put("Stack", this.bookStack.encode(registryLookup));
        }
        if (script != null) {
            if (script.getStates() != null) {
                var encodedStates = BIT_STRING_MAP_CODEC.encodeStart(NbtOps.INSTANCE, script.getStates().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> ((State.ValueBackedState) entry.getValue()).get(), (p, n) -> n, HashMap::new)));
                encodedStates.result().ifPresent(element -> nbt.put("States", element));
            }
            if (script.getPreviousState() != null && !script.getPreviousState().contains(null)) {
                var encodedPreviousState = BIT_STRING_LIST_CODEC.encodeStart(NbtOps.INSTANCE, script.getPreviousState());
                encodedPreviousState.result().ifPresent(element -> nbt.put("Previous", element));
            }
        }
        if (this.faces != null) {
            FACE_MAP_CODEC.encodeStart(NbtOps.INSTANCE, this.faces).result().ifPresent(element -> nbt.put("Faces", element));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Stack")) loadScript(ItemStack.fromNbt(registryLookup, nbt.getCompound("Stack")).orElse(ItemStack.EMPTY));
        if (script != null) {
            if (nbt.contains("States")) {
                var decodeStates = BIT_STRING_MAP_CODEC.decode(NbtOps.INSTANCE, nbt.get("States"));
                if (decodeStates.result().isPresent()) {
                    decodeStates.result().get().getFirst().forEach((key, value) -> {
                        try {
                            if (script.getState(key) instanceof final State.ValueBackedState state) {
                                state.set(value);
                            }
                        } catch (Exception ignored) {
                        }
                    });
                }
            }
            if (nbt.contains("Previous")) {
                var decodeStates = BIT_STRING_LIST_CODEC.decode(NbtOps.INSTANCE, nbt.get("Previous"));
                if (decodeStates.result().isPresent()) {
                    var list = decodeStates.result().get().getFirst();
                    if (list.size() == script.getPreviousState().size()) {
                        script.getPreviousState().clear();
                        script.getPreviousState().addAll(list);
                    }
                }
            }
        }
        if (nbt.contains("Faces")) {
            this.faces = FACE_MAP_CODEC.decode(NbtOps.INSTANCE, nbt.get("Faces")).result().get().getFirst();
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); }

    public Integer getFaceIndex(Direction dir) {
        return perFaceIndex.get(dir);
    }

    public void setFaceIndex(Direction dir, Integer index) {
        this.perFaceIndex.put(dir, index);
    }


    /*@Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return false; }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return null; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); } */
}
package io.github.tobyrue.btc.wires.circuit;

import com.mojang.serialization.Codec;
import io.github.sianabeeblebrox.rhdl.*;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
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
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
                            } else {
                                throw new ScriptException("Function 'excc' expects a single string value");
                            }
                        }, (script, ctx, args) -> {
                            if (this.getWorld() != null && this.getWorld().getServer() != null) {
                                this.getWorld().getServer().getCommandManager().executeWithPrefix(this.getWorld().getServer().getCommandSource(), (String) args.getFirst());
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
        this.markDirty();
        return Direction.fromHorizontal(horizontalOffset);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FPGABlockEntity blockEntity) {
        if (this.script != null) {
            if (!world.isClient) {
                this.script.evaluate();
            }
            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.IN && getState(direction) instanceof State.ValueBackedState valueState) {
                    valueState.set(BitString.zero(valueState.size()));
                    this.markDirty();
                    if (!world.isClient) {
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                }
            }
            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.IN && getState(direction) instanceof State.ValueBackedState valueState) {
                    if (valueState.size() == 1) {
                        valueState.set(valueState.get().or(IDungeonWire.isReceivingDungeonWirePower(state, world, pos, direction) ? BitString.one() : BitString.zero()));
                    } else if (valueState.size() == 4) {
                        int power = world.getEmittedRedstonePower(pos.offset(direction), direction.getOpposite());
                        final BitString value = getBitString(power);
                        valueState.set(valueState.get().or(value));
                    }
                    this.markDirty();
                    if (!world.isClient) {
                        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
                    }
                }
                if (getFaceType(direction) == State.Type.OUT && getState(direction) instanceof State.ValueBackedState valueState) {
                    int currentPower = 0;

                    if (valueState.size() == 1) {
                        currentPower = !valueState.get().isEmpty() ? 15 : 0;
                    } else if (valueState.size() == 4) {
                        currentPower = getPower(valueState.get());
                    }

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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }


    /*@Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return false; }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return null; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); } */
}
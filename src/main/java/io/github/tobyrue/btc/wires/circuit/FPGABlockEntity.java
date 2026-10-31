package io.github.tobyrue.btc.wires.circuit;

import com.mojang.serialization.Codec;
import io.github.sianabeeblebrox.rhdl.*;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
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
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private Script script;
    private ItemStack bookStack = ItemStack.EMPTY;

    public void setFace(Direction direction, String state) {
        this.faces.put(direction, state);
        this.markDirty();
    }


    public @Nullable State getState(Direction direction) {
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
        super(ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY, pos, state);
    }

    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isCreativeLevelTwoOp()) {
            if (loadScript(stack) instanceof final ScriptException scriptException) {
                player.sendMessage(Text.literal(scriptException.getMessage()).formatted(Formatting.RED));
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else {
                this.markDirty();
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
                this.script = RHDL.parse(extractScriptFromBook(bookStack), State::create, ScriptFunction.builtins(), Map.of());
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
        //TODO needs mark dirty?
        return Direction.fromHorizontal(horizontalOffset);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FPGABlockEntity blockEntity) {
        if (this.script != null && !world.isClient) {
            this.script.evaluate();
            for (Direction direction : Direction.values()) {
                if (getFaceType(direction) == State.Type.IN && getState(direction) instanceof State.ValueBackedState valueState) {
                    if (valueState.size() == 1) {
                        valueState.set(IDungeonWire.isReceivingDungeonWirePower(state, world, pos, direction) ? BitString.one() : BitString.zero());
                    } else if (valueState.size() == 4) {
                        int power = world.getEmittedRedstonePower(pos.offset(direction), direction.getOpposite());
                        final BitString value = getBitString(power);
                        valueState.set(value);
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
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction face) {
        if (this.script != null) {
            if (getFaceType(face) == State.Type.OUT && getState(face) instanceof State.ValueBackedState valueState) {
                if (valueState.size() == 4) {
                    return getPower(valueState.get());
                }
            }
        }
        return 0;
    }

    private static @NotNull int getPower(final BitString bitString) {
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
        if (!this.bookStack.isEmpty()) nbt.put("Stack", this.bookStack.encode(registryLookup));
        if (script != null) {
            var encodedStates = BIT_STRING_MAP_CODEC.encodeStart(NbtOps.INSTANCE, script.getStates().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> ((State.ValueBackedState) entry.getValue()).get(), (p, n) -> n, HashMap::new)));
            if (encodedStates.result().isPresent()) {
                nbt.put("States", encodedStates.result().get());
            }
            var encodedPreviousState = BIT_STRING_LIST_CODEC.encodeStart(NbtOps.INSTANCE, script.getPreviousState());
            if (encodedPreviousState.result().isPresent()) {
                nbt.put("Previous", encodedPreviousState.result().get());
            }
        }
        nbt.put("Faces", FACE_MAP_CODEC.encodeStart(NbtOps.INSTANCE, this.faces).result().get());
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
        } else {
            this.faces = HashMap.newHashMap(6);
        }
    }




    /*@Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return false; }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return null; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); } */
}
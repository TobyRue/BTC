package io.github.tobyrue.btc.wires.circuit;

import com.mojang.serialization.Codec;
import io.github.sianabeeblebrox.rhdl.*;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FPGABlockEntity extends BlockEntity /*implements IDungeonWire, IWireConnectionHelper */ {


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

    private State.ValueBackedState event = null;
    private Script script;
    private ItemStack bookStack = ItemStack.EMPTY;

    public enum Event {
        INITIALIZE,
        TICK,
        RANDOM_TICK,
        UPDATE;
    }


    public FPGABlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY, pos, state);
    }

    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.getMainHandStack().isOf(Items.WRITABLE_BOOK) && player.isCreativeLevelTwoOp()) {
            try {
                this.bookStack = stack;
                this.script = RHDL.parse(extractScriptFromBook(bookStack), this::create, ScriptFunction.builtins(), Map.of());
                System.out.println(script.evaluate());
                System.out.println(script.getStates().entrySet().stream().map(entry -> String.format("%s=%s", entry.getKey(), ((State.ValueBackedState) entry.getValue()).get())).collect(Collectors.joining("\n")));
                return ItemActionResult.SUCCESS;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
        return ItemActionResult.FAIL;
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
        return Direction.fromHorizontal(horizontalOffset);
    }

    private void triggerEvent(Event event) {
        if (this.event != null && this.script != null) {
            var eventCount = Event.values().length;
            var mask = BitString.zero(eventCount);
            this.event.set(BitString.zero(eventCount));
            script.evaluate();
            mask.set(event.ordinal());
            this.event.set(mask);
            script.evaluate();
        }
    }

    private State create(final String name, final State.Type type, final Map<String, Object> layout) {
        if (name.equals("event")) {
            if (type != State.Type.IN || !layout.isEmpty()) {
                throw new ScriptException();
            }
            return event = (State.ValueBackedState) create(name, type, Map.of("size", Event.values().length));
        } else {
            return create(name, type, layout);
        }
    }


    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

    }

    /*@Override public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) { return false; }
    @Override public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public void setConnection(Direction face, WireBlock.ConnectionType type, World world, BlockState state, BlockPos pos) { }
    @Override public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) { return null; }
    @Override public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) { return null; }
    @Override public Packet<ClientPlayPacketListener> toUpdatePacket() { return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) { return createNbt(registryLookup); } */
}
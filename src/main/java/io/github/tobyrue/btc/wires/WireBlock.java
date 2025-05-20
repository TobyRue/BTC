package io.github.tobyrue.btc.wires;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableBiMap;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.function.Supplier;

public class WireBlock extends Block implements IWireConnect, IHaveWrenchActions {

    public static final Supplier<ImmutableBiMap<EnumProperty<ConnectionType>, Direction>> CONNECTION_TO_DIRECTION = Suppliers.memoize(() ->
        ImmutableBiMap.<EnumProperty<ConnectionType>, Direction>builder()
            .put(EnumProperty.of("connection_up", ConnectionType.class), Direction.UP)
            .put(EnumProperty.of("connection_down", ConnectionType.class), Direction.DOWN)
            .put(EnumProperty.of("connection_north", ConnectionType.class), Direction.NORTH)
            .put(EnumProperty.of("connection_east", ConnectionType.class), Direction.EAST)
            .put(EnumProperty.of("connection_south", ConnectionType.class), Direction.SOUTH)
            .put(EnumProperty.of("connection_west", ConnectionType.class), Direction.WEST)
            .build()
     );

    @FunctionalInterface
    interface ApplyOperator {
        boolean apply(Boolean... args);
    }

    public enum Operator implements StringIdentifiable, ApplyOperator {
        TRUE("true", 0x28CC3B /* Green */,args -> true),
        FALSE("false", 0xD733C4 /* Magenta */, args -> false),
        OR("or", 0xCCCC28 /* Yellow */,args -> Arrays.stream(args).anyMatch(b -> b)),
        AND("and", 0xCC4128 /* Red */,args -> Arrays.stream(args).allMatch(b -> b)),
        NOR("nor", 0x3333D7 /* Blue */,args -> !OR.apply(args)),
        NAND("nand", 0x33BED7 /* Cyan */,args ->  !AND.apply(args)),
        XOR("xor", 0xCC9528 /* Orange */,args -> Arrays.stream(args).filter(b -> b).toList().size() == 1), // 1 and only 1
        XNOR("xnor", 0x8A28CC /* Purple */,args -> !XOR.apply(args));

        private final String name;
        private final ApplyOperator operator;
        private final int color;

        Operator(String name, int color, ApplyOperator operator) {
            this.name = name;
            this.operator = operator;
            this.color = color;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public boolean apply(Boolean ...values) {
            return this.operator.apply(values);
        }

        public int getColor() {
            return color;
        }
    }

    public enum ConnectionType implements StringIdentifiable {
        NONE("none"),
        INPUT("input"),
        OUTPUT("output");

        private final String name;

        ConnectionType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }

    public static final EnumProperty<Operator> OPERATOR = EnumProperty.of("operator", Operator.class);
    public static final BooleanProperty LATCHED = BooleanProperty.of("latched");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    protected final boolean IMMUTABLE;

    public WireBlock(Settings settings, boolean immutable) {
        super(settings);
        IMMUTABLE = immutable;
        this.setDefaultState(CONNECTION_TO_DIRECTION.get().keySet().stream().reduce(
                this.stateManager.getDefaultState().with(OPERATOR, Operator.OR).with(LATCHED, false).with(POWERED, false),
                (acc, con) -> acc.with(con, ConnectionType.INPUT),
                (lhs, rhs) -> {
                    throw new RuntimeException("Don't fold in parallel");
                }
        ));
    }

    @Override
    public ActionResult onWrenchUse(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide) {
        ItemStack heldItem = player.getStackInHand(hand);
        WrenchType type = stack.getOrDefault(BTC.WRENCH_TYPE, WrenchType.ROTATE);
        if (heldItem.isIn(BTC.WRENCHES) && (player.isCreative() || !this.IMMUTABLE)) {
            if (hand == Hand.OFF_HAND && type == WrenchType.WIRE) {
                var newState = state.cycle(OPERATOR);
                world.setBlockState(pos, newState);
                if (world.isClient) {
                    player.sendMessage(Text.translatable("block.btc.wire.change_operator", Text.translatable("block.btc.wire.operator." + newState.get(OPERATOR).asString())), true);
                }
                return ActionResult.SUCCESS;
            } else if (hand == Hand.MAIN_HAND && type == WrenchType.WIRE) {
                var property = CONNECTION_TO_DIRECTION.get().inverse().get(hitSide);
                var newState = state.cycle(property);
                world.setBlockState(pos, newState);
                if (world.isClient) {
                    player.sendMessage(Text.translatable("block.btc.wire.change_connection", Text.translatable("block.btc.wire.face." + hitSide.asString()), Text.translatable("block.btc.wire.connection." + newState.get(property).asString())), true);
                }
                return ActionResult.SUCCESS;
            } else if (hand == Hand.MAIN_HAND && type == WrenchType.WIRE_COMPLEX) {
                Direction dir = stack.getOrDefault(BTC.WRENCH_DIRECTION, Direction.UP);
                var property = switch (dir) {
                    case DOWN -> CONNECTION_TO_DIRECTION.get().inverse().get(Direction.DOWN);
                    case UP ->  CONNECTION_TO_DIRECTION.get().inverse().get(Direction.UP);
                    case NORTH ->  CONNECTION_TO_DIRECTION.get().inverse().get(Direction.NORTH);
                    case EAST ->  CONNECTION_TO_DIRECTION.get().inverse().get(Direction.EAST);
                    case SOUTH ->  CONNECTION_TO_DIRECTION.get().inverse().get(Direction.SOUTH);
                    case WEST ->  CONNECTION_TO_DIRECTION.get().inverse().get(Direction.WEST);
                };
                var newState = state.cycle(property);
                world.setBlockState(pos, newState);
                if (world.isClient) {
                    player.sendMessage(Text.translatable("block.btc.wire.change_connection", Text.translatable("block.btc.wire.face." + dir), Text.translatable("block.btc.wire.connection." + newState.get(property).asString())), true);
                }
                return ActionResult.SUCCESS;
            }
        }
        if (world.isClient) {
            player.sendMessage(Text.literal("Has power: " + hasPower(state, world, pos)));
        }
        return ActionResult.FAIL;
    }

//    @Override
//    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//
//        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
//    }

    protected boolean hasPower(BlockState state, World world, BlockPos pos) {
        return state.get(OPERATOR).apply(
            CONNECTION_TO_DIRECTION.get().entrySet().stream()
                .filter(entry -> state.get(entry.getKey()) == ConnectionType.INPUT)
                .map(entry -> {
                    var offsetState = world.getBlockState(pos.offset(entry.getValue()));
                    var oppositeConnection = CONNECTION_TO_DIRECTION.get().inverse().get(entry.getValue().getOpposite());
                    return offsetState.contains(WireBlock.POWERED)
                        && offsetState.contains(oppositeConnection)
                        && offsetState.get(oppositeConnection) == ConnectionType.OUTPUT
                        && offsetState.get(POWERED);
                }).toArray(Boolean[]::new)
        );
    }

    //    @Nullable
//    @Override
//    public BlockState getPlacementState(ItemPlacementContext ctx) {
//        return CONNECTION_TO_DIRECTION.get().keySet().stream().reduce(
//                this.getDefaultState().with(OPERATOR, Operator.OR).with(LATCHED, false).with(POWERED, false),
//                (acc, con) -> acc.with(con, ConnectionType.INPUT),
//                (lhs, rhs) -> {
//                    throw new RuntimeException("Don't fold in parallel");
//                }
//        );
//    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPERATOR);
        for (var conn : CONNECTION_TO_DIRECTION.get().keySet())
            builder.add(conn);
        builder.add(LATCHED);
        builder.add(POWERED);
    }
}

package io.github.tobyrue.btc.wires;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableBiMap;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
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
//        REDSTONE("redstone", 0xFF745B /* Coral */,args -> false);

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
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final BooleanProperty REDSTONE = BooleanProperty.of("redstone");
    public static final IntProperty DELAY = IntProperty.of("delay", 0 ,7);

    protected final boolean IMMUTABLE;

    public WireBlock(Settings settings, boolean immutable) {
        super(settings);
        IMMUTABLE = immutable;
        this.setDefaultState(CONNECTION_TO_DIRECTION.get().keySet().stream().reduce(
                this.stateManager.getDefaultState().with(OPERATOR, Operator.OR).with(POWERED, false).with(REDSTONE, false).with(DELAY, 0),
                (acc, con) -> acc.with(con, ConnectionType.INPUT),
                (lhs, rhs) -> {
                    throw new RuntimeException("Don't fold in parallel");
                }
        ));
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        updatePowerAndNotify(state, world, pos);
    }
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!world.isClient && state.getBlock() instanceof WireBlock) {
            if (state.getBlock() != newState.getBlock()) {
                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = pos.offset(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);
                    var property = state.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(direction.getOpposite()));

                    if (neighborState.getBlock() instanceof IDungeonWireAction action && property == WireBlock.ConnectionType.OUTPUT) {
                        action.onDungeonWireChange(neighborState, world, neighborPos, false);
                        neighborUpdate(state, world, pos, this, pos, true);
                    }
                }
            } else {
                if (world instanceof ServerWorld serverWorld) {
                    boolean newPowered = hasPower(state, world, pos);
                    for (Direction direction : Direction.values()) {
                        BlockPos neighborPos = pos.offset(direction);
                        BlockState neighborState = world.getBlockState(neighborPos);
                        if (neighborState.getBlock() instanceof IDungeonWireAction action) {
                            action.onDungeonWireChange(neighborState, world, neighborPos, newPowered);
                        }
                    }
                }
            }
        }
    }



    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos,
                                  Block sourceBlock, BlockPos sourcePos, boolean notify) {

        if (world instanceof ServerWorld serverWorld) {
            if (state.get(DELAY) > 0) {
                world.scheduleBlockTick(pos, this, state.get(DELAY));
            } else {
                updatePowerAndNotify(state, serverWorld, pos);
            }
        }

        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    private void updatePowerAndNotify(BlockState state, ServerWorld world, BlockPos pos) {
        boolean newPowered = hasPower(state, world, pos);

        if (state.get(POWERED) == newPowered) return;

        BlockState newState = state.with(POWERED, newPowered);
        world.setBlockState(pos, newState, Block.NOTIFY_ALL);

        for (Direction direction : Direction.values()) {
            var property = CONNECTION_TO_DIRECTION.get().inverse().get(direction);
            if (state.get(property) != ConnectionType.OUTPUT) continue;

            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof IDungeonWireAction action) {
                action.onDungeonWireChange(neighborState, world, neighborPos, newPowered);
            }
        }
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
            } else if (hand == Hand.MAIN_HAND && type == WrenchType.WIRE_DELAY) {
                if (!player.isSneaking()) {
                    var newState = state.cycle(DELAY);
                    world.setBlockState(pos, newState);
                    if (world.isClient) {
                        if (state.get(DELAY) != 7) {
                            player.sendMessage(Text.translatable("block.btc.wire.delay.change_delay", state.get(DELAY) + 1), true);
                        } else {
                            player.sendMessage(Text.translatable("block.btc.wire.delay.change_delay", 0), true);
                        }
                    }
                    return ActionResult.SUCCESS;
                } else {
                    world.setBlockState(pos, state.with(DELAY, 0));
                    if (world.isClient) {
                        player.sendMessage(Text.translatable("block.btc.wire.delay.change_delay", 0), true);
                    }
                    return ActionResult.SUCCESS;
                }

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

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        ImmutableBiMap<EnumProperty<ConnectionType>, Direction> map = CONNECTION_TO_DIRECTION.get();

        BlockState mirroredState = state;

        for (var entry : map.entrySet()) {
            EnumProperty<ConnectionType> property = entry.getKey();
            Direction originalDir = entry.getValue();
            Direction mirroredDir = getMirroredDirection(originalDir, mirror);
            EnumProperty<ConnectionType> mirroredProperty = map.inverse().get(mirroredDir);

            if (mirroredProperty != null) {
                mirroredState = mirroredState.with(mirroredProperty, state.get(property));
            }
        }

        return mirroredState;
    }

    private Direction getMirroredDirection(Direction direction, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> switch (direction) {
                case NORTH -> Direction.SOUTH;
                case SOUTH -> Direction.NORTH;
                default -> direction;
            };
            case FRONT_BACK -> switch (direction) {
                case EAST -> Direction.WEST;
                case WEST -> Direction.EAST;
                default -> direction;
            };
            default -> direction;
        };
    }
    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        ImmutableBiMap<EnumProperty<ConnectionType>, Direction> map = CONNECTION_TO_DIRECTION.get();

        BlockState rotatedState = state;

        for (var entry : map.entrySet()) {
            EnumProperty<ConnectionType> property = entry.getKey();
            Direction originalDir = entry.getValue();
            Direction rotatedDir = rotation.rotate(originalDir);
            EnumProperty<ConnectionType> rotatedProperty = map.inverse().get(rotatedDir);

            if (rotatedProperty != null) {
                rotatedState = rotatedState.with(rotatedProperty, state.get(property));
            }
        }

        return rotatedState;
    }
    protected boolean hasPower(BlockState state, World world, BlockPos pos) {
        return state.get(OPERATOR).apply(
                CONNECTION_TO_DIRECTION.get().entrySet().stream()
                        .filter(entry -> state.get(entry.getKey()) == ConnectionType.INPUT)
                        .map(entry -> {
                            var offsetState = world.getBlockState(pos.offset(entry.getValue()));
                            var oppositeConnection = CONNECTION_TO_DIRECTION.get().inverse().get(entry.getValue().getOpposite());
                            return (offsetState.contains(WireBlock.POWERED)
                                    && offsetState.contains(oppositeConnection)
                                    && offsetState.get(oppositeConnection) == ConnectionType.OUTPUT
                                    && offsetState.get(POWERED)) ||
                                    (world.getEmittedRedstonePower(pos.offset(entry.getValue()), entry.getValue().getOpposite()) > 0 && state.get(REDSTONE));
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
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPERATOR);
        for (var conn : CONNECTION_TO_DIRECTION.get().keySet())
            builder.add(conn);
        builder.add(POWERED);
        builder.add(REDSTONE);
        builder.add(DELAY);
    }
}

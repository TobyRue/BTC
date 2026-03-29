package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModComponents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Arrays;

public class WireBlock extends Block implements ModBlockEntityProvider<WireBlockEntity>, IDungeonWire, IHaveWrenchActions {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    /**
     * Mirrored is only switched with the last state if for example it is mirrored along North to South while the block has a facing property of north, otherwise it will not change.
     * This is used to detect if the BlockBox needs to be mirrored, this is done by returning a new distance array with the opposite axis it was mirrored on with the same numbers but negative.
     */
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);

    @Override
    public BlockEntityType<WireBlockEntity> getBlockEntityType() {
        return ModBlockEntities.WIRE_BLOCK_ENTITY;
    }

    public enum ConnectionType implements StringIdentifiable {
        NONE("none"),
        INPUT("input"),
        OUTPUT("output"),
        REDSTONE_INPUT("redstone_input"),
        REDSTONE_OUTPUT("redstone_output");
        private final String name;
        ConnectionType(String name) { this.name = name; }
        @Override public String asString() { return name; }
    }

    @FunctionalInterface
    interface ApplyOperator {
        boolean apply(Boolean... args);
    }

    public enum Operator implements StringIdentifiable, WireBlockSlow.ApplyOperator {
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

    public WireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH).with(MIRRORED, BlockMirror.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
        builder.add(FACING);
        builder.add(MIRRORED);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
                if (wire.getDelay() == 0) {
                    wire.updatePower();
                } else {
                    world.scheduleBlockTick(pos, this, wire.getDelay());
                }
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            wire.updatePower();
        }
        super.scheduledTick(state, world, pos, random);
    }


    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos) instanceof WireBlockEntity wireBlock && wireBlock.isEmittingRedstonePower(state, world, pos, direction.getOpposite()) ? 15 : 0;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(MIRRORED, mirror);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            super.onStateReplaced(state, world, pos, newState, moved);
            return;
        }

        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            if (state.get(FACING) != newState.get(FACING)) {
                BlockRotation rotation = getRotationBetween(state.get(FACING), newState.get(FACING));
                wire.rotateConnections(rotation);
            }

            if (newState.get(MIRRORED) != BlockMirror.NONE) {
                wire.mirrorConnections(newState.get(MIRRORED));
                world.setBlockState(pos, newState.with(MIRRORED, BlockMirror.NONE), Block.NOTIFY_LISTENERS | Block.NO_REDRAW);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private BlockRotation getRotationBetween(Direction oldDir, Direction newDir) {
        if (oldDir == newDir) return BlockRotation.NONE;
        if (oldDir.rotateYClockwise() == newDir) return BlockRotation.CLOCKWISE_90;
        if (oldDir.getOpposite() == newDir) return BlockRotation.CLOCKWISE_180;
        return BlockRotation.COUNTERCLOCKWISE_90;
    }
    @Override
    public ActionResult onWrenchUse(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide) {

        if (!(world.getBlockEntity(pos) instanceof WireBlockEntity wire) || !stack.isOf(ModItems.COPPER_WRENCH)) {
            return ActionResult.PASS;
        }

        WrenchType type = stack.getOrDefault(ModComponents.WRENCH_TYPE, WrenchType.ROTATE);

        if (type == WrenchType.WIRE) {
            if (hand == Hand.OFF_HAND) {
                wire.cycleOperator();
                if (world.isClient) {
                    player.sendMessage(Text.translatable("block.btc.wire.change_operator",
                            Text.translatable("block.btc.wire.operator." + wire.getOperator().asString())), true);
                }
            } else {
                wire.cycleConnection(hitSide);
                if (world.isClient) {
                    player.sendMessage(Text.translatable("block.btc.wire.change_connection",
                            Text.translatable("block.btc.wire.face." + hitSide.asString()),
                            Text.translatable("block.btc.wire.connection." + wire.getConnection(hitSide).asString())), true);
                }
            }
            return ActionResult.SUCCESS;
        } else if (type == WrenchType.WIRE_DELAY) {
            int newDelay = player.isSneaking() ? 0 : (wire.getDelay() + 1) % 8;
            wire.setDelay(newDelay);
            if (world.isClient) {
                player.sendMessage(Text.translatable("block.btc.wire.delay.change_delay", newDelay), true);
            }
            return ActionResult.SUCCESS;
        } else if (hand == Hand.MAIN_HAND && type == WrenchType.WIRE_COMPLEX) {
            Direction dir = stack.getOrDefault(ModComponents.WRENCH_DIRECTION, Direction.UP);

            var next = wire.cycleConnection(dir);

            if (world.isClient) {
                player.sendMessage(Text.translatable("block.btc.wire.change_connection", Text.translatable("block.btc.wire.face." + dir), Text.translatable("block.btc.wire.connection." + next.name)), true);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.CONSUME;
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return world.getBlockEntity(pos) instanceof WireBlockEntity wireBlock && wireBlock.isEmittingDungeonWirePower(state, world, pos, face);
    }
}
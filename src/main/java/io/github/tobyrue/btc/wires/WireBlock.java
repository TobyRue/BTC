package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.enums.IWrenchType;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireOperatorHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

public class WireBlock extends Block implements ModBlockEntityProvider<WireBlockEntity>, IDungeonWire, IWireDelayHelper, IWireConnectionHelper, IWireOperatorHelper, IOnBlockUpdate {
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

    @Override
    public void setConnection(Direction face, ConnectionType connectionType, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            entity.setConnection(face, connectionType, world, state, pos);
        }
    }

    @Override
    public ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.cycleConnection(face, world, state, pos);
        }
        return ConnectionType.NONE;
    }

    @Override
    public ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.getConnection(face, world, state, pos);
        }
        return ConnectionType.NONE;
    }

    @Override
    public Map<Direction, ConnectionType> getConnections(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.getConnections(world, state, pos);
        }
        return Map.of();
    }

    @Override
    public void setDelay(int delay, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            entity.setDelay(delay, world, state, pos);
        }
    }

    @Override
    public int getDelay(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.getDelay(world, state, pos);
        }
        return 0;
    }

    @Override
    public void setOperator(Operator op, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            entity.setOperator(op, world, state, pos);
        }
    }

    @Override
    public Operator cycleOperator(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.cycleOperator(world, state, pos);
        }
        return Operator.OR;
    }

    @Override
    public Operator getOperator(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity entity) {
            return entity.getOperator(world, state, pos);
        }
        return Operator.OR;
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
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        this.onUpdate(world, pos, state);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        this.onUpdate(world, pos, state);

        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            Direction currentFacing = state.get(FACING);
            BlockMirror currentMirror = state.get(MIRRORED);

            if (currentFacing != Direction.NORTH || currentMirror != BlockMirror.NONE) {
                if (currentMirror != BlockMirror.NONE) {
                    wire.mirrorConnections(currentMirror);
                }

                if (currentFacing != Direction.NORTH) {
                    BlockRotation rot = getRotationBetween(Direction.NORTH, currentFacing);
                    wire.rotateConnections(rot);
                }

                BlockState newState = state.with(FACING, Direction.NORTH).with(MIRRORED, BlockMirror.NONE);
                world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS | Block.NO_REDRAW);

                wire.updatePower();
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

    private BlockRotation getRotationBetween(Direction oldDir, Direction newDir) {
        if (oldDir == newDir) return BlockRotation.NONE;
        if (oldDir.rotateYClockwise() == newDir) return BlockRotation.CLOCKWISE_90;
        if (oldDir.getOpposite() == newDir) return BlockRotation.CLOCKWISE_180;
        return BlockRotation.COUNTERCLOCKWISE_90;
    }


    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return world.getBlockEntity(pos) instanceof WireBlockEntity wireBlock && wireBlock.isEmittingDungeonWirePower(state, world, pos, face);
    }

    @Override
    public void onUpdate(World world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            if (wire.getDelay(world, state, pos) == 0) {
                wire.updatePower();
            } else if (!world.getBlockTickScheduler().isQueued(pos, this)) {
                world.scheduleBlockTick(pos, this, wire.getDelay(world, state, pos));
            }
        }
    }
}
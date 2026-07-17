package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireOperatorHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
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
        TRUE("true", 0x28CC3B, args -> true),
        FALSE("false", 0xD733C4, args -> false),
        OR("or", 0xCCCC28, args -> Arrays.stream(args).anyMatch(b -> b)),
        AND("and", 0xCC4128, args -> Arrays.stream(args).allMatch(b -> b)),
        NOR("nor", 0x3333D7, args -> !OR.apply(args)),
        NAND("nand", 0x33BED7, args -> !AND.apply(args)),
        XOR("xor", 0xCC9528, args -> Arrays.stream(args).filter(b -> b).toList().size() == 1),
        XNOR("xnor", 0x8A28CC, args -> !XOR.apply(args));

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

        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            Direction currentFacing = state.get(FACING);
            BlockMirror currentMirror = state.get(MIRRORED);

            if (currentFacing != Direction.NORTH || currentMirror != BlockMirror.NONE) {
                if (currentMirror != BlockMirror.NONE) wire.mirrorConnections(currentMirror);
                if (currentFacing != Direction.NORTH) wire.rotateConnections(getRotationBetween(Direction.NORTH, currentFacing));

                BlockState newState = state.with(FACING, Direction.NORTH).with(MIRRORED, BlockMirror.NONE);
                world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS | Block.NO_REDRAW);
            }
        }

        this.onUpdate(world, pos, state);
    }


    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
        super.onBlockAdded(state, world, pos, oldState, moved);

        if (!world.isClient) {
            if (moved) {
                world.scheduleBlockTick(pos, this, 1);
            } else {
                this.onUpdate(world, pos, state);

                for (Direction direction : Direction.values()) {
                    BlockPos neighborPos = pos.offset(direction);
                    BlockState neighborState = world.getBlockState(neighborPos);

                    if (neighborState.getBlock() instanceof IOnBlockUpdate updater) {
                        updater.onUpdate(world, neighborPos, neighborState);
                    }
                    world.updateNeighbor(neighborPos, this, pos);
                }
            }
        }
    }

//    @Override
//    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
//        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
//            wire.setPower(wire.getScheduledPower());
//        }
//        super.scheduledTick(state, world, pos, random);
//    }


    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        if (world.getBlockEntity(pos) instanceof WireBlockEntity wire) {
            wire.onUpdate(world, pos, state);

            wire.setPower(wire.getScheduledPower());

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);

                if (neighborState.getBlock() instanceof IOnBlockUpdate updater) {
                    updater.onUpdate(world, neighborPos, neighborState);
                }
                world.updateNeighbor(neighborPos, this, pos);
            }
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
        if (world.getBlockEntity(pos) instanceof IOnBlockUpdate wire) {
            wire.onUpdate(world, pos, state);

        }
    }

//    private boolean newPower(World world, BlockPos pos, BlockState state) {
//        return world.getBlockEntity(pos) instanceof WireBlockEntity wire && wire.getOperator(world, state, pos).apply(
//                wire.getConnections(world, state, pos).entrySet().stream()
//                        .filter(e -> e.getValue() == ConnectionType.INPUT || e.getValue() == ConnectionType.REDSTONE_INPUT)
//                        .map(e -> {
//                            Direction direction = e.getKey();
//                            BlockPos neighborPos = pos.offset(direction);
//                            BlockState neighborState = world.getBlockState(neighborPos);
//                            return (e.getValue() == ConnectionType.INPUT && neighborState.getBlock() instanceof IDungeonWire w && w.isEmittingDungeonWirePower(neighborState, world, neighborPos, direction.getOpposite()))
//                                    || (e.getValue() == ConnectionType.REDSTONE_INPUT && world.getEmittedRedstonePower(neighborPos, direction) > 0);
//                        }).toArray(Boolean[]::new)
//        );
//    }
}
package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class RecessedStairsBlock extends HorizontalConnectingBlock {
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final MapCodec<RecessedStairsBlock> CODEC = RecessedStairsBlock.createCodec(RecessedStairsBlock::new);
    private static final VoxelShape EMPTY = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0, 0, 0)
    );
    private static final VoxelShape BOTTOM_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1),
            VoxelShapes.cuboid(0.25, 0.5, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape BOTTOM_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.5, 0, 0.75, 1, 0.25)
    );
    private static final VoxelShape BOTTOM_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0.5, 0.25, 1, 1, 0.75)
    );
    private static final VoxelShape BOTTOM_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.5, 0.75, 0.75, 1, 1)
    );
    private static final VoxelShape BOTTOM_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0.25, 0.25, 1, 0.75)
    );

    private static final VoxelShape TOP_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
    );
    private static final VoxelShape TOP_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.5, 0.25)
    );
    private static final VoxelShape TOP_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0, 0.25, 1, 0.5, 0.75)
    );
    private static final VoxelShape TOP_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.75, 0.75, 0.5, 1)
    );
    private static final VoxelShape TOP_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.25, 0.25, 0.5, 0.75)
    );

    protected RecessedStairsBlock(Settings settings) {
        super(0, 0, 0, 0, 0, settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(HALF, BlockHalf.BOTTOM).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        Direction facing = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        BlockState state = this.getDefaultState()
                .with(HALF, ctx.getSide() == Direction.DOWN ? BlockHalf.TOP : BlockHalf.BOTTOM)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        for (Direction dir : Direction.Type.HORIZONTAL) {
            state = state.with(getProperty(dir), false);
        }

//        if (facing.getOpposite().getAxis() == Direction.Axis.Z) {
//            state = state.with(EAST, true).with(WEST, true);
//        } else {
//            state = state.with(NORTH, true).with(SOUTH, true);
//        }

        return state;
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction dir,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if (!dir.getAxis().isHorizontal()) {
            return state;
        }

        // =========================
        // PANE / BAR MODE
        // =========================
        boolean connect =
                neighborState.isIn(BTC.RECESSED_STAIR) ||
                        neighborState.isIn(BlockTags.FENCES) ||
                        neighborState.isIn(BlockTags.WALLS) ||
                        neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite());

        return state.with(getProperty(dir), connect);
    }


    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighbors(pos, this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    private static BooleanProperty getProperty(Direction direction) {
        switch (direction) {
            case NORTH -> {
                return NORTH;
            }
            case EAST -> {
                return EAST;
            }
            case SOUTH -> {
                return SOUTH;
            }
            case WEST -> {
                return WEST;
            }
            default -> {return null;}
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == BlockHalf.BOTTOM) {
            return VoxelShapes.union(BOTTOM_MAIN, (state.get(NORTH) ? BOTTOM_NORTH : EMPTY), (state.get(EAST) ? BOTTOM_EAST : EMPTY), (state.get(SOUTH) ? BOTTOM_SOUTH : EMPTY), (state.get(WEST) ? BOTTOM_WEST : EMPTY));
        } else {
            return VoxelShapes.union(TOP_MAIN, (state.get(NORTH) ? TOP_NORTH : EMPTY), (state.get(EAST) ? TOP_EAST : EMPTY), (state.get(SOUTH) ? TOP_SOUTH : EMPTY), (state.get(WEST) ? TOP_WEST : EMPTY));
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(HALF) == BlockHalf.BOTTOM) {
            return VoxelShapes.union(BOTTOM_MAIN, (state.get(NORTH) ? BOTTOM_NORTH : EMPTY), (state.get(EAST) ? BOTTOM_EAST : EMPTY), (state.get(SOUTH) ? BOTTOM_SOUTH : EMPTY), (state.get(WEST) ? BOTTOM_WEST : EMPTY));
        } else {
            return VoxelShapes.union(TOP_MAIN, (state.get(NORTH) ? TOP_NORTH : EMPTY), (state.get(EAST) ? TOP_EAST : EMPTY), (state.get(SOUTH) ? TOP_SOUTH : EMPTY), (state.get(WEST) ? TOP_WEST : EMPTY));
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, HALF, WATERLOGGED);
        super.appendProperties(builder);
    }

    @Override
    protected MapCodec<? extends HorizontalConnectingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

}

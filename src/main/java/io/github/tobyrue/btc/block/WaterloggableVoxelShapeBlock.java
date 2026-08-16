package io.github.tobyrue.btc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.WorldAccess;

import java.util.function.Function;

public class WaterloggableVoxelShapeBlock extends VoxelShapedBlock implements Waterloggable {
    public WaterloggableVoxelShapeBlock(Settings settings, Function<BlockState, VoxelShape> shapeFunction, boolean requiresSupport) {
        super(settings, shapeFunction, requiresSupport);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(Properties.WATERLOGGED, false));
    }
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess worldAccess = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = worldAccess.getFluidState(blockPos);

        boolean isWater = fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER;
        return this.getDefaultState()
                .with(Properties.WATERLOGGED, isWater);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.WATERLOGGED);
    }
    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        FluidState currentFluidState = world.getFluidState(pos);
        FluidState currentFluidStateUp = world.getFluidState(pos.up());
        boolean isWaterlogged = state.get(Properties.WATERLOGGED);

        if (!isWaterlogged && ((currentFluidState.getFluid() == Fluids.WATER || currentFluidState.getFluid() == Fluids.FLOWING_WATER) || (currentFluidStateUp.getFluid() == Fluids.WATER || currentFluidStateUp.getFluid() == Fluids.FLOWING_WATER))) {
            state = state.with(Properties.WATERLOGGED, true);
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        } else if (isWaterlogged) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}

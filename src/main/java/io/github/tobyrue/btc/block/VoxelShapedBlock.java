package io.github.tobyrue.btc.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.function.Function;

public class VoxelShapedBlock extends Block {
    private final Function<BlockState, VoxelShape> shapeFunction;
    private final boolean requiresSupport;

    public VoxelShapedBlock(Settings settings, Function<BlockState, VoxelShape> shapeFunction, boolean requiresSupport) {
        super(settings);
        this.shapeFunction = shapeFunction;
        this.requiresSupport = requiresSupport;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return shapeFunction.apply(state).offset(vec3d.x, vec3d.y, vec3d.z);
    }
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Vec3d vec3d = state.getModelOffset(world, pos);
        return shapeFunction.apply(state).offset(vec3d.x, vec3d.y, vec3d.z);
    }

    public boolean canPlace(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState floorState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, floorState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    @Override
    protected ImmutableMap<BlockState, VoxelShape> getShapesForStates(Function<BlockState, VoxelShape> stateToShape) {
        return super.getShapesForStates(stateToShape);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {

        if (this.requiresSupport) {
            if (direction == Direction.DOWN && !this.canPlace(state, world, pos)) {
                return Blocks.AIR.getDefaultState();
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}

package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class DungeonPressurePlateBlock extends PressurePlateBlock {
    private static final VoxelShape SHAPE;

    private static final VoxelShape DOWN_SHAPE;

    public DungeonPressurePlateBlock(BlockSetType type, Settings settings) {
        super(type, settings);
    }

    static {
        SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        DOWN_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        if (!state.get(POWERED)) {
            return SHAPE;
        } else {
            return DOWN_SHAPE;
        }
    }
}

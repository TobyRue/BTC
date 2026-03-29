package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class DungeonPressurePlateBlock extends PressurePlateBlock implements IDungeonWire {
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

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return state.getBlock() instanceof DungeonPressurePlateBlock && face == Direction.DOWN && state.get(POWERED);
    }
}

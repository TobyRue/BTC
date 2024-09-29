package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class KeyDispenserBlock extends Block implements ModBlockEntityProvider<KeyDispenserBlockEntity>{
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape TOP_MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;

    private static final VoxelShape SHAPE;

    public KeyDispenserBlock(Settings settings) {
        super(settings);
    }
    static {
        BOTTOM_SHAPE = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0);
        TOP_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 5.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 5.0, 4.0, 12.0, 9.0, 12.0);
        BOTTOM_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 9.0, 3.0, 13.0, 12.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 12.0, 2.0, 14.0, 14.0, 14.0);

        SHAPE = VoxelShapes.union(BOTTOM_SHAPE, TOP_MIDDLE_SHAPE, MIDDLE_SHAPE, BOTTOM_MIDDLE_SHAPE, TOP_SHAPE);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }
    @Override
    public BlockEntityType<KeyDispenserBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KEY_DISPENSER_ENTITY;
    }
}

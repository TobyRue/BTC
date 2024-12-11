package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.PedestalBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends Block implements BlockEntityProvider{
    private static final VoxelShape BOTTOM_SHAPE1;
    private static final VoxelShape BOTTOM_SHAPE2;
    private static final VoxelShape BOTTOM_SHAPE3;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape TOP_SHAPE1;
    private static final VoxelShape TOP_SHAPE2;
    private static final VoxelShape TOP_SHAPE3;
    private static final VoxelShape TOP_SHAPE4;

    private static final VoxelShape SHAPE;

    public PedestalBlock(Settings settings) {
        super(settings);
    }
    static {
        BOTTOM_SHAPE1 = Block.createCuboidShape(1, 0.0, 1.0, 15.0, 1.0, 15.0);
        BOTTOM_SHAPE2 = Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 2.0, 14.0);
        BOTTOM_SHAPE3 = Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 3.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 3.0, 4.0, 12.0, 12.0, 12.0);
        TOP_SHAPE1 = Block.createCuboidShape(3.0, 12.0, 3.0, 13.0, 13.0, 13.0);
        TOP_SHAPE2 = Block.createCuboidShape(2.0, 13.0, 2.0, 14.0, 14.0, 14.0);
        TOP_SHAPE3 = Block.createCuboidShape(1, 14.0, 1.0, 15.0, 15.0, 15.0);
        TOP_SHAPE4 = Block.createCuboidShape(2.0, 15.0, 2.0, 14.0, 16.0, 14.0);
        SHAPE = VoxelShapes.union(BOTTOM_SHAPE1, BOTTOM_SHAPE2, BOTTOM_SHAPE3, MIDDLE_SHAPE, MIDDLE_SHAPE, TOP_SHAPE1, TOP_SHAPE2, TOP_SHAPE3, TOP_SHAPE4);
    }
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return world.getBlockEntity(pos, ModBlockEntities.PEDESTAL_BLOCK_ENTITY).get().onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
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
}
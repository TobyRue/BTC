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

    private static final VoxelShape SHAPE;

    public PedestalBlock(Settings settings) {
        super(settings);
    }
    static {
        SHAPE = VoxelShapes.union(
                VoxelShapes.cuboid(0.0625, 0, 0.0625, 0.9375, 0.0625, 0.9375),
                VoxelShapes.cuboid(0.125, 0.0625, 0.125, 0.875, 0.1875, 0.875),
                VoxelShapes.cuboid(0.1875, 0.1875, 0.1875, 0.8125, 0.375, 0.8125),
                VoxelShapes.cuboid(0.1875, 0.75, 0.1875, 0.8125, 0.8125, 0.8125),
                VoxelShapes.cuboid(0.125, 0.8125, 0.125, 0.875, 1, 0.875),
                VoxelShapes.cuboid(0.4375, 0.5, 0.1875, 0.5625, 0.625, 0.25),
                VoxelShapes.cuboid(0.4375, 0.5, 0.75, 0.5625, 0.625, 0.8125),
                VoxelShapes.cuboid(0.75, 0.5, 0.4375, 0.8125, 0.625, 0.5625),
                VoxelShapes.cuboid(0.1875, 0.5, 0.4375, 0.25, 0.625, 0.5625),
                VoxelShapes.cuboid(0.25, 0.375, 0.25, 0.75, 0.75, 0.75)
        );
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
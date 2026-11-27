package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.block.entities.ScrollTableBlockEntity;
import net.minecraft.block.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ScrollTableBlock extends Block implements ModBlockEntityProvider<ScrollTableBlockEntity> {
    public static final MapCodec<ScrollTableBlock> CODEC = createCodec(ScrollTableBlock::new);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.125, 0.875),
            VoxelShapes.cuboid(0.1875, 0.125, 0.1875, 0.8125, 0.75, 0.8125),
            VoxelShapes.cuboid(0.125, 0.75, 0.125, 0.875, 0.875, 0.875)
    );

    public ScrollTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ScrollTableBlockEntity scrollTable) {
                player.openHandledScreen(scrollTable);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ScrollTableBlockEntity scrollTable) {
                ItemScatterer.spawn(world, pos, scrollTable);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntityType<ScrollTableBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SCROLL_TABLE_BLOCK_ENTITY;
    }
}
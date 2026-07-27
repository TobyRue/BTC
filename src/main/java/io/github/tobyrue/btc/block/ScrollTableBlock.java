package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.ItemPedestalBlockEntity;
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
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ScrollTableBlock extends Block implements ModBlockEntityProvider<ScrollTableBlockEntity> {
    public static final MapCodec<ScrollTableBlock> CODEC = createCodec(ScrollTableBlock::new);

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
    public BlockEntityType<ScrollTableBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SCROLL_TABLE_BLOCK_ENTITY;
    }
}
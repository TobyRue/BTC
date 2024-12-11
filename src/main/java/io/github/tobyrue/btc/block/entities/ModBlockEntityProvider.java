package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface ModBlockEntityProvider<T extends BlockEntity> extends BlockEntityProvider {
    BlockEntityType<T> getBlockEntityType();
    @Nullable
    @Override
    default T createBlockEntity(BlockPos pos, BlockState state) {
        return getBlockEntityType().instantiate(pos, state);
    }

}

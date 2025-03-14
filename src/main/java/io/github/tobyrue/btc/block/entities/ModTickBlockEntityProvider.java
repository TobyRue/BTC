package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface ModTickBlockEntityProvider<J extends BlockEntity & BlockEntityTicker<J>> extends ModBlockEntityProvider<J> {
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, getBlockEntityType(), (world1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof BlockEntityTicker<?> ticker) {
                ((BlockEntityTicker<T>) ticker).tick(world1, pos, state1, (T) blockEntity);
            } else {
                System.err.println("Block entity at " + pos + " is not a valid ticker!");
            }
        });
    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }
}

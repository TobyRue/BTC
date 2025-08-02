package io.github.tobyrue.btc.mixin;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockWithEntity.class)
public interface BlockWithEntityInvoker  {
    @Invoker("validateTicker")
    static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> invokeValidateTicker(
            BlockEntityType<A> givenType,
            BlockEntityType<E> expectedType,
            BlockEntityTicker<? super E> ticker
    ) {
        throw new AssertionError(); // Will be overwritten by Mixin at runtime
    }
}

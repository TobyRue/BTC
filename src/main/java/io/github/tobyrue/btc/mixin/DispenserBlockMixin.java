package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityProvider.class)
public interface DispenserBlockMixin {
    @ModifyReturnValue(method = "getTicker", at = @At("RETURN"))
    private BlockEntityTicker<DispenserBlockEntity> getTicker(@Nullable BlockEntityTicker<DispenserBlockEntity> original, World world, BlockState state, BlockEntityType<DispenserBlockEntity> type) {
        if (type == BlockEntityType.DISPENSER && this instanceof DispenserBlock) {
            return BlockWithEntityInvoker.invokeValidateTicker(
                    type,
                    BlockEntityType.DISPENSER,
                    (innerWorld, pos, innerState, blockEntity) -> {
                        if (original != null) {
                            original.tick(innerWorld, pos, innerState, blockEntity);
                        }
                        btc$tick(innerWorld, pos, innerState, blockEntity);
                        // ticking logic here :)
                    }
            );
        }

        return original;
    }

    @Unique
    private void btc$tick(World world, BlockPos pos, BlockState state, DispenserBlockEntity blockEntity) {
        blockEntity.
        System.out.println("Hello World");
    }
}
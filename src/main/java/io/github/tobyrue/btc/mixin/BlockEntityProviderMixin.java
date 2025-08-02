package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.tobyrue.btc.spell.SpellItem;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntityProvider.class)
public interface BlockEntityProviderMixin {
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
                    }
            );
        }

        return original;
    }


    @Unique
    private void btc$tick(World world, BlockPos pos, BlockState state, DispenserBlockEntity dispenserBlockEntity) {
        for (var i = 0; i < dispenserBlockEntity.size(); i++) {
            var stack = dispenserBlockEntity.getStack(i);
            if (stack.getItem() instanceof SpellItem spellItem) {
                spellItem.tickCooldowns(stack);
            }
        }
    }
}
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
    private <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nullable BlockEntityTicker<T> original, World world, BlockState state, BlockEntityType<T> type) {
        if (type == BlockEntityType.DISPENSER && this instanceof DispenserBlock) {
            return BlockWithEntityInvoker.invokeValidateTicker(
                    type,
                    BlockEntityType.DISPENSER,
                    (innerWorld, pos, innerState, blockEntity) -> {
                        if (original != null) {
                            original.tick(innerWorld, pos, innerState, (T) blockEntity);
                        }
                        btc$tickSpells(innerWorld, pos, innerState, blockEntity);
                    }
            );
        }
        return original;
    }

    @Unique
    private <T extends BlockEntity> void btc$tickSpells(World world, BlockPos pos, BlockState state, T blockEntity) {
        if (blockEntity instanceof LootableContainerBlockEntity containerBlock) {
            for (var i = 0; i < containerBlock.size(); i++) {
                var stack = containerBlock.getStack(i);
                if (stack.getItem() instanceof SpellItem spellItem) {
                    spellItem.tickCooldowns(stack);
                }
            }
        }
    }
}
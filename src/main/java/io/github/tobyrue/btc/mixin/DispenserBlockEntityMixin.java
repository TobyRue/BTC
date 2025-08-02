package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellItem;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DispenserBlockEntity.class)
public abstract class DispenserBlockEntityMixin implements LootableInventory {
    @Shadow public abstract int size();

    @Shadow protected abstract DefaultedList<ItemStack> getHeldStacks();

    @WrapOperation(
            method = "chooseNonEmptySlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"
            )
    )
    private boolean overrideIsEmpty(ItemStack stack, Operation<Boolean> original) {
        boolean originalResult = original.call(stack);

        if (originalResult) return true;

        if (stack.getItem() instanceof SpellItem s &&
                s.getSpellDataStore(stack) instanceof SpellDataStore data &&
                data.getCooldown(data.getSpell().getCooldown(data.getArgs(), null)) > 0) {
            return true;
        }

        return false;
    }
}

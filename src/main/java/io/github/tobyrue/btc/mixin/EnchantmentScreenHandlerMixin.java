package io.github.tobyrue.btc.mixin;


import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.slot.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {
    public int getLevel(ItemStack item, RegistryKey<Enchantment> key) {
        return Optional.ofNullable(item.get(DataComponentTypes.ENCHANTMENTS))
                .flatMap(component -> component.getEnchantments().stream()
                        .filter(holder -> holder.matchesKey(key))
                        .findFirst()
                        .map(component::getLevel))
                .orElse(-1);
    }
    @Inject(method = "onContentChanged", at = @At("RETURN"))
    private void convertInfusedPaper(Inventory inventory, CallbackInfo ci) {
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler)(Object)this;
        Slot slot = handler.getSlot(0);
        ItemStack stack = slot.getStack();
        if (stack.isOf(Items.PAPER) && (getLevel(stack, ModEnchantments.INFUSION) > 0)) {
            slot.setStack(new ItemStack(ModItems.ENCHANTED_PAPER));
        }
    }
}
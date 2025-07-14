package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModEnchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Item.class)
public class PaperMixin {
    public int getLevel(ItemStack item, RegistryKey<Enchantment> key) {
        return Optional.ofNullable(item.get(DataComponentTypes.ENCHANTMENTS))
                .flatMap(component -> component.getEnchantments().stream()
                        .filter(holder -> holder.matchesKey(key))
                        .findFirst()
                        .map(component::getLevel))
                .orElse(-1);
    }
    @ModifyReturnValue(method = "isEnchantable", at = @At("RETURN"))
    private boolean modifyIsEnchantable(boolean original) {
        if ((Object) this == Items.PAPER) {
            return true;
        }
        return original;
    }
    @ModifyReturnValue(method = "getEnchantability", at = @At("RETURN"))
    private int modifyGetEnchantability(int original) {
        if ((Object) this == Items.PAPER) {
            return 1;
        }
        return original;
    }

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (world.isClient()) return;

        if (stack.isOf(Items.PAPER)) {
            var paperAmount = stack.getCount();
            if (getLevel(stack, ModEnchantments.INFUSION) > 0) {
                ItemStack enchantedPaper = new ItemStack(ModItems.ENCHANTED_PAPER, paperAmount);
                if (entity instanceof PlayerEntity player) {
                    player.getInventory().setStack(slot, enchantedPaper);
                }
            }
        }
    }

}

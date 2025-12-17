package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.item.ScopedCrossbow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowMixin {
    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void getPullTime(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        float f = EnchantmentHelper.getCrossbowChargeTime(stack, user, 1.25f);
        if (stack.getItem() instanceof ScopedCrossbow) {
            cir.setReturnValue(MathHelper.floor(f * 30.0f));
        }
    }
}

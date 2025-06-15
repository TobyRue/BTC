package io.github.tobyrue.btc.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PaperMixin {
    @Inject(method = "isEnchantable", at = @At("HEAD"), cancellable = true)
    private void makePaperEnchantable(CallbackInfoReturnable<Boolean> cir) {

        if ((Object)this == Items.PAPER) {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "getEnchantability", at = @At("HEAD"), cancellable = true)
    private void paperEnchantable(CallbackInfoReturnable<Integer> cir) {
        if ((Object)this == Items.PAPER) {
            cir.setReturnValue(1);
        }
    }
}

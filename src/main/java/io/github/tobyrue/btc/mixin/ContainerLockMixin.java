package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.BTC;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ContainerLock.class)
public class ContainerLockMixin {

    @Shadow @Final private String key;

    @Inject(method = "canOpen", at = @At("HEAD"), cancellable = true)
    public void canOpen(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.key.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }

        Text stackKeyComponent = stack.get(BTC.KEY_UUID);
        if (stackKeyComponent != null) {
            String stackKeyStr = stackKeyComponent.getString();

            if (stackKeyStr.equals(this.key)) {
                cir.setReturnValue(true);
            } else {
                cir.setReturnValue(false);
            }
        }
    }
}

package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.client.SpyGlassCameraController;
import net.minecraft.client.Keyboard;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void onTelescopeScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (SpyGlassCameraController.isActive() && SpyGlassCameraController.handleMouseScroll(vertical)) {
            ci.cancel();
        }
    }
}
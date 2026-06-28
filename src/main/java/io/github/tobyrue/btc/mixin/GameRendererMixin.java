package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.client.SpyGlassCameraController;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void injectSpyglassBlockFov(CallbackInfoReturnable<Double> cir) {
        if (SpyGlassCameraController.isActive()) {
            cir.setReturnValue(SpyGlassCameraController.modifyFieldOfView(cir.getReturnValue()));
        }
    }
}
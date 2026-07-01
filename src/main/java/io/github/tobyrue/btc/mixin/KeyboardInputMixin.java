package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.client.SpyGlassCameraController;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void cancelMovementInputs(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (SpyGlassCameraController.isActive()) {
            KeyboardInput input = (KeyboardInput) (Object) this;

            input.movementForward = 0.0F;
            input.movementSideways = 0.0F;

            input.pressingForward = false;
            input.pressingBack = false;
            input.pressingLeft = false;
            input.pressingRight = false;

            input.jumping = false;
            input.sneaking = false;
        }
    }
}
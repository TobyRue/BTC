package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.client.SpyGlassCameraController;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {
    @Shadow private Vec3d pos;

    @Inject(method = "update", at = @At("TAIL"))
    private void overrideCameraCoordinates(CallbackInfo ci) {
        if (SpyGlassCameraController.isActive()) {
            this.pos = SpyGlassCameraController.getCameraPositionOverride(this.pos);
        }
    }
}
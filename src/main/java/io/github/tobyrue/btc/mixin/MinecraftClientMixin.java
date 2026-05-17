package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.client.ClientLensHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void injectLensGlow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (ClientLensHandler.shouldMobGlow(entity)) {
            cir.setReturnValue(true);
        }
    }
}
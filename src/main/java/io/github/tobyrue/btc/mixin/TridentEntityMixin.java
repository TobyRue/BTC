package io.github.tobyrue.btc.mixin;

import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {
    @Inject(method = "age", at = @At("HEAD"))
    public void age(CallbackInfo ci) {
        //TODO cancel in some way to stop despawn
    }
}

package io.github.tobyrue.btc.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Inject(method = "age", at = @At("HEAD"), cancellable = true)
    public void age(CallbackInfo ci) {
        var me = (TridentEntity) (Object) this;
        if (me.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
            ci.cancel();
        }
    }
}

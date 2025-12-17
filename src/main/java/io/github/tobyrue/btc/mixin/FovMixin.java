package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class FovMixin {
    @Inject(method = "getFovMultiplier", at = @At("HEAD"), cancellable = true)
    public void getFovMultiplier(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) ((Object) this);
        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && player.isUsingSpyglass() && player.isSneaking() && player.getMainHandStack().isOf(ModItems.SCOPED_CROSSBOW)) {
            cir.setReturnValue(0.1f);
        }
    }
}

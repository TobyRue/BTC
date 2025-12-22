package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerSpyglassMixin {


    @Unique
    public boolean isUsingScopedCrossbow(PlayerEntity player) {
        return player.isSneaking() && player.getMainHandStack().isOf(ModItems.SCOPED_CROSSBOW);
    }

    @Inject(method = "isUsingSpyglass", at = @At("HEAD"), cancellable = true)
    private void modifySpyglassCondition(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) ((Object) this);
        if (isUsingScopedCrossbow(player)) {
            cir.setReturnValue(true);
        }
    }
}

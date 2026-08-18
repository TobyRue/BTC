package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityShieldMixin {

    @ModifyVariable(
            method = "applyDamage",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private float btc$reduceDamageIfBlocking(float amount) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isUsingItem() && entity.getActiveItem().isOf(ModItems.HEROIC_SWORD)) {
            return amount * 0.2F;
        }

        return amount;
    }
}
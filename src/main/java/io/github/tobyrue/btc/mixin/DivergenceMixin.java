package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.regestries.ModEnchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(RangedWeaponItem.class)
public class DivergenceMixin  {

    @ModifyVariable(
            method = "shootAll",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1
    )
    private float btc$applyPrecisionToDivergence(
            float divergence,
            ServerWorld world,
            LivingEntity shooter,
            Hand hand,
            ItemStack stack
    ) {
        int level = ModEnchantments.getLevel(
                stack,
                ModEnchantments.PRECISION
        );

        if (level <= 0) return divergence;

        float multiplier = 1.0f - (level * 0.1f);
        return divergence * Math.max(multiplier, 0.5f);
    }
}

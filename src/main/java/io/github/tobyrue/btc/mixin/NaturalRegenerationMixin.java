package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerEntity.class)
public abstract class NaturalRegenerationMixin {
    @ModifyReturnValue(method = "canFoodHeal", at = @At("RETURN"))
    private boolean modifyCanFoodHeal(boolean original) {
        return original && !((PlayerEntity) ((Object) this)).hasStatusEffect(ModStatusEffects.NO_NATURAL_REGENERATION);
    }
}

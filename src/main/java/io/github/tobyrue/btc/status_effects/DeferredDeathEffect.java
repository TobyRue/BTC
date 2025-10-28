package io.github.tobyrue.btc.status_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;

public class DeferredDeathEffect extends StatusEffect {
    public DeferredDeathEffect() {
        super(StatusEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.kill();
        return super.applyUpdateEffect(entity, amplifier);
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (duration <= 1) {
            return true;
        }
        return false;
    }
}

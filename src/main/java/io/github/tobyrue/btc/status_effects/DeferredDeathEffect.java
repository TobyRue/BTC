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

    //Try later
//    @Override
//    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
//        // Get the instance on the entity so we can read remaining duration
//        StatusEffectInstance inst = entity.getStatusEffect(this);
//        if (inst == null) return;
//
//        // When duration is 1 or less, this is the last tick before removal.
//        // Kill on server only.
//        if (!entity.getWorld().isClient && inst.getDuration() <= 1) {
//            // You can use kill(), remove, or setHealth(0). kill() is fine.
//            entity.kill();
//        }
//    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if (duration <= 1) {
            return true;
        }
        return false;
    }
}

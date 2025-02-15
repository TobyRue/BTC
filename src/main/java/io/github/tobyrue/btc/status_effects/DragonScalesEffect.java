package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.BTC;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;

import java.util.Objects;

public class DragonScalesEffect extends StatusEffect {
    public DragonScalesEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.BENEFICIAL, 0x9E4576);
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        super.onEntityDamage(entity, amplifier, source, amount);

        // Check if the attacked entity has this status effect
        if (entity.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES))) {
            // Get the level of the effect (higher level = more damage reflection & protection)
            int level = Objects.requireNonNull(entity.getStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES))).getAmplifier() + 1;
            // Reflect damage to the attacker
            if (source.getAttacker() instanceof LivingEntity attacker) {
                float reflectionDamage = amount * (0.25f * level); // Reflect 25% of damage per level
                attacker.damage(source, reflectionDamage);
            }

            // Apply damage reduction to the entity with the effect
            float reducedDamage = amount * (0.2f * level) + entity.getHealth();
            // Ensure that damage doesn't go negative
            if(reducedDamage < 0) {
                reducedDamage = 0;
            }

            if(!(entity.getHealth() <= 0)){
                entity.setHealth(reducedDamage);
            }
        }
    }


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // You can control the frequency of the effect. Returning true means it updates regularly.
        return true;
    }
}

package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;

import java.util.Objects;
import java.util.WeakHashMap;

public class DragonScalesEffect extends StatusEffect {
    private static final WeakHashMap<LivingEntity, Float> STORED_AFTER = new WeakHashMap<>();


    public DragonScalesEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0x9E4576);
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        super.onEntityDamage(entity, amplifier, source, amount);
        // Check if the attacked entity has this status effect
        if (entity.hasStatusEffect(ModStatusEffects.DRAGON_SCALES)) {
            if (!STORED_AFTER.containsKey(entity)) {
                STORED_AFTER.put(entity, entity.getHealth());
            }
            var lastHealth = STORED_AFTER.get(entity);
            var currentHealth = entity.getHealth();
            var dmg = lastHealth - currentHealth;
            // Get the level of the effect (higher level = more damage reflection & protection)
            int level = amplifier + 1;
            // Reflect damage to the attacker
            if (source.getAttacker() instanceof LivingEntity attacker) {
                float reflectionDamage = dmg * (0.2f * level); // Reflect 20% of damage per level
                attacker.damage(source, reflectionDamage);
            }

            // Apply damage reduction to the entity with the effect
            float reducedDamage = dmg * (0.2f * level) + currentHealth;
            // Ensure that damage doesn't go negative
            if(reducedDamage < 0) {
                reducedDamage = 0;
            }

            if(!(entity.getHealth() <= 0)){
                entity.setHealth(reducedDamage);
                STORED_AFTER.replace(entity, lastHealth, currentHealth);
            }
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        for (LivingEntity entity : STORED_AFTER.keySet()) {
            STORED_AFTER.remove(entity);
        }
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (!STORED_AFTER.containsKey(entity)) {
            STORED_AFTER.put(entity, entity.getHealth());
        }
        super.onApplied(entity, amplifier);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }
}

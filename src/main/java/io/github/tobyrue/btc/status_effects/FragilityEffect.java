

package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.world.ServerWorld;

import java.util.WeakHashMap;

public class FragilityEffect extends StatusEffect {
    // Store damage accumulated during effect
    private static final WeakHashMap<LivingEntity, Float> LAST_HEALTH = new WeakHashMap<>();
    public FragilityEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9566698F); // dark crimson red
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (!LAST_HEALTH.containsKey(entity)){
            LAST_HEALTH.put(entity, entity.getHealth() + entity.getAbsorptionAmount());
        }
        super.onApplied(entity, amplifier);
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        if (entity.hasStatusEffect(ModStatusEffects.FRAGILITY)) {
            if (!LAST_HEALTH.containsKey(entity)){
                LAST_HEALTH.put(entity, entity.getHealth() + entity.getAbsorptionAmount());
            }
            System.out.println("Damaged");
            var a = LAST_HEALTH.get(entity) - entity.getHealth();
            System.out.println("Amount A: " + a);
            System.out.println("Last Health: " + LAST_HEALTH.get(entity));

            float dm = (float) (Math.round((amplifier * 0.1) * 2) / 2.0);

            System.out.println("Damage Multiplier: " + dm);
            System.out.println("Added Damage: " + a*dm);

            var d = entity.damage(source, a*dm);

            System.out.println("Damaged: " + d);

            LAST_HEALTH.put(entity, entity.getHealth() + entity.getAbsorptionAmount());
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributes) {
        super.onRemoved(attributes);
        // When effect ends, unleash stored damage
        for (LivingEntity entity : LAST_HEALTH.keySet()) {
            if (entity.hasStatusEffect(ModStatusEffects.FRAGILITY)) continue;
            LAST_HEALTH.remove(entity);
        }
    }
}
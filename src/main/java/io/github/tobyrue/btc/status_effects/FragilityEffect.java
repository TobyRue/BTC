

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
    private static final WeakHashMap<LivingEntity, Float> LAST_HEALTH = new WeakHashMap<>();
    private static final WeakHashMap<LivingEntity, Boolean> IS_PROCESSING = new WeakHashMap<>();

    public FragilityEffect() {
        super(StatusEffectCategory.HARMFUL, 0x9566698F);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        LAST_HEALTH.put(entity, entity.getHealth() + entity.getAbsorptionAmount());
        super.onApplied(entity, amplifier);
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        // Must be server side
        if (entity.getWorld().isClient()) return;

        // Prevent recursive damage callback
        if (IS_PROCESSING.getOrDefault(entity, false)) return;

        // Must have this effect
        if (!entity.hasStatusEffect(ModStatusEffects.FRAGILITY)) return;

        float previous = LAST_HEALTH.getOrDefault(entity, entity.getHealth() + entity.getAbsorptionAmount());
        float current = entity.getHealth() + entity.getAbsorptionAmount();

        float damageTaken = previous - current;

        // Update now for next tick
        LAST_HEALTH.put(entity, current);

        if (damageTaken <= 0) return;

        // Extra damage = 10% per amplifier level
        float extra = damageTaken * (0.10F * (amplifier + 1));

        // Damage again without recursion
        IS_PROCESSING.put(entity, true);
        entity.damage(source, extra);
        IS_PROCESSING.put(entity, false);
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        for (LivingEntity entity : LAST_HEALTH.keySet()) {
            if (entity.hasStatusEffect(ModStatusEffects.FRAGILITY)) continue;
            LAST_HEALTH.remove(entity);
        }
        for (LivingEntity entity : IS_PROCESSING.keySet()) {
            if (entity.hasStatusEffect(ModStatusEffects.FRAGILITY)) continue;
            IS_PROCESSING.remove(entity);
        }
    }
}

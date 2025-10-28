package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import java.util.WeakHashMap;

public class CursedReckoningEffect extends StatusEffect {
    // Store damage accumulated during effect
    private static final WeakHashMap<LivingEntity, Float> STORED_DAMAGE = new WeakHashMap<>();

    public CursedReckoningEffect() {
        super(StatusEffectCategory.HARMFUL, 0x5E0A0A); // dark crimson red
    }

    @Override
    public void onEntityDamage(LivingEntity entity, int amplifier, DamageSource source, float amount) {
        if (entity.hasStatusEffect(ModStatusEffects.CURSED_RECKONING)) {
            STORED_DAMAGE.put(entity, STORED_DAMAGE.getOrDefault(entity, 0f) + amount);
            // Cancel actual damage
            entity.setHealth(Math.min(entity.getMaxHealth(), entity.getHealth() + amount));
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributes) {
        super.onRemoved(attributes);
        // When effect ends, unleash stored damage
        for (LivingEntity entity : STORED_DAMAGE.keySet()) {
            if (entity.hasStatusEffect(ModStatusEffects.CURSED_RECKONING)) continue;
            float dmg = STORED_DAMAGE.remove(entity);
            if (!entity.getWorld().isClient && dmg > 0)
                entity.damage(entity.getDamageSources().magic(), dmg);
        }
    }
}

package io.github.tobyrue.btc.regestries;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class ModMaps {
    public static final Map<RegistryEntry<StatusEffect>, RegistryEntry<StatusEffect>> EFFECT_OPPOSITES = new HashMap<>();

    static {
        EFFECT_OPPOSITES.put(StatusEffects.WEAKNESS, StatusEffects.STRENGTH);
        EFFECT_OPPOSITES.put(StatusEffects.STRENGTH, StatusEffects.WEAKNESS);

        EFFECT_OPPOSITES.put(StatusEffects.SLOWNESS, StatusEffects.SPEED);
        EFFECT_OPPOSITES.put(StatusEffects.SPEED, StatusEffects.SLOWNESS);

        EFFECT_OPPOSITES.put(StatusEffects.MINING_FATIGUE, StatusEffects.HASTE);
        EFFECT_OPPOSITES.put(StatusEffects.HASTE, StatusEffects.MINING_FATIGUE);

        EFFECT_OPPOSITES.put(StatusEffects.BLINDNESS, StatusEffects.NIGHT_VISION);
        EFFECT_OPPOSITES.put(StatusEffects.NIGHT_VISION, StatusEffects.BLINDNESS);

        EFFECT_OPPOSITES.put(StatusEffects.POISON, StatusEffects.REGENERATION);
        EFFECT_OPPOSITES.put(StatusEffects.REGENERATION, StatusEffects.POISON);

        EFFECT_OPPOSITES.put(StatusEffects.WITHER, StatusEffects.HEALTH_BOOST);
        EFFECT_OPPOSITES.put(StatusEffects.HEALTH_BOOST, StatusEffects.WITHER);

        EFFECT_OPPOSITES.put(StatusEffects.UNLUCK, StatusEffects.LUCK);
        EFFECT_OPPOSITES.put(StatusEffects.LUCK, StatusEffects.UNLUCK);

        EFFECT_OPPOSITES.put(StatusEffects.HUNGER, StatusEffects.SATURATION);
        EFFECT_OPPOSITES.put(StatusEffects.SATURATION, StatusEffects.HUNGER);

        EFFECT_OPPOSITES.put(StatusEffects.DARKNESS, StatusEffects.GLOWING);
        EFFECT_OPPOSITES.put(StatusEffects.GLOWING, StatusEffects.DARKNESS);

        EFFECT_OPPOSITES.put(StatusEffects.NAUSEA, StatusEffects.CONDUIT_POWER);
        EFFECT_OPPOSITES.put(StatusEffects.CONDUIT_POWER, StatusEffects.NAUSEA);

        EFFECT_OPPOSITES.put(StatusEffects.SLOW_FALLING, StatusEffects.LEVITATION);
        EFFECT_OPPOSITES.put(StatusEffects.LEVITATION, StatusEffects.SLOW_FALLING);

    }
}

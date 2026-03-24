package io.github.tobyrue.btc.misc;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public interface StatusEffectHolderBlockEntity {
    void setPotionContents(RegistryEntry<StatusEffect> storedEffect);

    void setDuration(int duration);

    void setAmplifier(int amplifier);

    default void setEffect(RegistryEntry<StatusEffect> storedEffect, int duration, int amplifier) {
        setPotionContents(storedEffect);
        setDuration(duration);
        setAmplifier(amplifier);
    }
}

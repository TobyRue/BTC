package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.status_effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static final RegistryEntry<StatusEffect> BUILDER_BLUNDER;
    public static final RegistryEntry<StatusEffect> MINER_MISHAP;
    public static final RegistryEntry<StatusEffect> DRAGON_SCALES;
    public static final RegistryEntry<StatusEffect> DROWNING;
    public static final RegistryEntry<StatusEffect> NO_NATURAL_REGENERATION;
    public static final RegistryEntry<StatusEffect> FROST_WALKER;
    public static final RegistryEntry<StatusEffect> DEFERRED_DEATH;
    public static final RegistryEntry<StatusEffect> CURSED_RECKONING;
    public static final RegistryEntry<StatusEffect> FRAGILITY;

    static {
        BUILDER_BLUNDER = ModStatusEffects.register("builder_blunder", new BuilderBlunderEffect());
        MINER_MISHAP = ModStatusEffects.register("miner_mishap", new MinerMishapEffect());
        DRAGON_SCALES = ModStatusEffects.register("dragon_scales", new DragonScalesEffect());
        DROWNING = ModStatusEffects.register("drowning", new DrowningEffect());
        NO_NATURAL_REGENERATION = ModStatusEffects.register("no_natural_regeneration", new DummyStatusEffect(StatusEffectCategory.HARMFUL, 0x680000));
        FROST_WALKER = ModStatusEffects.register("frost_walker", new FrostWalkerEffect());
        DEFERRED_DEATH = ModStatusEffects.register("deferred_death", new DeferredDeathEffect());
        CURSED_RECKONING = ModStatusEffects.register("cursed_reckoning", new CursedReckoningEffect());
        FRAGILITY = ModStatusEffects.register("fragility", new FragilityEffect());
    }

    private static RegistryEntry<StatusEffect> register(String id, StatusEffect statusEffect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, BTC.identifierOf(id), statusEffect);
    }
}

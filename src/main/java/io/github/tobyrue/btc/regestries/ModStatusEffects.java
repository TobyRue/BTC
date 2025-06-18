package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.status_effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModStatusEffects {
    public static final StatusEffect BUILDER_BLUNDER;
    public static final StatusEffect MINER_MISHAP;
    public static final StatusEffect DRAGON_SCALES;
    public static final StatusEffect DROWNING;
    public static final StatusEffect NO_NATURAL_REGENERATION;
    public static final StatusEffect FROST_WALKER;

    static {
        BUILDER_BLUNDER = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("builder_blunder"), new BuilderBlunderEffect());
        MINER_MISHAP = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("miner_mishap"), new MinerMishapEffect());
        DRAGON_SCALES = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("dragon_scales"), new DragonScalesEffect());
        DROWNING = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("drowning"), new DrowningEffect());
        NO_NATURAL_REGENERATION = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("no_natural_regeneration"), new DummyStatusEffect(StatusEffectCategory.HARMFUL, 0x680000));
        FROST_WALKER = Registry.register(Registries.STATUS_EFFECT, BTC.identifierOf("frost_walker"), new FrostWalkerEffect());
    }
}

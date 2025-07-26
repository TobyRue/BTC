package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Spell;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModRegistries {
    public static final Identifier SPELL_ID = BTC.identifierOf("spell");

    public static final RegistryKey<Registry<Spell>> SPELL_KEY =
            RegistryKey.ofRegistry(SPELL_ID);

    public static final Registry<Spell> SPELL = FabricRegistryBuilder.createSimple(Spell.class, SPELL_ID)
            .buildAndRegister();
}

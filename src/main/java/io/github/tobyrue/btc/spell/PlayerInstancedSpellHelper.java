package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.BTC;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PlayerInstancedSpellHelper {
    public static final ComponentType<Spell.InstancedSpell> KNOWN_SPELLS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("known_spells"),
            ComponentType.<Spell.InstancedSpell>builder().codec(Spell.InstancedSpell.CODEC).build()
    );
    public static final ComponentType<Spell.InstancedSpell> FAVORITE_SPELLS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("favorite_spells"),
            ComponentType.<Spell.InstancedSpell>builder().codec(Spell.InstancedSpell.CODEC).build()
    );
//    public static boolean has(final ServerPlayerEntity player, final Spell.InstancedSpell spell) {
//
//    }
}

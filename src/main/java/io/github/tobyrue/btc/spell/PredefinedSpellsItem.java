package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class PredefinedSpellsItem extends MinimalPredefinedSpellsItem {
    public PredefinedSpellsItem(Settings settings) {
        super(settings);
    }


    public static List<Spell.InstancedSpell> getKnownSpells(PlayerSpellData data) {
        return data.knownSpells;
    }

    public static List<Spell.InstancedSpell> getFavoriteSpells(PlayerSpellData data) {
        return data.favoriteSpells;
    }

    public static void addKnownSpell(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell, @Nullable Identifier advId) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).knownSpells;
        boolean exists = list.stream().anyMatch(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));

        if (!exists) {
            if (advId != null) {
                if (AdvancementUtils.hasAdvancement(player, advId.getNamespace(), advId.getPath())) {
                    list.add(spell);
                }
            } else {
                list.add(spell);
            }
            state.markDirty();
        }
    }

    public static void addFavoriteSpell(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).favoriteSpells;
        boolean exists = list.stream().anyMatch(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));

        if (!exists) {
            list.add(spell);
            state.markDirty();
        }
    }
    public static void addFavoriteSpellWithIndex(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell, int index) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).favoriteSpells;
        boolean exists = list.stream().anyMatch(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
        if (!exists) {
            list.set(index, spell);
            state.markDirty();
        }
    }
    public static void removeKnownSpell(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).knownSpells;
        list.removeIf(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
        state.markDirty();
    }

    public static void removeFavoriteSpell(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).favoriteSpells;
        list.removeIf(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
        state.markDirty();
    }
    public static void removeAllFavoriteSpells(ServerPlayerEntity player, SpellPersistentState state, Spell.InstancedSpell spell) {
        List<Spell.InstancedSpell> list = state.getPlayerData(player).favoriteSpells;
        list.removeIf(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
        state.markDirty();
    }
}

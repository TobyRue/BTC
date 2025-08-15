package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class PredefinedSpellsItem extends SpellItem {
    public PredefinedSpellsItem(Settings settings) {
        super(settings);
    }

    public abstract List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity);


    public static void addSpell(ServerPlayerEntity player, List<Spell.InstancedSpell> spellList, @Nullable Identifier id, Spell.InstancedSpell spell) {
        boolean exists = spellList.stream()
                .anyMatch(s -> s.spell() == spell.spell() && s.args() == spell.args());
        if (id != null) {
            if (AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath())) {
                if (!exists) {
                    spellList.add(spell);
                }
            }
        } else {
            if (!exists) {
                spellList.add(spell);
            }
        }
    }
    public static void removeSpell(List<Spell.InstancedSpell> spellList, Spell.InstancedSpell spell) {
        spellList.removeIf(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
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
            list.add(index, spell);
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
}

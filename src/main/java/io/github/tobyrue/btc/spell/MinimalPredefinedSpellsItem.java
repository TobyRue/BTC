package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public abstract class MinimalPredefinedSpellsItem extends SpellItem {
    public MinimalPredefinedSpellsItem(Settings settings) {
        super(settings);
    }

    public abstract List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity);

    public static void addSpellToItem(ServerPlayerEntity player, List<Spell.InstancedSpell> spellList, @Nullable Identifier id, Spell.InstancedSpell spell) {
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

    public static void removeSpellFromItem(List<Spell.InstancedSpell> spellList, Spell.InstancedSpell spell) {
        spellList.removeIf(s -> s.spell().equals(spell.spell()) && s.args().equals(spell.args()));
    }
}

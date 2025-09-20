package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.client.screen.HexagonRadialMenu;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class MinimalPredefinedSpellsItem extends SpellItem {
    public MinimalPredefinedSpellsItem(Settings settings) {
        super(settings);
    }

    public abstract List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity);

    public static void addSpellToItem(PlayerEntity player, List<Spell.InstancedSpell> spellList, @Nullable Identifier id, Spell.InstancedSpell spell) {
        boolean exists = spellList.stream()
                .anyMatch(s -> s.spell() == spell.spell() && s.args() == spell.args());
        if (id != null) {
            if (player instanceof ServerPlayerEntity serverPlayer && AdvancementUtils.hasAdvancement(serverPlayer, id.getNamespace(), id.getPath())) {
                if (!exists) {
                    //TODO make a packet that detects if the advancement is activated and if so it runs THIS METHOD again but with NULL IDENTIFIER, the packet will need a identifier and another identifier (of the spell) using ModRegistries.SPELL.get() or something like that to remember the spell
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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() != null) {
            //TODO
            tooltip.add(Text.literal(ModRegistries.SPELL.getId(data.getSpell()).toString()));
        }
    }
}

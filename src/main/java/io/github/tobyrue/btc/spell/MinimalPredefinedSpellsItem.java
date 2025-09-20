package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public abstract class MinimalPredefinedSpellsItem extends SpellItem {
    private boolean resetSpellAdvancements = false;

    public MinimalPredefinedSpellsItem(Settings settings) {
        super(settings);
    }

    public abstract List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity);

    public static void addSpellToItem(PlayerEntity player, List<Spell.InstancedSpell> spellList, @Nullable Identifier id, Spell.InstancedSpell spell) {
        boolean exists = spellList.stream()
                .anyMatch(s -> s.spell() == spell.spell() && s.args() == spell.args());
        if (id != null) {
            if (AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath())) {
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

    public abstract List<Identifier> getSpellAdvancements(ItemStack stack, World world, LivingEntity entity);

    public static void refreshAllMinimalSpellsItems(PlayerEntity player, World world) {
        for (ItemStack stack : player.getInventory().main) {
            if (stack.getItem() instanceof MinimalPredefinedSpellsItem minimal) {
                List<Identifier> advancements = minimal.getSpellAdvancements(stack, world, player);

                for (Identifier id : advancements) {
                    System.out.println("Item " + stack.getItem() + " requires advancement: " + id);

                    AdvancementUtils.requestAdvancementCheck(id.getNamespace(), id.getPath());

                    boolean has = AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath());
                    if (has) {
                        System.out.println("Player has advancement " + id + " -> unlock spells");
                    }
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!resetSpellAdvancements && entity instanceof PlayerEntity player && world.isChunkLoaded(player.getChunkPos().getCenterX(), player.getChunkPos().getCenterZ())) {
            resetSpellAdvancements = true;
            refreshAllMinimalSpellsItems(player, world);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() != null) {
            tooltip.add(Text.translatable(data.getSpell().getName(data.getArgs()).toString().replaceAll(".*'([^']+)'.*", "$1")));
        }
    }

}

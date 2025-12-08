package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
            if (player != null && player.getWorld() != null) {
                if (AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath())) {
                    if (!exists) {
                        spellList.add(spell);
                    }
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
                    if (world != null) {
                        boolean has = AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath());
                        if (has) {
                            System.out.println("Player has advancement " + id + " -> unlock spells");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!resetSpellAdvancements && entity instanceof PlayerEntity player && world != null) {
            resetSpellAdvancements = true;
            refreshAllMinimalSpellsItems(player, world);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        final var data = this.getSpellDataStore(stack);
        if (data.getSpell() != null) {
            tooltip.add(Text.translatable(data.getSpell().getName(data.getArgs()).toString().replaceAll(".*'([^']+)'.*", "$1")).formatted(this.getSpellTextColor()));
            tooltip.add(data.getSpell().getDescription(data.getArgs()));
            if (data.getSpell() instanceof ChanneledSpell channeledSpell) {
                var d = channeledSpell.disturb.distributionLevel();
                if (d == ChanneledSpell.DistributionLevels.CLICK || d == ChanneledSpell.DistributionLevels.DAMAGE_AND_CLICK || d == ChanneledSpell.DistributionLevels.CROUCH_AND_CLICK || d == ChanneledSpell.DistributionLevels.MOVE_AND_CLICK || d == ChanneledSpell.DistributionLevels.DAMAGE_CROUCH_AND_CLICK || d == ChanneledSpell.DistributionLevels.MOVE_CROUCH_AND_CLICK || d == ChanneledSpell.DistributionLevels.MOVE_DAMAGE_AND_CLICK || d == ChanneledSpell.DistributionLevels.DAMAGE_CROUCH_MOVE_AND_CLICK) {
                    tooltip.add(Text.translatable("item.btc.spell.hold", (channeledSpell.disturb.hold() / 20)));
                }
            }
            if (type.isAdvanced()) {
                tooltip.add(Text.literal("NBT: " + GrabBag.toNBT(data.getArgs()
                )).formatted(Formatting.DARK_GRAY));
            }
        }
    }
    private Formatting getSpellTextColor() {
        return Formatting.WHITE;
    }
}

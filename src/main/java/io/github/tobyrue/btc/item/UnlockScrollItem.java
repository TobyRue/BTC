package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.btc.util.UnlockScrollCache;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class UnlockScrollItem extends Item {
    public UnlockScrollItem() {
        super(new Item.Settings().maxCount(1).rarity(Rarity.RARE).component(
                ModComponents.UNLOCK_SPELL_COMPONENT,
                new UnlockSpellComponent(Optional.empty(), 0, Identifier.of("empty"), "{}")
        ));
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world);
        super.onCraftByPlayer(stack, world, player);
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        UnlockScrollCache.invalidate(stack);
        super.onCraft(stack, world);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient() && user instanceof ServerPlayerEntity player) {
            UnlockSpellComponent component = stack.get(ModComponents.UNLOCK_SPELL_COMPONENT);

            if (component != null && component.id() instanceof Identifier id) {
                NbtCompound argsNbt = component.argsAsNbt();
                Spell registeredSpell = ModRegistries.SPELL.get(id);

                if (registeredSpell != null) {
                    GrabBag currentArgs = GrabBag.fromNBT(argsNbt);
                    Spell.InstancedSpell spellToUnlock = new Spell.InstancedSpell(registeredSpell, currentArgs);

                    MinecraftServer server = player.getServer();
                    if (server != null) {
                        SpellPersistentState spellState = SpellPersistentState.get(server);
                        PlayerSpellData playerData = spellState.getPlayerData(player);

                        boolean alreadyKnown = playerData.knownSpells.stream()
                                .anyMatch(inst -> inst.spell() == spellToUnlock.spell()
                                        && inst.args().equalsOther(currentArgs));

                        if (!alreadyKnown) {
                            component.advancement().ifPresent(av -> {
                                AdvancementEntry advancement = player.server.getAdvancementLoader().get(av);
                                if (advancement != null) {
                                    player.getAdvancementTracker().grantCriterion(advancement, "unlock");
                                }
                            });

                            playerData.knownSpells.add(spellToUnlock);
                            spellState.markDirty();

                            stack.decrementUnlessCreative(1, user);
                        }
                    }
                }
            }
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public Text getName(ItemStack stack) {
        Spell.InstancedSpell inst = UnlockScrollCache.getCachedSpell(stack);
        if (stack.contains(ModComponents.SCROLL_DEFINITION_COMPONENT)) {
            Identifier name = stack.get(ModComponents.SCROLL_DEFINITION_COMPONENT).name();
            int color = stack.get(ModComponents.SCROLL_DEFINITION_COMPONENT).color();
            return Text.translatable("scroll_upgrade." + name.getNamespace().toLowerCase(Locale.ROOT) + "." + name.getPath().toLowerCase(Locale.ROOT));
        }
        if (inst != null && inst.spell() != null) {
            try {
                return Text.translatable(this.getTranslationKey(stack), inst.spell().getName(inst.args()));
            } catch (Exception e) {
                return Text.translatable(this.getTranslationKey() + ".err");
            }
        }
        return Text.translatable(this.getTranslationKey() + ".err");
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        Spell.InstancedSpell inst = UnlockScrollCache.getCachedSpell(stack);
        if (inst != null && inst.args() != null) {
            if (inst.spell() instanceof UpgradableSpell upgradableSpell) {
                if (Screen.hasShiftDown() && upgradableSpell.getUpgradeDescriptions() != null) {
                    for (Pair<Identifier, Text> description : upgradableSpell.getUpgradeDescriptions()) {
                        var name = description.getLeft();
                        tooltip.add(Text.translatable("scroll_upgrade." + name.getNamespace().toLowerCase(Locale.ROOT) + "." + name.getPath().toLowerCase(Locale.ROOT)).append(" - ").append(description.getRight()));
                    }
                } else {
                    tooltip.add(Text.literal("Hold Shift to see Upgrades"));
                }
            }
            var c = inst.args().getInt("cooldown");
            tooltip.add(Text.translatable("item.btc.spell.type." + inst.spell().getSpellType()));
            tooltip.add(Text.translatable("item.btc.spell.cooldown", c / 20));
            tooltip.add(inst.spell().getDescription(inst.args()));
            if (type.isAdvanced()) {
                UnlockSpellComponent comp = stack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
                if (comp != null) {
                    comp.advancement().ifPresent(av ->
                            tooltip.add(Text.literal("Adv: " + av.toTranslationKey()).formatted(Formatting.DARK_GRAY))
                    );
                }
                tooltip.add(Text.literal("NBT: " + GrabBag.toNBT(inst.args())).formatted(Formatting.DARK_GRAY));
            }
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}
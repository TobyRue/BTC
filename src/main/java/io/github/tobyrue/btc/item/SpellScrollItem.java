package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class SpellScrollItem extends Item {
    public final Spell spell;

    public SpellScrollItem(Spell spellType) {
        super(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).maxCount(1));
        this.spell = spellType;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Start using the item
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if ((user instanceof ServerPlayerEntity player)) {
            var progress = player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(BTC.identifierOf(String.format("adventure/get_%s_scroll", spell))));
            if (!progress.isDone()) {
                if (spell != null) {
                    player.getAdvancementTracker().grantCriterion(
                            player.server.getAdvancementLoader().get(BTC.identifierOf(String.format("adventure/get_%s_scroll", spell))),
                            "scroll"
                    );
                    player.sendMessage(Text.translatable("item.btc.scroll.gained", Text.translatable("item.btc.scroll.gained." + Objects.requireNonNull(ModRegistries.SPELL.getId(spell)).getNamespace())), true);
                    stack.decrementUnlessCreative(1, user);
                }
            }
        }
        return super.finishUsing(stack, world, user);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.btc.scroll.type", Text.translatable("item.btc.scroll.type." + spell.getSpellType())).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("item.btc.scroll.attack", Text.translatable("item.btc.scroll.attack." + Objects.requireNonNull(ModRegistries.SPELL.getId(spell)).getNamespace())).formatted(Formatting.BLUE));
        if (spell.getCooldown() instanceof Spell.SpellCooldown c) {
            tooltip.add(Text.translatable("item.btc.scroll.cooldown", (c.ticks() / 20)).formatted(Formatting.BLUE));
        }
        super.appendTooltip(stack, context, tooltip, type);
    }
}

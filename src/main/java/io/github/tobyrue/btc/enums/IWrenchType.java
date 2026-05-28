package io.github.tobyrue.btc.enums;

import io.github.tobyrue.btc.regestries.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.List;
import java.util.Optional;

public interface IWrenchType {
    ActionResult useOnBlock(ItemUsageContext context);

    default void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.btc.wrench.type." + Optional.ofNullable(stack.getOrDefault(ModComponents.WRENCH_TYPE, null)).map(WrenchType::asString).orElse("missingno") + ".tooltip"));
    }

    default Text getName(ItemStack stack) {
        return Text.translatable("item.btc.wrench.type." + Optional.ofNullable(stack.getOrDefault(ModComponents.WRENCH_TYPE, null)).map(WrenchType::asString).orElse("missingno"));
    }
}

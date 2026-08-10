package io.github.tobyrue.btc.tooltip;

import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;

public record UpgradeTreeTooltipData(List<Pair<Identifier, Text>> upgrades) implements TooltipData {}
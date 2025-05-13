package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.CopperWireBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class CreativeWrenchItem extends Item {
    public CreativeWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.use().formatted(Formatting.RED));
    }

    public MutableText use() {return Text.literal("Creative Wrench - Does things to BTC Blocks");}

}

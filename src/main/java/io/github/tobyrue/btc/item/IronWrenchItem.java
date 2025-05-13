package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.CopperWireBlock;
import io.github.tobyrue.btc.block.DungeonDoorBlock;
import io.github.tobyrue.btc.block.DungeonWireBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;

import java.util.List;

public class IronWrenchItem extends Item {
    public IronWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var state = context.getWorld().getBlockState(context.getBlockPos());
        if (!state.streamTags().anyMatch(t -> t == BTC.WRENCH_BLACKLIST) && !(state.getBlock() instanceof PistonBlock && state.get(PistonBlock.EXTENDED)) && (state.getBlock() instanceof CopperWireBlock && state.get(CopperWireBlock.SURVIVAL))) {
            context.getWorld().setBlockState(context.getBlockPos(), state.rotate(context.getPlayer().isSneaking() ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90));
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.getDescription1().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
        tooltip.add(this.getDescription2().formatted(Formatting.BLUE));
        tooltip.add(this.getDescription3().formatted(Formatting.DARK_RED));
        tooltip.add(this.getDescription4().formatted(Formatting.DARK_RED));
        tooltip.add(this.getDescription5().formatted(Formatting.DARK_RED));
    }


    public MutableText getDescription1() {
        return Text.literal("Rotate blocks with 4 directions by clicking normally");
    }

    public MutableText getDescription2() {
        return Text.literal("Hold Shift to rotate in the opposite direction.");
    }

    public MutableText getDescription3() {
        return Text.literal("Disclaimer: Normal Click Might Have Special Actions for Blocks");
    }

    public MutableText getDescription4() {
        return Text.literal("Disclaimer: Can Rotate Some Blocks with 6 Facing Properties");
    }

    public MutableText getDescription5() {
        return Text.literal("But in Only North East South West Directions");
    }
}

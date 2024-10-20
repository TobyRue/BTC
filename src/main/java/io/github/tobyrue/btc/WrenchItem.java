package io.github.tobyrue.btc;

import net.minecraft.block.PistonBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Formatting;

import java.util.List;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var state = context.getWorld().getBlockState(context.getBlockPos());
        if(!state.streamTags().anyMatch(t->t == BTC.WRENCH_BLACKLIST) && !(state.getBlock() instanceof PistonBlock && state.get(PistonBlock.EXTENDED))) {
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
    }
    public MutableText getDescription1() {
        return Text.literal("Hold Left Alt:");
    }
    public MutableText getDescription2() {
        return Text.literal("To Rotate Blocks that have 6 Directions");
    }
    public MutableText getDescription3() {
        return Text.literal("Disclaimer (Only Works with Blocks From BTC)");
    }
}

package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.CopperWireBlock;
import io.github.tobyrue.btc.block.DungeonDoorBlock;
import io.github.tobyrue.btc.block.DungeonWireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.state.property.Properties;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;

import java.util.List;

public class GoldWrenchItem extends Item {
    public GoldWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        // Check if the block has the FACING property
        if (state.contains(Properties.FACING) &&
                !state.streamTags().anyMatch(t -> t == BTC.WRENCH_BLACKLIST) &&
                !(state.getBlock() instanceof PistonBlock && state.get(PistonBlock.EXTENDED)) &&
                (state.getBlock() instanceof CopperWireBlock && state.get(CopperWireBlock.SURVIVAL))) {

            Direction currentFacing = state.get(Properties.FACING);
            Direction newFacing;

            // Define the rotation order (all 6 directions)
            Direction[] rotationOrder = new Direction[]{
                    Direction.UP, Direction.NORTH, Direction.EAST,
                    Direction.SOUTH, Direction.WEST, Direction.DOWN
            };

            // Find the current direction's position in the rotation order
            int index = -1;
            for (int i = 0; i < rotationOrder.length; i++) {
                if (rotationOrder[i] == currentFacing) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                // If for some reason the current facing is not in the list, default to UP
                newFacing = Direction.UP;
            } else {
                // Determine the next facing direction based on sneaking
                if (context.getPlayer().isSneaking()) {
                    // Sneaking: rotate backwards
                    newFacing = rotationOrder[(index - 1 + rotationOrder.length) % rotationOrder.length];
                } else {
                    // Normal: rotate forwards
                    newFacing = rotationOrder[(index + 1) % rotationOrder.length];
                }
            }
            // Apply the new facing direction to the block
            context.getWorld().setBlockState(context.getBlockPos(), state.with(Properties.FACING, newFacing));
            return ActionResult.SUCCESS;
        }
        // Return pass if the block does not have the FACING property
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
        return Text.literal("Rotate blocks with 6 directions by clicking normally.");
    }
    public MutableText getDescription2() {
        return Text.literal("Hold Shift to rotate in the opposite direction.");
    }
    public MutableText getDescription3() {
        return Text.literal("Disclaimer: Normal Click Might Have Special Actions for Blocks");
    }
}

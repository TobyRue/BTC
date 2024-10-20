package io.github.tobyrue.btc;

import net.minecraft.block.PistonBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;

public class GoldWrenchItem extends Item {
    public GoldWrenchItem(Settings settings) {
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
}

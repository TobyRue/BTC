package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;

public class ButterBucket extends Item {
    public ButterBucket(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var pos = context.getBlockPos();
        var dir = context.getSide();
        var world = context.getWorld();
        var offset = world.getBlockState(pos.offset(dir));
        var offsetAndDown = world.getBlockState(pos.offset(dir).down());
        ActionResult actionResult = super.useOnBlock(context);

        if (context.getPlayer() != null) {
            if (offsetAndDown.hasSolidTopSurface(world, pos.offset(dir).down(), context.getPlayer()) && (offset.isAir() || offset.isReplaceable())) {
                world.setBlockState(pos.offset(dir), ModBlocks.BUTTER.getDefaultState());
                if (actionResult.isAccepted()) {
                    context.getPlayer().setStackInHand(context.getHand(), BucketItem.getEmptiedStack(context.getStack(), context.getPlayer()));
                }
                return ActionResult.CONSUME;
            } else if (world.getBlockState(pos).isReplaceable()) {
                world.setBlockState(pos, ModBlocks.BUTTER.getDefaultState());
                if (actionResult.isAccepted()) {
                    context.getPlayer().setStackInHand(context.getHand(), BucketItem.getEmptiedStack(context.getStack(), context.getPlayer()));
                }
                return ActionResult.CONSUME;
            }
        }
        return ActionResult.CONSUME;
    }
}

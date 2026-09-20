package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;

public class ButterBucket extends Item {
    public ButterBucket(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var pos = context.getBlockPos();
        var dir = context.getSide();
        var world = context.getWorld();
        var offsetPos = pos.offset(dir);
        var offset = world.getBlockState(offsetPos);
        var offsetAndDown = world.getBlockState(offsetPos.down());
        var player = context.getPlayer();

        if (player != null) {
            boolean placed = false;
            var targetPos = pos;

            if (offsetAndDown.hasSolidTopSurface(world, offsetPos.down(), player) && (offset.isAir() || offset.isReplaceable())) {
                targetPos = offsetPos;
                placed = true;
            } else if (world.getBlockState(pos).isReplaceable()) {
                targetPos = pos;
                placed = true;
            }

            if (placed) {
                if (!world.isClient()) {
                    world.setBlockState(targetPos, ModBlocks.BUTTER.getDefaultState());

                    if (!player.getAbilities().creativeMode) {
                        context.getPlayer().setStackInHand(
                                context.getHand(),
                                BucketItem.getEmptiedStack(context.getStack(), player)
                        );
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}
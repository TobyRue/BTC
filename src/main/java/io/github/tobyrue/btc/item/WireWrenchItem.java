package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.CycleDirection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class WireWrenchItem extends Item {
    public WireWrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        CycleDirection current = stack.getOrDefault(BTC.WRENCH_DIRECTION, CycleDirection.UP);

        boolean reverse = player.isSneaking();
        CycleDirection next = current.next(reverse);

        stack.set(BTC.WRENCH_DIRECTION, next);

        if (world.isClient) {
            player.sendMessage(Text.literal("Wrench direction: " + next.name()), true);
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        CycleDirection dir = stack.getOrDefault(BTC.WRENCH_DIRECTION, CycleDirection.UP);
        tooltip.add(Text.literal("Direction: " + dir.name()).formatted(net.minecraft.util.Formatting.AQUA));
        super.appendTooltip(stack, context, tooltip, type);
    }
}

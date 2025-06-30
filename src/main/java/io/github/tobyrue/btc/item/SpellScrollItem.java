package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class SpellScrollItem extends Item {
    public final SpellRegistryEnum spellType;

    public SpellScrollItem(Settings settings, SpellRegistryEnum spellType) {
        super(settings);
        this.spellType = spellType;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 30;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BRUSH;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Start using the item
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if ((user instanceof ServerPlayerEntity player)) {
            var progress = player.getAdvancementTracker().getProgress(player.server.getAdvancementLoader().get(BTC.identifierOf(String.format("adventure/get_%s_scroll", spellType))));
            if (!progress.isDone()) {
                if (spellType != null) {
                    player.getAdvancementTracker().grantCriterion(
                            player.server.getAdvancementLoader().get(BTC.identifierOf(String.format("adventure/get_%s_scroll", spellType))),
                            "scroll"
                    );
                    player.sendMessage(Text.translatable("item.btc.scroll.gained", Text.translatable("item.btc.scroll.gained." + spellType.asString())), true);
                    stack.decrementUnlessCreative(1, user);
                }
            }
        }
        return super.finishUsing(stack, world, user);
    }
}

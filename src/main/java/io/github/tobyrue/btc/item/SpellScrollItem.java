package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpellScrollItem extends Item {
    public final SpellRegistryEnum spellType;

    public SpellScrollItem(Settings settings, SpellRegistryEnum spellType) {
        super(settings);
        this.spellType = spellType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (spellType != null) {
            user.sendMessage(Text.translatable("item.btc.scroll.gained", Text.translatable("item.btc.scroll.gained" + spellType.asString())), true);
            user.getStackInHand(hand).decrementUnlessCreative(1, user);
            return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
        }
        return super.use(world, user, hand);
    }
}

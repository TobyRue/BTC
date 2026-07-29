package io.github.tobyrue.btc.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RagePepperItem extends Item {
    public RagePepperItem(Settings settings) {
        super(settings);
    }
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack resultStack = super.finishUsing(stack, world, user);

        if (!world.isClient()) {
            user.setOnFireFor(15);
        }

        return resultStack;
    }
}

package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.spell.ItemCooldownProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class StaffItem extends Item implements ItemCooldownProvider {
    public StaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClient && entity instanceof LivingEntity) {
            this.tickCooldowns(stack);
        }
    }
    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        if (this instanceof ItemCooldownProvider cp) {
            return cp.getVisibleCooldownKey(stack) != null;
        }
        return false;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (this instanceof ItemCooldownProvider cp) {
            String key = cp.getVisibleCooldownKey(stack);
            if (key != null) {
                float progress = cp.getCooldownProgressInverse(stack, key);
                return Math.round(13 * progress);
            }
        }
        return 0;
    }
}

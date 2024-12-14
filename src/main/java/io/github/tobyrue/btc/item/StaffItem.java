package io.github.tobyrue.btc.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class StaffItem extends Item {
    public StaffItem(Settings settings) {
        super(settings);
    }
    public boolean shoot(LivingEntity user, World world, double x, double y, double z, double v) {

        return false;
    }
}

package io.github.tobyrue.btc.misc;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CornerStorage {
    BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world);
}

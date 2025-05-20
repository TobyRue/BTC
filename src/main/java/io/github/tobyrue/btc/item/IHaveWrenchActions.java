package io.github.tobyrue.btc.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IHaveWrenchActions {
    ActionResult onWrenchUse(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction hitSide);
}

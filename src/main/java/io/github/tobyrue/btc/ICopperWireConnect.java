package io.github.tobyrue.btc;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICopperWireConnect {
    boolean shouldCopperConnect(BlockState state, World world, BlockPos pos);
}

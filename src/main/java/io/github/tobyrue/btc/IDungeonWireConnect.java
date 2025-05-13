package io.github.tobyrue.btc;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDungeonWireConnect {
    boolean shouldConnect(BlockState state, World world, BlockPos pos);
}

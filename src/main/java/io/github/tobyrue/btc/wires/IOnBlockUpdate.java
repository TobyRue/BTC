package io.github.tobyrue.btc.wires;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IOnBlockUpdate {
    void onUpdate(World world, BlockPos pos, BlockState state);
}

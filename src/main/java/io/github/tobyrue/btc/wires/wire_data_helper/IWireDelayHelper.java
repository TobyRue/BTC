package io.github.tobyrue.btc.wires.wire_data_helper;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWireDelayHelper {
    void setDelay(int delay, World world, BlockState state, BlockPos pos);

    int getDelay(World world, BlockState state, BlockPos pos);
}

package io.github.tobyrue.btc.wires;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IDungeonWire {
    boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face);
}

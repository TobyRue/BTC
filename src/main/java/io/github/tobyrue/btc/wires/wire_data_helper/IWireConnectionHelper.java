package io.github.tobyrue.btc.wires.wire_data_helper;

import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public interface IWireConnectionHelper {
    void setConnection(Direction face, WireBlock.ConnectionType connectionType, World world, BlockState state, BlockPos pos);

    WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos);

    WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos);

    Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos);
}

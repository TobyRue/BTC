package io.github.tobyrue.btc.wires.wire_data_helper;

import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWireOperatorHelper {
    void setOperator(WireBlock.Operator op, World world, BlockState state, BlockPos pos);

    WireBlock.Operator cycleOperator(World world, BlockState state, BlockPos pos);

    WireBlock.Operator getOperator(World world, BlockState state, BlockPos pos);
}

package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.DungeonWireBlock;
import io.github.tobyrue.btc.block.FireDispenserBlock;
import io.github.tobyrue.btc.enums.FireDispenserType;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface IDungeonWirePowered {
    default boolean shouldWirePower(BlockState state, World world, BlockPos pos, boolean top, boolean bottom, boolean sides) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Direction dirFromNeighborToThis = direction.getOpposite();
            if (neighborState.getBlock() instanceof WireBlock) {
                if ((direction != Direction.DOWN && direction != Direction.UP && sides) || (direction == Direction.DOWN && bottom) || (direction == Direction.UP && top)) {
                    var property = neighborState.get(WireBlock.CONNECTION_TO_DIRECTION.get().inverse().get(dirFromNeighborToThis));
                    if (property == WireBlock.ConnectionType.OUTPUT && neighborState.get(WireBlock.POWERED)) {
                        boolean isPowered = neighborState.get(WireBlock.POWERED);
                        return isPowered;
                    }
                }
            }
        }
        return false;
    }
}

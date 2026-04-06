package io.github.tobyrue.btc.wires;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Deprecated
public interface IDungeonWirePowered {
    //TODO Make static and add to wire
    default boolean shouldWirePower(BlockState state, World world, BlockPos pos, boolean top, boolean bottom, boolean sides) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Direction dirFromNeighborToThis = direction.getOpposite();
            if (neighborState.getBlock() instanceof WireBlockSlow) {
                if ((direction != Direction.DOWN && direction != Direction.UP && sides) || (direction == Direction.DOWN && bottom) || (direction == Direction.UP && top)) {
                    var property = neighborState.get(WireBlockSlow.CONNECTION_TO_DIRECTION.get().inverse().get(dirFromNeighborToThis));
                    if (property == WireBlockSlow.ConnectionType.OUTPUT && neighborState.get(WireBlockSlow.POWERED)) {
                        boolean isPowered = neighborState.get(WireBlockSlow.POWERED);
                        return isPowered;
                    }
                }
            }
        }
        return false;
    }
}

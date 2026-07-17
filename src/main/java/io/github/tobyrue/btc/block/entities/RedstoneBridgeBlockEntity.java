package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.RedstoneBridgeBlock;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneBridgeBlockEntity extends BlockEntity {
    private final int[] cachedOutputs = new int[6];

    public RedstoneBridgeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REDSTONE_BRIDGE_BLOCK_ENTITY, pos, state);
    }

    /**
     * Instantly returns the cached value.
     * No active world queries mean zero risk of NullPointerExceptions or thread-access desyncs!
     */
    public int getWeakPower(Direction direction) {
        return cachedOutputs[direction.getOpposite().getId()];
    }

    /**
     * Recalculates and caches the bridge's redstone outputs.
     * Returns true if any of the outputs changed, signaling that we need to update neighbors.
     */
    public boolean updateCachedPower(World world, BlockState state) {
        boolean changed = false;

        for (Direction dir : Direction.values()) {
            int oldPower = cachedOutputs[dir.getId()];
            int newPower = calculateOutputPowerForSide(world, state, dir);

            if (oldPower != newPower) {
                cachedOutputs[dir.getId()] = newPower;
                changed = true;
            }
        }

        if (changed) {
            markDirty();
        }
        return changed;
    }

    private int calculateOutputPowerForSide(World world, BlockState state, Direction direction) {
        return switch (direction) {
            case EAST -> switch (state.get(RedstoneBridgeBlock.X_AXIS)) {
                case ALIGNED -> getPowerFromSide(world, Direction.WEST);
                case INVERTED, NONE -> 0;
            };
            case WEST -> switch (state.get(RedstoneBridgeBlock.X_AXIS)) {
                case INVERTED -> getPowerFromSide(world, Direction.EAST);
                case ALIGNED, NONE -> 0;
            };

            case UP -> switch (state.get(RedstoneBridgeBlock.Y_AXIS)) {
                case ALIGNED -> getPowerFromSide(world, Direction.DOWN);
                case INVERTED, NONE -> 0;
            };
            case DOWN -> switch (state.get(RedstoneBridgeBlock.Y_AXIS)) {
                case INVERTED -> getPowerFromSide(world, Direction.UP);
                case ALIGNED, NONE -> 0;
            };

            case SOUTH -> switch (state.get(RedstoneBridgeBlock.Z_AXIS)) {
                case ALIGNED -> getPowerFromSide(world, Direction.NORTH);
                case INVERTED, NONE -> 0;
            };
            case NORTH -> switch (state.get(RedstoneBridgeBlock.Z_AXIS)) {
                case INVERTED -> getPowerFromSide(world, Direction.SOUTH);
                case ALIGNED, NONE -> 0;
            };
        };
    }

    private int getPowerFromSide(World world, Direction direction) {
        BlockPos neighborPos = this.pos.offset(direction);

        int power = world.getEmittedRedstonePower(neighborPos, direction);

        System.out.printf("[Bridge Cache System] Pos %s read raw power: %d from %s%n",
                this.pos.toShortString(), power, direction.name());

        return power;
    }
}
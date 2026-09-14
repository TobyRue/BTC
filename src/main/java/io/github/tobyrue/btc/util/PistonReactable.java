package io.github.tobyrue.btc.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface PistonReactable {

    boolean canBePushed(BlockState state);

    /**
     * How many pushes in a row are required to trigger the reaction.
     */
    int getRequiredPushes(BlockState state);

    /**
     * Maximum ticks allowed between pushes before the push count resets.
     * Example: 40 ticks = 2 seconds.
     */
    long getMaxTickDelay(BlockState state);

    /**
     * Called when the block has been pushed enough times within the allowed delay.
     *
     * @param world The world instance.
     * @param pos The position the block was just moved to.
     * @param state The current BlockState.
     * @param pushCount The number of consecutive pushes reached.
     */
    BlockState onPistonReaction(World world, BlockPos pos, BlockState state, int pushCount);
}
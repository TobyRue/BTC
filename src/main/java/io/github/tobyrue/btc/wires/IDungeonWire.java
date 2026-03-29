package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.block.DungeonFlameBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

/**
 * Should be implemented into a class that gives off wire power.
 */
public interface IDungeonWire {
    /**
     * On {@link IDungeonWire} being implemented to a class this method should check what directions in reference to the <code>face</code> to see if it should output wire power, should return true if that side in that case will give wire power.
     * The parameters represent the offset values rather than the block's own, calculated in the inverse direction of the block's current <code>face</code> orientation (not in the implementation but in the use case).
     * @param state The {@link BlockState} of the block that is being checked to see if it is emitting wire power.
     * @param pos The {@link BlockPos} of the block that is being checked to see if it is emitting wire power.
     * @param face The block face at <code>pos</code> to check for wire power emission.
     * @return True if the given <code>face</code> of the given block is emitting wire power
     */
    boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face);

    /**
     * The parameters represent the block's own values.
     * @param state The {@link BlockState} of the block being checked to see if it is receiving wire power.
     * @param pos The {@link BlockPos} of the block being checked to see if it is receiving wire power.
     * @param faces The faces of the block at <code>pos</code> to check for wire power.
     * @return True if there is a found source of a type of wire power coming from any of the given <code>faces</code>.
     */
    static boolean isReceivingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction... faces) {
        return Arrays.stream(faces).anyMatch(face ->
                world.getBlockState(pos.offset(face)).getBlock() instanceof IDungeonWire wire
                && wire.isEmittingDungeonWirePower(
                        world.getBlockState(pos.offset(face)),
                        world,
                        pos.offset(face),
                        face.getOpposite()
                )
        );
    }
}

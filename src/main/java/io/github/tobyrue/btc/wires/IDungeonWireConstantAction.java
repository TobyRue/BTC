package io.github.tobyrue.btc.wires;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDungeonWireConstantAction {
    //fix me moving block next to doors does not update them and cables don't do sound.
    void onDungeonWireChange(BlockState state, World world, BlockPos pos, boolean powered);

}

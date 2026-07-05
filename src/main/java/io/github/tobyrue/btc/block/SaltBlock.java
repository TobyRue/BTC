package io.github.tobyrue.btc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.piston.PistonBehavior;
    import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SaltBlock extends Block {
    public static final IntProperty ABSORBED_LIGHT = IntProperty.of("absorbed_light", 0, 15);

    public SaltBlock(Settings settings) {
        super(settings.luminance(state -> state.get(ABSORBED_LIGHT)).ticksRandomly());
        this.setDefaultState(this.stateManager.getDefaultState().with(ABSORBED_LIGHT, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ABSORBED_LIGHT);
    }

    /**
     * Randomly ticks to absorb light from its surroundings, behaving like a sponge for ambient light.
     */
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int currentStored = state.get(ABSORBED_LIGHT);
        int surroundingLight = world.getLightLevel(pos);

        if (surroundingLight > currentStored) {
            world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored + 1), 3);
        } else if (surroundingLight < currentStored && random.nextInt(5) == 0) {
            world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored - 1), 3);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved && !world.isClient) {
            int storedLight = state.get(ABSORBED_LIGHT);

            if (storedLight > 0) {
                for (Direction dir : Direction.values()) {
                    BlockPos neighborPos = pos.offset(dir);
                    world.updateNeighborsAlways(neighborPos, this);
                }
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
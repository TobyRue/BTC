package io.github.tobyrue.btc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

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

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.scheduleBlockTick(pos, this, 10);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 10);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int currentStored = state.get(ABSORBED_LIGHT);

        int maxSurroundingLight = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            if (!world.getBlockState(neighborPos).isOpaqueFullCube(world, neighborPos)) {
                int blockLight = world.getLightLevel(LightType.BLOCK, neighborPos);

                maxSurroundingLight = Math.max(maxSurroundingLight, blockLight);
            }
        }
        if (maxSurroundingLight > currentStored) {
            world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored + 1), 3);
        } else if (maxSurroundingLight <= currentStored && currentStored > 0) {
            if (random.nextInt(3) == 0) {
                world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored - 1), 3);
            }
        }

        world.scheduleBlockTick(pos, this, 20);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        //TODO
        if (!world.getBlockTickScheduler().isQueued(pos, this)) {
            world.scheduleBlockTick(pos, this, 20);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved && !world.isClient && state.get(ABSORBED_LIGHT) > 0) {
            for (Direction dir : Direction.values()) {
                world.updateNeighborsAlways(pos.offset(dir), this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
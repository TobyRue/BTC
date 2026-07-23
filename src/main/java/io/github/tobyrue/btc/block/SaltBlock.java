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
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos. offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getLuminance() > state.get(ABSORBED_LIGHT)) {
                world.setBlockState(pos, state.with(ABSORBED_LIGHT, neighborState.getLuminance()));
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos. offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getLuminance() > state.get(ABSORBED_LIGHT)) {
                world.setBlockState(pos, state.with(ABSORBED_LIGHT, neighborState.getLuminance()));
            }
        }
    }
}
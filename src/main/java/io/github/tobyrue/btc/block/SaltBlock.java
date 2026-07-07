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
        boolean isSuperheated = isHeatSource(world.getBlockState(pos.down()));

        int maxSurroundingLight = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            if (!world.getBlockState(neighborPos).isOpaqueFullCube(world, neighborPos)) {
                int blockLight = world.getLightLevel(LightType.BLOCK, neighborPos);

                maxSurroundingLight = Math.max(maxSurroundingLight, blockLight);
            }
        }
        if (isSuperheated) {
            if (currentStored < 15) {
                world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored + 1), 3);
            }
        } else if (maxSurroundingLight > currentStored) {
            world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored + 1), 3);
        } else if (maxSurroundingLight <= currentStored && currentStored > 0) {
            if (random.nextInt(3) == 0) {
                world.setBlockState(pos, state.with(ABSORBED_LIGHT, currentStored - 1), 3);
            }
        }

        world.scheduleBlockTick(pos, this, 20);
    }


    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int heat = state.get(ABSORBED_LIGHT);
        ItemStack heldItem = player.getStackInHand(player.getActiveHand());

        if (heat >= 6 && !heldItem.isEmpty()) {
            Optional<SmeltingRecipe> recipe = world.getRecipeManager()
                    .getFirstMatch(RecipeType.SMELTING, new SingleStackRecipeInput(heldItem), world)
                    .map(entry -> entry.value());

            if (recipe.isPresent()) {
                if (!world.isClient) {
                    ItemStack cookedResult = recipe.get().getResult(world.getRegistryManager()).copy();

                    heldItem.decrement(1);
                    if (!player.getInventory().insertStack(cookedResult)) {
                        player.dropItem(cookedResult, false);
                    }

                    int newHeat = Math.max(0, heat - 2);
                    world.setBlockState(pos, state.with(ABSORBED_LIGHT, newHeat), 3);

                    world.playSound(null, pos, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.2F);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int heat = state.get(ABSORBED_LIGHT);
        if (heat >= 8) {
            double x = (double)pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double y = (double)pos.getY() + 1.0;
            double z = (double)pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;

            world.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.02, 0.0);
            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 0.01, 0.0);
            }
        }
    }

    private boolean isHeatSource(BlockState state) {
        return state.isOf(Blocks.CAMPFIRE) ||
                state.isOf(Blocks.SOUL_CAMPFIRE) ||
                state.isOf(Blocks.LAVA) ||
                state.isOf(Blocks.FIRE) ||
                state.isOf(Blocks.SOUL_FIRE) ||
                state.isOf(Blocks.MAGMA_BLOCK);
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
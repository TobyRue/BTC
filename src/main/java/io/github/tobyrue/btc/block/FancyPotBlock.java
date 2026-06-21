package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.FancyPotBlockEntity;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class FancyPotBlock extends Block implements ModBlockEntityProvider<FancyPotBlockEntity>, Waterloggable {
    private static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    @Override
    public BlockEntityType<FancyPotBlockEntity> getBlockEntityType() {
        return ModBlockEntities.FANCY_RED_BLOCK_ENTITY;
    }

    public FancyPotBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof FancyPotBlockEntity pot) {
            if (world.isClient) {
                return ItemActionResult.CONSUME;
            } else {
                ItemStack itemStack = pot.getStack();
                if (!stack.isEmpty() && (itemStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(itemStack, stack) && itemStack.getCount() < itemStack.getMaxCount())) {
                    player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                    ItemStack itemStack2 = stack.splitUnlessCreative(1, player);
                    float f;
                    if (pot.isEmpty()) {
                        pot.setStack(itemStack2);
                        f = (float)itemStack2.getCount() / (float)itemStack2.getMaxCount();
                    } else {
                        itemStack.increment(1);
                        f = (float)itemStack.getCount() / (float)itemStack.getMaxCount();
                    }

                    world.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_DECORATED_POT_INSERT, SoundCategory.BLOCKS, 1.0F, 0.7F + 0.5F * f);
                    if (world instanceof ServerWorld) {
                        ServerWorld serverWorld = (ServerWorld)world;
                        serverWorld.spawnParticles(ParticleTypes.DUST_PLUME, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, 7, 0.0, 0.0, 0.0, 0.0);
                    }

                    pot.markDirty();
                    world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    return ItemActionResult.SUCCESS;
                } else {
                    return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
            }
        } else {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(Properties.WATERLOGGED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.WATERLOGGED);
    }
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(Properties.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }


    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        BlockEntity var5 = world.getBlockEntity(pos);
        if (var5 instanceof FancyPotBlockEntity dungeonPotBlockEntity) {
            return dungeonPotBlockEntity.asStack(state);
        } else {
            return super.getPickStack(world, pos, state);
        }
    }

    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    public static class RedFancyPot extends FancyPotBlock implements ModBlockEntityProvider<FancyPotBlockEntity>, Waterloggable  {

        @Override
        public BlockEntityType<FancyPotBlockEntity> getBlockEntityType() {
            return ModBlockEntities.FANCY_RED_BLOCK_ENTITY;
        }

        public RedFancyPot(Settings settings) {
            super(settings);
            this.setDefaultState(this.stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
        }

        @Nullable
        @Override
        public BlockState getPlacementState(ItemPlacementContext ctx) {
            return this.getDefaultState()
                    .with(Properties.WATERLOGGED, false);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
            builder.add(Properties.WATERLOGGED);
        }
    }
    public static class GreenFancyPot extends FancyPotBlock implements ModBlockEntityProvider<FancyPotBlockEntity>, Waterloggable  {

        @Override
        public BlockEntityType<FancyPotBlockEntity> getBlockEntityType() {
            return ModBlockEntities.FANCY_GREEN_BLOCK_ENTITY;
        }

        public GreenFancyPot(Settings settings) {
            super(settings);
            this.setDefaultState(this.stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
        }

        @Nullable
        @Override
        public BlockState getPlacementState(ItemPlacementContext ctx) {
            return this.getDefaultState()
                    .with(Properties.WATERLOGGED, false);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
            builder.add(Properties.WATERLOGGED);
        }
    }
    public static class BlueFancyPot extends FancyPotBlock implements ModBlockEntityProvider<FancyPotBlockEntity>, Waterloggable  {

        @Override
        public BlockEntityType<FancyPotBlockEntity> getBlockEntityType() {
            return ModBlockEntities.FANCY_BLUE_BLOCK_ENTITY;
        }

        public BlueFancyPot(Settings settings) {
            super(settings);
            this.setDefaultState(this.stateManager.getDefaultState().with(Properties.WATERLOGGED, false));
        }

        @Nullable
        @Override
        public BlockState getPlacementState(ItemPlacementContext ctx) {
            return this.getDefaultState()
                    .with(Properties.WATERLOGGED, false);
        }

        @Override
        protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
            builder.add(Properties.WATERLOGGED);
        }
    }
}

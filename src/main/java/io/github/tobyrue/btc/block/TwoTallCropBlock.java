package io.github.tobyrue.btc.block;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TwoTallCropBlock extends PlantBlock implements Fertilizable {
    public static final MapCodec<TwoTallCropBlock> CODEC =
            RecordCodecBuilder.mapCodec((instance) -> instance.group(
                            RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("seed")
                                    .forGetter((block) -> block.pickBlockItem), createSettingsCodec())
                    .apply(instance, TwoTallCropBlock::new));

    private final RegistryKey<Item> pickBlockItem;
    public static final IntProperty AGE = IntProperty.of("age", 0, 9);
    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;

    public TwoTallCropBlock(RegistryKey<Item> pickBlockItem, AbstractBlock.Settings settings) {
        super(settings);
        this.pickBlockItem = pickBlockItem;
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0).with(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends PlantBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(AGE) < 9;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int nextAge = Math.min(9, state.get(AGE) + MathHelper.nextInt(world.random, 2, 5));
        BlockPos lowerPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState lowerState = world.getBlockState(lowerPos);

        if (lowerState.isOf(this)) {
            if (nextAge >= 4) {
                BlockPos upperPos = lowerPos.up();
                BlockState upperState = world.getBlockState(upperPos);
                if (upperState.isAir() || upperState.isOf(this)) {
                    world.setBlockState(lowerPos, lowerState.with(AGE, nextAge).with(HALF, DoubleBlockHalf.LOWER), 2);
                    world.setBlockState(upperPos, withWaterloggedState(world, upperPos, (BlockState)((BlockState)this.getDefaultState().with(AGE, nextAge)).with(HALF, DoubleBlockHalf.UPPER)), 2);
                } else {
                    world.setBlockState(lowerPos, lowerState.with(AGE, 3).with(HALF, DoubleBlockHalf.LOWER), 2);
                }
            } else {
                world.setBlockState(lowerPos, lowerState.with(AGE, nextAge).with(HALF, DoubleBlockHalf.LOWER), 2);
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER && world.getBaseLightLevel(pos, 0) >= 9) {
            if (world.getBlockState(pos.down()).isOf(Blocks.FARMLAND)) {
                float moisture = CropBlock.getAvailableMoisture(this, world, pos);
                if (random.nextInt((int)(25.0F / moisture) + 1) == 0) {
                    int age = getAge(state);
                    if (age < getMaxAge()) {
                        int nextAge = age + 1;
                        if (nextAge >= 4) {
                            BlockPos upperPos = pos.up();
                            BlockState upperState = world.getBlockState(upperPos);
                            if (upperState.isAir() || upperState.isOf(this)) {
                                world.setBlockState(pos, state.with(AGE, nextAge), 2);
                                world.setBlockState(upperPos, withWaterloggedState(world, upperPos, this.getDefaultState().with(AGE, nextAge).with(HALF, DoubleBlockHalf.UPPER)), 2);
                            }
                        } else {
                            world.setBlockState(pos, state.with(AGE, nextAge), 2);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.get(HALF);
        if (state.get(AGE) >= 4) {
            if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
                if (!neighborState.isOf(this) || neighborState.get(HALF) == doubleBlockHalf) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }
        if (doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    protected IntProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 9;
    }

    public int getAge(BlockState state) {
        return state.get(this.getAgeProperty());
    }

    public BlockState withAge(int age) {
        return this.getDefaultState().with(this.getAgeProperty(), age);
    }

    public final boolean isMature(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof RavagerEntity && world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            world.breakBlock(pos, true, entity);
        }

        super.onEntityCollision(state, world, pos, entity);
    }


    @Override
    protected boolean hasRandomTicks(BlockState state) {
        return !this.isMature(state);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (state.get(HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState = world.getBlockState(pos.down());
            return blockState.isOf(this) && blockState.get(HALF) == DoubleBlockHalf.LOWER && blockState.get(AGE) >= 4;
        } else {
            return super.canPlaceAt(state, world, pos);
        }
    }

    public static BlockState withWaterloggedState(WorldView world, BlockPos pos, BlockState state) {
        return state.contains(Properties.WATERLOGGED) ? (BlockState)state.with(Properties.WATERLOGGED, world.isWater(pos)) : state;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            if (player.isCreative()) {
                onBreakInCreative(world, pos, state, player);
            } else if (state.get(AGE) == 9) {
                Registry<Item> registry = world.getRegistryManager().get(RegistryKeys.ITEM);
                Optional<Item> optionalItem = registry.getOrEmpty(this.pickBlockItem);
                optionalItem.ifPresent(item -> dropStack(world, pos, item.getDefaultStack()));
            }
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, Blocks.AIR.getDefaultState(), blockEntity, tool);
    }

    protected static void onBreakInCreative(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)state.get(HALF);
        if (doubleBlockHalf == DoubleBlockHalf.UPPER) {
            BlockPos blockPos = pos.down();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(state.getBlock()) && blockState.get(HALF) == DoubleBlockHalf.LOWER) {
                BlockState blockState2 = blockState.getFluidState().isOf(Fluids.WATER) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState2, 35);
                world.syncWorldEvent(player, 2001, blockPos, Block.getRawIdFromState(blockState));
            }
        }
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return new ItemStack(DataFixUtils.orElse(world.getRegistryManager().get(RegistryKeys.ITEM).getOrEmpty(this.pickBlockItem), this));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE, HALF);
    }

    @Override
    protected long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    protected int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }
}
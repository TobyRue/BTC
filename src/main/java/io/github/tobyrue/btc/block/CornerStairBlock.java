package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CornerStairBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST  = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST  = BooleanProperty.of("west");

    public CornerStairBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(HALF, BlockHalf.BOTTOM).with(WATERLOGGED, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
    }


    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        return (this.getDefaultState()).with(HALF, direction == Direction.DOWN || direction != Direction.UP && ctx.getHitPos().y - (double)blockPos.getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, WATERLOGGED, NORTH, EAST, SOUTH, WEST);
        super.appendProperties(builder);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof MeltingIceBlock) {
                updateConnections(world, neighborPos, neighborState);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        for (Direction dir : Direction.values()) {
            if (world.getBlockState(pos.offset(dir)).getBlock() instanceof MeltingIceBlock && !world.isClient()) {
                updateConnections((World) world, pos.offset(dir), world.getBlockState(pos.offset(dir)));
            }
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.offset(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this)) {
                updateConnections(world, neighborPos, neighborState);
            }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }


    private void updateConnections(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        BlockState newState = state
                .with(NORTH, !isSameBlock(world, pos.north()))
                .with(EAST, !isSameBlock(world, pos.east()))
                .with(SOUTH, !isSameBlock(world, pos.south()))
                .with(WEST, !isSameBlock(world, pos.west()));

        world.setBlockState(pos, newState, 3);
    }

    private boolean isSameBlock(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(this);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, NavigationType type) {
        return false;
    }
}

package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.util.VoxelShapeRotator;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SaltCrystalBlock extends Block implements Waterloggable {
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty LIT = Properties.LIT;
    private final int level;

    public static VoxelShape makeShapeLvl1() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.3125, 0, 0.25, 0.5625, 0.25, 0.5)
        );
    }
    public static VoxelShape makeShapeLvl2() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.3125, 0, 0.25, 0.5625, 0.25, 0.5),
                VoxelShapes.cuboid(0.5, 0, 0.4375, 0.75, 0.5, 0.6875),
                VoxelShapes.cuboid(0.5625, 0, 0.3125, 0.6875, 0.125, 0.4375),
                VoxelShapes.cuboid(0.375, 0, 0.5, 0.5, 0.125, 0.625)
        );
    }
    public static VoxelShape makeShapeLvl3() {
        return VoxelShapes.union(
                VoxelShapes.cuboid(0.3125, 0, 0.3125, 0.8125, 0.5, 0.8125),
                VoxelShapes.cuboid(0.125, 0, 0.125, 0.5, 0.375, 0.5),
                VoxelShapes.cuboid(0.5, 0, 0.1875, 0.625, 0.125, 0.3125),
                VoxelShapes.cuboid(0.1875, 0, 0.5, 0.3125, 0.125, 0.625),
                VoxelShapes.cuboid(0.5, 0, 0.6875, 0.6875, 0.1875, 0.875)
        );
    }

    public SaltCrystalBlock(Settings settings, int level) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.UP).with(LIT, false));
        this.level = level;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.get(FACING).getOpposite() && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }

        FluidState currentFluidState = world.getFluidState(pos);
        FluidState currentFluidStateUp = world.getFluidState(pos.up());
        boolean isWaterlogged = state.get(WATERLOGGED);

        if (!isWaterlogged && ((currentFluidState.getFluid() == Fluids.WATER || currentFluidState.getFluid() == Fluids.FLOWING_WATER) || (currentFluidStateUp.getFluid() == Fluids.WATER || currentFluidStateUp.getFluid() == Fluids.FLOWING_WATER))) {
            state = state.with(WATERLOGGED, true);
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        } else if (isWaterlogged) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        WorldAccess worldAccess = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = worldAccess.getFluidState(blockPos);

        boolean isWater = fluidState.getFluid() == Fluids.WATER || fluidState.getFluid() == Fluids.FLOWING_WATER;
        return this.getDefaultState()
                .with(WATERLOGGED, isWater)
                .with(FACING, ctx.getSide());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, LIT);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.GLOWSTONE_DUST) && !state.get(LIT)) {
            world.setBlockState(pos, state.with(LIT, true));
            stack.decrementUnlessCreative(1, player);
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private static final Map<Direction, VoxelShape>[] SHAPES_BY_LEVEL;

    static {
        SHAPES_BY_LEVEL = new Map[] {
                VoxelShapeRotator.makeShapeMap(makeShapeLvl1()),
                VoxelShapeRotator.makeShapeMap(makeShapeLvl2()),
                VoxelShapeRotator.makeShapeMap(makeShapeLvl3())
        };
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_LEVEL[this.level - 1].get(state.get(FACING));
    }
}
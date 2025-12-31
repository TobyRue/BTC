package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ColumnBlock extends Block implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty IS_END = BooleanProperty.of("is_end");

    public Block getBaseBlock() {
        return baseBlock;
    }

    private final Block baseBlock;

    private static final VoxelShape BOTTOM_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1),
            VoxelShapes.cuboid(0.25, 0.5, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape TOP_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
    );
    private static final VoxelShape NORTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.25, 0.5, 0.75, 0.75, 1),
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5)
    );
    private static final VoxelShape EAST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.25, 0.5, 0.75, 0.75),
            VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1)
    );
    private static final VoxelShape SOUTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.5),
            VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1)
    );
    private static final VoxelShape WEST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0.25, 0.25, 1, 0.75, 0.75),
            VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1)
    );

    private static final VoxelShape COLUMN_UP_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape COLUMN_NORTH_SOUTH =  VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 1)
    );
    private static final VoxelShape COLUMN_EAST_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.25, 1, 0.75, 0.75)
    );

    public ColumnBlock(Settings settings, Block baseBlock) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false).with(FACING, Direction.DOWN).with(IS_END, false));
        this.baseBlock = baseBlock;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getSide();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        BlockState state = this.getDefaultState()
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(FACING, facing)
                .with(IS_END, false);

        return super.getPlacementState(ctx);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof BlockItem blockItem){
            if (blockItem.getBlock() == baseBlock && !state.get(IS_END)) {
                world.setBlockState(pos, state.with(IS_END, true));
                stack.decrementUnlessCreative(1, player);
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }



    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.get(IS_END) && !world.isClient()) {
            Block.dropStack(Objects.requireNonNull(world.getServer()).getOverworld().toServerWorld(), pos, baseBlock.asItem().getDefaultStack());
        }
        super.onBroken(world, pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, FACING, IS_END);
        super.appendProperties(builder);
    }


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(IS_END)) {
            switch (state.get(FACING)) {
                case NORTH -> {
                    return NORTH_MAIN;
                }
                case EAST -> {
                    return EAST_MAIN;
                }
                case SOUTH -> {
                    return SOUTH_MAIN;
                }
                case WEST -> {
                    return WEST_MAIN;
                }
                case UP -> {
                    return TOP_MAIN;
                }
                case DOWN -> {
                    return BOTTOM_MAIN;
                }
                default -> {}
            }
        } else {
            switch (state.get(FACING).getAxis()) {
                case X -> {
                    return COLUMN_EAST_WEST;
                }
                case Y -> {
                    return COLUMN_UP_DOWN;
                }
                case Z -> {
                    return COLUMN_NORTH_SOUTH;
                }
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (state.get(IS_END)) {
            switch (state.get(FACING)) {
                case NORTH -> {
                    return NORTH_MAIN;
                }
                case EAST -> {
                    return EAST_MAIN;
                }
                case SOUTH -> {
                    return SOUTH_MAIN;
                }
                case WEST -> {
                    return WEST_MAIN;
                }
                case UP -> {
                    return TOP_MAIN;
                }
                case DOWN -> {
                    return BOTTOM_MAIN;
                }
                default -> {}
            }
        } else {
            switch (state.get(FACING).getAxis()) {
                case X -> {
                    return COLUMN_EAST_WEST;
                }
                case Y -> {
                    return COLUMN_UP_DOWN;
                }
                case Z -> {
                    return COLUMN_NORTH_SOUTH;
                }
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }



    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            Direction dir,
            BlockState neighborState,
            WorldAccess world,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return state;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.LAVA.getStill(false);
        }
        return super.getFluidState(state);
    }
}

package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PilasterBlock extends HorizontalConnectingBlock {

    //VALUES FROM HorizontalConnectingBlock are not just n-e-s-w, if it is vertical then it is up down left right according to how is looks like facing the tip of the pilaster
    //FACING is the opposite direction that the back is on. UP : NORTH, RIGHT : EAST, DOWN : SOUTH, LEFT : WEST

    public static final MapCodec<PilasterBlock> CODEC = PilasterBlock.createCodec(PilasterBlock::new);
    private static final VoxelShape EMPTY = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0, 0, 0)
    );
    private static final VoxelShape BOTTOM_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1),
            VoxelShapes.cuboid(0.25, 0.5, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape BOTTOM_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.5, 0, 0.75, 1, 0.25)
    );
    private static final VoxelShape BOTTOM_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0.5, 0.25, 1, 1, 0.75)
    );
    private static final VoxelShape BOTTOM_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.5, 0.75, 0.75, 1, 1)
    );
    private static final VoxelShape BOTTOM_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0.25, 0.25, 1, 0.75)
    );

    private static final VoxelShape TOP_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
    );
    private static final VoxelShape TOP_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.5, 0.25)
    );
    private static final VoxelShape TOP_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0, 0.25, 1, 0.5, 0.75)
    );
    private static final VoxelShape TOP_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.75, 0.75, 0.5, 1)
    );
    private static final VoxelShape TOP_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.25, 0.25, 0.5, 0.75)
    );

    private static final VoxelShape NORTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.5)
    );
    private static final VoxelShape EAST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1),
            VoxelShapes.cuboid(0.5, 0.25, 0.25, 1, 0.75, 0.75)
    );
    private static final VoxelShape SOUTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5),
            VoxelShapes.cuboid(0.25, 0.25, 0.5, 0.75, 0.75, 1)
    );
    private static final VoxelShape WEST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1),
            VoxelShapes.cuboid(0, 0.25, 0.25, 0.5, 0.75, 0.75)
    );

    protected PilasterBlock(Settings settings) {
        super(0, 0, 0, 0, 0, settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(FacingBlock.FACING, Direction.DOWN).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        Direction facing = ctx.getHorizontalPlayerFacing();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);

        BlockState state = this.getDefaultState()
                .with(FacingBlock.FACING, direction.getAxis().isVertical() ? direction.getOpposite() : direction)
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);

        if (direction.getAxis().isVertical()) {
            for (Direction dir : Direction.Type.HORIZONTAL) {
                if (getProperty(dir, direction) != null) {
                    state = state.with(getProperty(dir, direction), false);
                }
            }
        } else {
            for (Direction dir : Direction.values()) {
                if (getProperty(dir, direction) != null) {
                    state = state.with(getProperty(dir, direction), false);
                }
            }
        }

//        if (facing.getOpposite().getAxis() == Direction.Axis.Z) {
//            state = state.with(EAST, true).with(WEST, true);
//        } else {
//            state = state.with(NORTH, true).with(SOUTH, true);
//        }

        return state;
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
        var facing = state.get(FacingBlock.FACING);
        var facingN = state.get(FacingBlock.FACING);

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }


        if (facing.getAxis().isVertical()) {
            if (!dir.getAxis().isHorizontal()) {
                return state;
            }

            boolean connect =
                    neighborState.isIn(BTC.PILASTER) ||
                            neighborState.isIn(BlockTags.FENCES) ||
                            neighborState.isIn(BlockTags.WALLS) ||
                            neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite());

            if (getProperty(dir, facing) != null) {
                return state.with(getProperty(dir, facing), connect);
            }
        } else {

            if (facingN != facing) {
                return state;
            }

            boolean connect =
                    neighborState.isIn(BTC.PILASTER) ||
                            neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite());

            System.out.println("PROPERTY: " + getProperty(dir, facing) + " Pos: " + pos);
            if (getProperty(dir, facing) != null) {
                return state.with(getProperty(dir, facing), connect);
            }
        }
        return state;
    }


    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighbors(pos, this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    //FACING is the opposite direction that the back is on. UP : NORTH, RIGHT : EAST, DOWN : SOUTH, LEFT : WEST
    @Nullable
    private static BooleanProperty getProperty(Direction direction, Direction facing) {
        if (facing.getAxis().isVertical()) {
            switch (direction) {
                case NORTH -> {
                    return NORTH;
                }
                case EAST -> {
                    return EAST;
                }
                case SOUTH -> {
                    return SOUTH;
                }
                case WEST -> {
                    return WEST;
                }
                default -> {
                    return null;
                }
            }
        } else {
            if (facing == Direction.NORTH) {
                switch (direction) {
                    case UP -> {
                        return NORTH;
                    }
                    case EAST -> {
                        return EAST;
                    }
                    case DOWN -> {
                        return SOUTH;
                    }
                    case WEST -> {
                        return WEST;
                    }
                    default -> {
                        return null;
                    }
                }
            } else if (facing == Direction.EAST) {
                switch (direction) {
                    case UP -> {
                        return NORTH;
                    }
                    case SOUTH -> {
                        return EAST;
                    }
                    case DOWN -> {
                        return SOUTH;
                    }
                    case NORTH -> {
                        return WEST;
                    }
                    default -> {
                        return null;
                    }
                }
            } else if (facing == Direction.SOUTH) {
                switch (direction) {
                    case UP -> {
                        return NORTH;
                    }
                    case WEST -> {
                        return EAST;
                    }
                    case DOWN -> {
                        return SOUTH;
                    }
                    case EAST -> {
                        return WEST;
                    }
                    default -> {
                        return null;
                    }
                }
            } else if (facing == Direction.WEST) {
                switch (direction) {
                    case UP -> {
                        return NORTH;
                    }
                    case NORTH -> {
                        return EAST;
                    }
                    case DOWN -> {
                        return SOUTH;
                    }
                    case SOUTH -> {
                        return WEST;
                    }
                    default -> {
                        return null;
                    }
                }
            }
        }
        switch (direction) {
            case NORTH -> {
                return NORTH;
            }
            case EAST -> {
                return EAST;
            }
            case SOUTH -> {
                return SOUTH;
            }
            case WEST -> {
                return WEST;
            }
            default -> {
                return null;
            }
        }
    }


    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var facing = state.get(FacingBlock.FACING);
        if (facing == Direction.DOWN) {
            return VoxelShapes.union(BOTTOM_MAIN, (state.get(NORTH) ? BOTTOM_NORTH : EMPTY), (state.get(EAST) ? BOTTOM_EAST : EMPTY), (state.get(SOUTH) ? BOTTOM_SOUTH : EMPTY), (state.get(WEST) ? BOTTOM_WEST : EMPTY));
        } else if (facing == Direction.UP) {
            return VoxelShapes.union(TOP_MAIN, (state.get(NORTH) ? TOP_NORTH : EMPTY), (state.get(EAST) ? TOP_EAST : EMPTY), (state.get(SOUTH) ? TOP_SOUTH : EMPTY), (state.get(WEST) ? TOP_WEST : EMPTY));
        } else if (facing == Direction.NORTH) {
            return VoxelShapes.union(NORTH_MAIN);
        } else if (facing == Direction.EAST) {
            return VoxelShapes.union(EAST_MAIN);
        } else if (facing == Direction.SOUTH) {
            return VoxelShapes.union(SOUTH_MAIN);
        } else {
            return VoxelShapes.union(WEST_MAIN);
        }
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        var facing = state.get(FacingBlock.FACING);
        if (facing == Direction.DOWN) {
            return VoxelShapes.union(BOTTOM_MAIN, (state.get(NORTH) ? BOTTOM_NORTH : EMPTY), (state.get(EAST) ? BOTTOM_EAST : EMPTY), (state.get(SOUTH) ? BOTTOM_SOUTH : EMPTY), (state.get(WEST) ? BOTTOM_WEST : EMPTY));
        } else if (facing == Direction.UP) {
            return VoxelShapes.union(TOP_MAIN, (state.get(NORTH) ? TOP_NORTH : EMPTY), (state.get(EAST) ? TOP_EAST : EMPTY), (state.get(SOUTH) ? TOP_SOUTH : EMPTY), (state.get(WEST) ? TOP_WEST : EMPTY));
        } else if (facing == Direction.NORTH) {
            return VoxelShapes.union(NORTH_MAIN);
        } else if (facing == Direction.EAST) {
            return VoxelShapes.union(EAST_MAIN);
        } else if (facing == Direction.SOUTH) {
            return VoxelShapes.union(SOUTH_MAIN);
        } else {
            return VoxelShapes.union(WEST_MAIN);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, WEST, SOUTH, FacingBlock.FACING, WATERLOGGED);
        super.appendProperties(builder);
    }

    @Override
    protected MapCodec<? extends HorizontalConnectingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}

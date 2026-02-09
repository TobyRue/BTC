package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PilasterBlock extends HorizontalConnectingBlock {
    public static final BooleanProperty OTHER_BLOCKS = BooleanProperty.of("other_blocks");
    public static final BooleanProperty CONNECTS = BooleanProperty.of("connects");

    //VALUES FROM HorizontalConnectingBlock are not just n-e-s-w, if it is vertical then it is up down left right according to how is looks like facing the tip of the pilaster
    //FACING is the direction that the back is on. UP : NORTH, RIGHT : EAST, DOWN : SOUTH, LEFT : WEST

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
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5),
            VoxelShapes.cuboid(0.25, 0.25, 0.5, 0.75, 0.75, 1)
    );
    private static final VoxelShape SOUTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.5)
    );
    private static final VoxelShape EAST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1),
            VoxelShapes.cuboid(0, 0.25, 0.25, 0.5, 0.75, 0.75)
    );
    private static final VoxelShape WEST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1),
            VoxelShapes.cuboid(0.5, 0.25, 0.25, 1, 0.75, 0.75)
    );

    private static final VoxelShape NORTH_UP = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.75, 0.5, 0.75, 1, 1)
    );
    private static final VoxelShape NORTH_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.5, 0.75, 0.25, 1)
    );
    private static final VoxelShape NORTH_LEFT = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.5, 0.25, 0.75, 1)
    );
    private static final VoxelShape NORTH_RIGHT = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0.25, 0.5, 1, 0.75, 1)
    );

    private static final VoxelShape EAST_UP = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.75, 0.25, 0.5, 1, 0.75)
    );
    private static final VoxelShape EAST_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.25, 0.5, 0.25, 0.75)
    );
    private static final VoxelShape EAST_LEFT = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0, 0.5, 0.75, 0.25)
    );
    private static final VoxelShape EAST_RIGHT = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.75, 0.5, 0.75, 1)
    );

    private static final VoxelShape SOUTH_UP = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.75, 0, 0.75, 1, 0.5)
    );
    private static final VoxelShape SOUTH_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.25, 0.5)
    );
    private static final VoxelShape SOUTH_LEFT = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0.25, 0, 1, 0.75, 0.5)
    );
    private static final VoxelShape SOUTH_RIGHT = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0, 0.25, 0.75, 0.5)
    );

    private static final VoxelShape WEST_UP = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0.75, 0.25, 1, 1, 0.75)
    );
    private static final VoxelShape WEST_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0, 0.25, 1, 0.25, 0.75)
    );
    private static final VoxelShape WEST_LEFT = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0.25, 0.75, 1, 0.75, 1)
    );
    private static final VoxelShape WEST_RIGHT = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0.25, 0, 1, 0.75, 0.25)
    );

    protected PilasterBlock(Settings settings) {
        super(0, 0, 0, 0, 0, settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(FacingBlock.FACING, Direction.DOWN).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(WATERLOGGED, false).with(OTHER_BLOCKS, true).with(CONNECTS, true));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection();

        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return this.getDefaultState()
                .with(Properties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
                .with(FacingBlock.FACING, direction)
                .with(
                        NORTH,
                        canConnect(
                                world,
                                this.getDefaultState().with(FacingBlock.FACING, direction),
                                blockPos, world.getBlockState(getDirectionalOffset(blockPos, direction, RelativePointDirection.ONE)),
                                getDirectionalOffset(blockPos, direction, RelativePointDirection.ONE),
                                Direction.SOUTH)
                )
                .with(
                        EAST,
                        canConnect(
                                world,
                                this.getDefaultState().with(FacingBlock.FACING, direction),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction, RelativePointDirection.TWO)),
                                getDirectionalOffset(blockPos, direction, RelativePointDirection.TWO),
                                Direction.WEST)
                )
                .with(
                        SOUTH,
                        canConnect(
                                world,
                                this.getDefaultState().with(FacingBlock.FACING, direction),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction, RelativePointDirection.THREE)),
                                getDirectionalOffset(blockPos, direction, RelativePointDirection.THREE),
                                Direction.NORTH
                        )
                )
                .with(
                        WEST,
                        canConnect(
                                world,
                                this.getDefaultState().with(FacingBlock.FACING, direction),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction, RelativePointDirection.FOUR)),
                                getDirectionalOffset(blockPos, direction, RelativePointDirection.FOUR),
                                Direction.EAST
                        )
                );
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

        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (getProperty(dir, facing) != null) {
            return state.with(getProperty(dir, facing), canConnect(world, state, pos, neighborState, neighborPos, dir));
        }
        return state;
    }

    public boolean canConnect(WorldAccess world, BlockState state, BlockPos pos, BlockState neighborState, BlockPos neighborPos, Direction dir) {
        var facing = state.get(FacingBlock.FACING);
        if (state.get(CONNECTS)) {
            if (facing.getAxis().isVertical()) {
                if (dir.getAxis().isHorizontal()) {
                    return neighborState.isIn(BTC.PILASTER) ||
                            neighborState.isIn(BTC.PILLAR) ||
                            neighborState.isIn(BlockTags.FENCES) ||
                            neighborState.isIn(BlockTags.WALLS) ||
                            neighborState.isIn(BTC.PANE) ||
                            neighborState.getBlock() instanceof PaneBlock ||
                            (state.get(OTHER_BLOCKS) && (neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite())));
                }
            } else {
                return neighborState.isIn(BTC.PILASTER) ||
                        neighborState.isIn(BTC.PANE) ||
                        neighborState.getBlock() instanceof PaneBlock ||
                        (state.get(OTHER_BLOCKS) && neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite()));
            }
        }
        return false;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighbors(pos, this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }
    public enum RelativePointDirection {
        ONE,
        TWO,
        THREE,
        FOUR
    }

    private static BlockPos getDirectionalOffset(BlockPos pos, Direction facing, RelativePointDirection relativeDirection) {
        switch (facing) {
            case UP, DOWN -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.north();
                    }
                    case THREE -> {
                        return pos.south();
                    }
                    case FOUR -> {
                        return pos.west();
                    }
                    case TWO -> {
                        return pos.east();
                    }
                }
            }
            case NORTH -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.up();
                    }
                    case THREE -> {
                        return pos.down();
                    }
                    case FOUR -> {
                        return pos.west();
                    }
                    case TWO -> {
                        return pos.east();
                    }
                }
            }
            case EAST -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.up();
                    }
                    case THREE -> {
                        return pos.down();
                    }
                    case FOUR -> {
                        return pos.north();
                    }
                    case TWO -> {
                        return pos.south();
                    }
                }
            }
            case SOUTH -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.up();
                    }
                    case THREE -> {
                        return pos.down();
                    }
                    case FOUR -> {
                        return pos.east();
                    }
                    case TWO -> {
                        return pos.west();
                    }
                }
            }
            case WEST -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.up();
                    }
                    case THREE -> {
                        return pos.down();
                    }
                    case FOUR -> {
                        return pos.south();
                    }
                    case TWO -> {
                        return pos.north();
                    }
                }
            }
        }
        return pos;
    }

    //FACING is the direction that the back is on. UP : NORTH, RIGHT : EAST, DOWN : SOUTH, LEFT : WEST
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
            return VoxelShapes.union(NORTH_MAIN, (state.get(NORTH) ? NORTH_UP : EMPTY), (state.get(EAST) ? NORTH_RIGHT : EMPTY), (state.get(SOUTH) ? NORTH_DOWN : EMPTY), (state.get(WEST) ? NORTH_LEFT : EMPTY));
        } else if (facing == Direction.EAST) {
            return VoxelShapes.union(EAST_MAIN, (state.get(NORTH) ? EAST_UP : EMPTY), (state.get(EAST) ? EAST_RIGHT : EMPTY), (state.get(SOUTH) ? EAST_DOWN : EMPTY), (state.get(WEST) ? EAST_LEFT : EMPTY));
        } else if (facing == Direction.SOUTH) {
            return VoxelShapes.union(SOUTH_MAIN, (state.get(NORTH) ? SOUTH_UP : EMPTY), (state.get(EAST) ? SOUTH_RIGHT : EMPTY), (state.get(SOUTH) ? SOUTH_DOWN : EMPTY), (state.get(WEST) ? SOUTH_LEFT : EMPTY));
        } else {
            return VoxelShapes.union(WEST_MAIN, (state.get(NORTH) ? WEST_UP : EMPTY), (state.get(EAST) ? WEST_RIGHT : EMPTY), (state.get(SOUTH) ? WEST_DOWN : EMPTY), (state.get(WEST) ? WEST_LEFT : EMPTY));
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
        builder.add(NORTH, EAST, WEST, SOUTH, FacingBlock.FACING, WATERLOGGED, OTHER_BLOCKS, CONNECTS);
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

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_180 ->
                    state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST)).with(FacingBlock.FACING, rotation.rotate(state.get(FacingBlock.FACING)));
            case COUNTERCLOCKWISE_90 ->
                    state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH)).with(FacingBlock.FACING, rotation.rotate(state.get(FacingBlock.FACING)));
            case CLOCKWISE_90 ->
                    state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH)).with(FacingBlock.FACING, rotation.rotate(state.get(FacingBlock.FACING)));
            default -> state;
        };
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return switch (mirror) {
            case LEFT_RIGHT -> state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH)).rotate(mirror.getRotation(state.get(FacingBlock.FACING)));
            case FRONT_BACK -> state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST)).rotate(mirror.getRotation(state.get(FacingBlock.FACING)));
            default -> super.mirror(state, mirror).rotate(mirror.getRotation(state.get(FacingBlock.FACING)));
        };
    }
}

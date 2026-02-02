package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class ColumnBlock extends HorizontalConnectingBlock {
    public static final BooleanProperty OTHER_BLOCKS = BooleanProperty.of("other_blocks");
    public static final BooleanProperty CONNECTS = BooleanProperty.of("connects");
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;


    private static final VoxelShape EMPTY = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0, 0, 0)
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
    private static final VoxelShape X_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0, 1, 0.75, 0.25)
    );
    private static final VoxelShape X_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.75, 0.25, 1, 1, 0.75)
    );
    private static final VoxelShape X_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.75, 1, 0.75, 1)
    );
    private static final VoxelShape X_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.25, 1, 0.25, 0.75)
    );
    private static final VoxelShape Y_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0, 0.75, 1, 0.25)
    );
    private static final VoxelShape Y_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0, 0.25, 1, 1, 0.75)
    );
    private static final VoxelShape Y_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.75, 0.75, 1, 1)
    );
    private static final VoxelShape Y_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.25, 0.25, 1, 0.75)
    );
    private static final VoxelShape Z_NORTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.75, 0, 0.75, 1, 1)
    );
    private static final VoxelShape Z_EAST = VoxelShapes.union(
            VoxelShapes.cuboid(0.75, 0.25, 0, 1, 0.75, 1)
    );
    private static final VoxelShape Z_SOUTH = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0, 0.75, 0.25, 1)
    );
    private static final VoxelShape Z_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0, 0.25, 0.75, 1)
    );

    public static final MapCodec<ColumnBlock> CODEC = ColumnBlock.createCodec(ColumnBlock::new);

    public ColumnBlock(Settings settings) {
        super(0, 0, 0, 0, 0, settings);
        this.setDefaultState(this.getDefaultState().with(Properties.WATERLOGGED, false).with(AXIS, Direction.Axis.Y).with(CONNECTS, true).with(OTHER_BLOCKS, true).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false));
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
                .with(AXIS, direction.getAxis())
                .with(
                        NORTH,
                        canConnect(
                                world,
                                this.getDefaultState().with(AXIS, direction.getAxis()),
                                blockPos, world.getBlockState(getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.ONE)),
                                getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.ONE),
                                Direction.SOUTH)
                )
                .with(
                        EAST,
                        canConnect(
                                world,
                                this.getDefaultState().with(AXIS, direction.getAxis()),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.TWO)),
                                getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.TWO),
                                Direction.WEST)
                )
                .with(
                        SOUTH,
                        canConnect(
                                world,
                                this.getDefaultState().with(AXIS, direction.getAxis()),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.THREE)),
                                getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.THREE),
                                Direction.NORTH
                        )
                )
                .with(
                        WEST,
                        canConnect(
                                world,
                                this.getDefaultState().with(AXIS, direction.getAxis()),
                                blockPos,
                                world.getBlockState(getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.FOUR)),
                                getDirectionalOffset(blockPos, direction.getAxis(), PilasterBlock.RelativePointDirection.FOUR),
                                Direction.EAST
                        )
                );
    }

    private static BlockPos getDirectionalOffset(BlockPos pos, Direction.Axis facing, PilasterBlock.RelativePointDirection relativeDirection) {
        switch (facing) {
            case X -> {
                switch (relativeDirection) {
                    case ONE -> {
                        return pos.north();
                    }
                    case THREE -> {
                        return pos.south();
                    }
                    case FOUR -> {
                        return pos.down();
                    }
                    case TWO -> {
                        return pos.up();
                    }
                }
            }
            case Y -> {
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
            case Z -> {
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
        }
        return pos;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.WATERLOGGED, AXIS, CONNECTS, OTHER_BLOCKS, NORTH, EAST, SOUTH, WEST);
        super.appendProperties(builder);
    }


    @Override
    protected MapCodec<? extends HorizontalConnectingBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(AXIS)) {
            case X -> {
                return VoxelShapes.union(COLUMN_EAST_WEST, (state.get(NORTH) ? X_NORTH : EMPTY), (state.get(EAST) ? X_EAST : EMPTY), (state.get(SOUTH) ? X_SOUTH : EMPTY), (state.get(WEST) ? X_WEST : EMPTY));
            }
            case Y -> {
                return VoxelShapes.union(COLUMN_UP_DOWN, (state.get(NORTH) ? Y_NORTH : EMPTY), (state.get(EAST) ? Y_EAST : EMPTY), (state.get(SOUTH) ? Y_SOUTH : EMPTY), (state.get(WEST) ? Y_WEST : EMPTY));
            }
            case Z -> {
                return VoxelShapes.union(COLUMN_NORTH_SOUTH, (state.get(NORTH) ? Z_NORTH : EMPTY), (state.get(EAST) ? Z_EAST : EMPTY), (state.get(SOUTH) ? Z_SOUTH : EMPTY), (state.get(WEST) ? Z_WEST : EMPTY));
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(AXIS)) {
            case X -> {
                return VoxelShapes.union(COLUMN_EAST_WEST, (state.get(NORTH) ? X_NORTH : EMPTY), (state.get(EAST) ? X_EAST : EMPTY), (state.get(SOUTH) ? X_SOUTH : EMPTY), (state.get(WEST) ? X_WEST : EMPTY));
            }
            case Y -> {
                return VoxelShapes.union(COLUMN_UP_DOWN, (state.get(NORTH) ? Y_NORTH : EMPTY), (state.get(EAST) ? Y_EAST : EMPTY), (state.get(SOUTH) ? Y_SOUTH : EMPTY), (state.get(WEST) ? Y_WEST : EMPTY));
            }
            case Z -> {
                return VoxelShapes.union(COLUMN_NORTH_SOUTH, (state.get(NORTH) ? Z_NORTH : EMPTY), (state.get(EAST) ? Z_EAST : EMPTY), (state.get(SOUTH) ? Z_SOUTH : EMPTY), (state.get(WEST) ? Z_WEST : EMPTY));
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
        if (state.get(Properties.WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        var axis = state.get(AXIS);
        if (getProperty(dir, axis) != null) {
            return state.with(getProperty(dir, axis), canConnect(world, state, pos, neighborState, neighborPos, dir));
        }
        return state;
    }

    public boolean canConnect(WorldAccess world, BlockState state, BlockPos pos, BlockState neighborState, BlockPos neighborPos, Direction dir) {
        if (state.get(CONNECTS)) {
            return neighborState.isIn(BTC.COLUMN) ||
                    neighborState.isIn(BTC.PILASTER) ||
                    neighborState.isIn(BlockTags.FENCES) ||
                    neighborState.isIn(BlockTags.WALLS) ||
                    neighborState.isIn(BTC.PANE) ||
                    neighborState.getBlock() instanceof PaneBlock ||
                    (state.get(OTHER_BLOCKS) && (neighborState.isSideSolidFullSquare(world, neighborPos, dir.getOpposite())));
        }
        return false;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.updateNeighbors(pos, this);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Nullable
    private static BooleanProperty getProperty(Direction direction, Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            switch (direction) {
                case NORTH -> {
                    return NORTH;
                }
                case UP -> {
                    return EAST;
                }
                case SOUTH -> {
                    return SOUTH;
                }
                case DOWN -> {
                    return WEST;
                }
                default -> {
                    return null;
                }
            }
        } else if (axis == Direction.Axis.Y) {
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
        } else if (axis == Direction.Axis.Z) {
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
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(AXIS, switch (state.get(AXIS)) {
            case X -> switch (rotation) {
                case NONE, CLOCKWISE_180 -> state.get(AXIS);
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> Direction.Axis.Z;
            };
            case Y -> switch (rotation) {
                case NONE, CLOCKWISE_180, CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.get(AXIS);
            };
            case Z -> switch (rotation) {
                case NONE, CLOCKWISE_180 -> state.get(AXIS);
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> Direction.Axis.X;
            };
        });
    }

}

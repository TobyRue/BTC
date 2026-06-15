package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class BellowBlock extends HorizontalFacingBlock {
    public static final MapCodec<BellowBlock> CODEC = createCodec(BellowBlock::new);

    // --- BASELINE DEFINITIONS (Your exact Floor-North Inputs) ---
    private static final VoxelShape V1_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.75, 1, 1, 1),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.75),
            VoxelShapes.cuboid(0.375, 0.875, 0.375, 0.625, 1, 0.625)
    );

    private static final VoxelShape V2_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.6875, 1, 1, 0.9375),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.6875),
            VoxelShapes.cuboid(0.375, 0.875, 0.3125, 0.625, 1, 0.5625)
    );

    private static final VoxelShape V3_BASE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.25),
            VoxelShapes.cuboid(0, 0, 0.625, 1, 1, 0.875),
            VoxelShapes.cuboid(0.125, 0, 0.25, 0.875, 0.875, 0.625),
            VoxelShapes.cuboid(0.375, 0.875, 0.3125, 0.625, 1, 0.5625)
    );

    // --- LEVEL 0 (V1) STATIC BOUNDS ---
    public static final VoxelShape V1_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V1_BASE);

    public static final VoxelShape V1_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V1_BASE);

    public static final VoxelShape V1_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V1_BASE);
    public static final VoxelShape V1_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V1_BASE);
    public static final VoxelShape V1_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V1_BASE);
    public static final VoxelShape V1_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V1_BASE);

    // --- LEVEL 1 (V2) STATIC BOUNDS ---
    public static final VoxelShape V2_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V2_BASE);

    public static final VoxelShape V2_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V2_BASE);

    public static final VoxelShape V2_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V2_BASE);
    public static final VoxelShape V2_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V2_BASE);
    public static final VoxelShape V2_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V2_BASE);
    public static final VoxelShape V2_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V2_BASE);

    // --- LEVEL 2 (V3) STATIC BOUNDS ---
    public static final VoxelShape V3_FLOOR_NORTH = rotateShape(BlockFace.FLOOR, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_FLOOR_SOUTH = rotateShape(BlockFace.FLOOR, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_FLOOR_EAST  = rotateShape(BlockFace.FLOOR, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_FLOOR_WEST  = rotateShape(BlockFace.FLOOR, Direction.WEST,  V3_BASE);

    public static final VoxelShape V3_CEILING_NORTH = rotateShape(BlockFace.CEILING, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_CEILING_SOUTH = rotateShape(BlockFace.CEILING, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_CEILING_EAST  = rotateShape(BlockFace.CEILING, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_CEILING_WEST  = rotateShape(BlockFace.CEILING, Direction.WEST,  V3_BASE);

    public static final VoxelShape V3_WALL_NORTH = rotateShape(BlockFace.WALL, Direction.NORTH, V3_BASE);
    public static final VoxelShape V3_WALL_SOUTH = rotateShape(BlockFace.WALL, Direction.SOUTH, V3_BASE);
    public static final VoxelShape V3_WALL_EAST  = rotateShape(BlockFace.WALL, Direction.EAST,  V3_BASE);
    public static final VoxelShape V3_WALL_WEST  = rotateShape(BlockFace.WALL, Direction.WEST,  V3_BASE);


    private static VoxelShape rotateShape(BlockFace face, Direction facing, VoxelShape baseFloorNorth) {
        VoxelShape[] buffer = new VoxelShape[]{baseFloorNorth};

        if (face == BlockFace.CEILING) {
            VoxelShape current = VoxelShapes.empty();
            for (net.minecraft.util.math.Box box : buffer[0].getBoundingBoxes()) {
                current = VoxelShapes.union(current, VoxelShapes.cuboid(
                        box.minX, 1.0 - box.maxY, box.minZ,
                        box.maxX, 1.0 - box.minY, box.maxZ
                ));
            }
            buffer[0] = current;
        } else if (face == BlockFace.WALL) {
            VoxelShape current = VoxelShapes.empty();
            for (net.minecraft.util.math.Box box : buffer[0].getBoundingBoxes()) {
                current = VoxelShapes.union(current, VoxelShapes.cuboid(
                        box.minX, box.minZ, 1.0 - box.maxY,
                        box.maxX, box.maxZ, 1.0 - box.minY
                ));
            }
            buffer[0] = current;
        }

        int rotationSteps = switch (facing) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };

        for (int i = 0; i < rotationSteps; i++) {
            VoxelShape rotated = VoxelShapes.empty();
            for (net.minecraft.util.math.Box box : buffer[0].getBoundingBoxes()) {
                rotated = VoxelShapes.union(rotated, VoxelShapes.cuboid(
                        1.0 - box.maxZ, box.minY, box.minX,
                        1.0 - box.minZ, box.maxY, box.maxX
                ));
            }
            buffer[0] = rotated;
        }

        return buffer[0];
    }

    public static final EnumProperty<BlockFace> FACE = Properties.BLOCK_FACE;
    public static final IntProperty LEVEL = IntProperty.of("level", 0, 2);


    protected BellowBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LEVEL, 0).with(FACE, BlockFace.WALL));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACE, FACING, LEVEL);
    }

    private boolean isReceivingAnyPower(BlockState state, World world, BlockPos pos) {
        if (world.isReceivingRedstonePower(pos)) {
            return true;
        }
        return IDungeonWire.isReceivingDungeonWirePower(state, world, pos, getDirections(state).stream());
    }


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        this.checkAndScheduleTransition(state, world, pos);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (world.isClient || state.isOf(oldState.getBlock())) return;
        this.checkAndScheduleTransition(state, world, pos);
    }

    private void checkAndScheduleTransition(BlockState state, World world, BlockPos pos) {
        boolean receivingPower = isReceivingAnyPower(state, world, pos);
        int currentLevel = state.get(LEVEL);

        if ((receivingPower && currentLevel < 2) || (!receivingPower && currentLevel > 0)) {
            if (!world.getBlockTickScheduler().isQueued(pos, this)) {
                world.scheduleBlockTick(pos, this, 2);
            }
        }
    }


    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean receivingPower = isReceivingAnyPower(state, world, pos);
        int currentLevel = state.get(LEVEL);
        int nextLevel = currentLevel;

        if (receivingPower) {
            if (currentLevel < 2) {
                nextLevel = currentLevel + 1;
            }
        } else {
            if (currentLevel > 0) {
                nextLevel = currentLevel - 1;
            }
        }

        if (nextLevel != currentLevel) {
            state = state.with(LEVEL, nextLevel);
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }

        if ((receivingPower && nextLevel < 2) || (!receivingPower && nextLevel > 0)) {
            world.scheduleBlockTick(pos, this, 2);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        int level = state.get(LEVEL);
        BlockFace face = state.get(FACE);
        Direction facing = state.get(FACING);

        return switch (level) {
            case 0 -> switch (face) {
                case FLOOR -> switch (facing) {
                    case SOUTH -> V1_FLOOR_SOUTH;
                    case EAST -> V1_FLOOR_EAST;
                    case WEST -> V1_FLOOR_WEST;
                    default -> V1_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V1_CEILING_SOUTH;
                    case EAST -> V1_CEILING_EAST;
                    case WEST -> V1_CEILING_WEST;
                    default -> V1_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V1_WALL_SOUTH;
                    case EAST -> V1_WALL_EAST;
                    case WEST -> V1_WALL_WEST;
                    default -> V1_WALL_NORTH;
                };
            };
            case 1 -> switch (face) {
                case FLOOR -> switch (facing) {
                    case SOUTH -> V2_FLOOR_SOUTH;
                    case EAST -> V2_FLOOR_EAST;
                    case WEST -> V2_FLOOR_WEST;
                    default -> V2_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V2_CEILING_SOUTH;
                    case EAST -> V2_CEILING_EAST;
                    case WEST -> V2_CEILING_WEST;
                    default -> V2_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V2_WALL_SOUTH;
                    case EAST -> V2_WALL_EAST;
                    case WEST -> V2_WALL_WEST;
                    default -> V2_WALL_NORTH;
                };
            };
            default -> switch (face) { // level == 2
                case FLOOR -> switch (facing) {
                    case SOUTH -> V3_FLOOR_SOUTH;
                    case EAST -> V3_FLOOR_EAST;
                    case WEST -> V3_FLOOR_WEST;
                    default -> V3_FLOOR_NORTH;
                };
                case CEILING -> switch (facing) {
                    case SOUTH -> V3_CEILING_SOUTH;
                    case EAST -> V3_CEILING_EAST;
                    case WEST -> V3_CEILING_WEST;
                    default -> V3_CEILING_NORTH;
                };
                case WALL -> switch (facing) {
                    case SOUTH -> V3_WALL_SOUTH;
                    case EAST -> V3_WALL_EAST;
                    case WEST -> V3_WALL_WEST;
                    default -> V3_WALL_NORTH;
                };
            };
        };
    }

    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction[] var2 = ctx.getPlacementDirections();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Direction direction = var2[var4];
            BlockState blockState;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockState = this.getDefaultState().with(FACE, direction == Direction.UP ? BlockFace.CEILING : BlockFace.FLOOR).with(FACING, ctx.getHorizontalPlayerFacing());
            } else {
                blockState = this.getDefaultState().with(FACE, BlockFace.WALL).with(FACING, direction.getOpposite());
            }

            if (blockState.canPlaceAt(ctx.getWorld(), ctx.getBlockPos())) {
                return blockState;
            }
        }

        return null;
    }

    protected static Direction getDirection(BlockState state) {
        return switch (state.get(FACE)) {
            case CEILING -> Direction.UP;
            case FLOOR -> Direction.DOWN;
            default -> state.get(FACING).getOpposite();
        };
    }
    protected static List<Direction> getDirections(BlockState state) {
        List<Direction> dirs = new ArrayList<>();

        dirs.add(switch (state.get(FACE)) {
            case CEILING -> Direction.UP;
            case FLOOR -> Direction.DOWN;
            default -> state.get(FACING).getOpposite();
        });
        dirs.add(switch (state.get(FACE)) {
            case WALL -> Direction.DOWN;
            case FLOOR -> state.get(FACING);
            case CEILING -> state.get(FACING).getOpposite();
        });
        return dirs;
    }


    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }
}

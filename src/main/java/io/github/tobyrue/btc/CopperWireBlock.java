package io.github.tobyrue.btc;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static io.github.tobyrue.btc.DungeonWireBlock.*;

public class CopperWireBlock extends Block {
    public static final MapCodec<CopperWireBlock> CODEC = createCodec(CopperWireBlock::new);

    public static final DirectionProperty FACING = Properties.FACING;

    public static final BooleanProperty FACING_UP = BooleanProperty.of("up");
    public static final BooleanProperty FACING_DOWN = BooleanProperty.of("down");
    public static final BooleanProperty FACING_LEFT = BooleanProperty.of("left");
    public static final BooleanProperty FACING_RIGHT = BooleanProperty.of("right");

    public static final BooleanProperty ROOT1 = BooleanProperty.of("root");
    public static final EnumProperty<Connection> CONNECTION1 = EnumProperty.of("connection", Connection.class);

    public static final BooleanProperty POWERED1 = BooleanProperty.of("powered");

    public static final BooleanProperty POWERABLE_BY_REDSTONE = BooleanProperty.of("powerable_by_redstone");

    public CopperWireBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ROOT1, false)
                .with(POWERABLE_BY_REDSTONE, true)
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
                .with(CONNECTION1, Connection.NONE)
                .with(POWERED1, false)
        );
    }

    @Override
    public MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();

        BlockState placementState = this.getDefaultState()
                .with(FACING, ctx.getSide());

        placementState = updateFacingState(placementState, world, blockPos);
        Connection parent = findConnectionParent(placementState, world, blockPos);
        placementState = placementState.with(CONNECTION1, parent);
        placementState = updatePowered(placementState, world, blockPos);

        return placementState;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                CONNECTION1,
                FACING_DOWN,
                FACING_LEFT,
                FACING_RIGHT,
                FACING_UP,
                FACING,
                POWERED1,
                POWERABLE_BY_REDSTONE,
                ROOT1
        );
    }

    private BlockState updateFacingState(BlockState blockState, World world, BlockPos blockPos) {
        boolean up = world.getBlockState(blockPos.offset(Direction.UP)).isOf(this) || world.getBlockState(blockPos.offset(Direction.UP)).getBlock() instanceof DungeonWireBlock;
        boolean down = world.getBlockState(blockPos.offset(Direction.DOWN)).isOf(this) || world.getBlockState(blockPos.offset(Direction.DOWN)).getBlock() instanceof DungeonWireBlock;
        boolean north = world.getBlockState(blockPos.offset(Direction.NORTH)).isOf(this) || world.getBlockState(blockPos.offset(Direction.NORTH)).getBlock() instanceof DungeonWireBlock;
        boolean east = world.getBlockState(blockPos.offset(Direction.EAST)).isOf(this) || world.getBlockState(blockPos.offset(Direction.EAST)).getBlock() instanceof DungeonWireBlock;
        boolean south = world.getBlockState(blockPos.offset(Direction.SOUTH)).isOf(this) || world.getBlockState(blockPos.offset(Direction.SOUTH)).getBlock() instanceof DungeonWireBlock;
        boolean west = world.getBlockState(blockPos.offset(Direction.WEST)).isOf(this) || world.getBlockState(blockPos.offset(Direction.WEST)).getBlock() instanceof DungeonWireBlock;

        if(!up && !down && !north && !east && !south && !west) {
            return blockState;
        }

        Direction facing = blockState.get(FACING);

        if (facing == Direction.UP) {
            return blockState
                    .with(FACING_UP, south)
                    .with(FACING_DOWN, north)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.DOWN) {
            return blockState
                    .with(FACING_UP, north)
                    .with(FACING_DOWN, south)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.NORTH) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if (facing == Direction.EAST) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, south)
                    .with(FACING_RIGHT, north);
        } else if(facing == Direction.SOUTH) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, west)
                    .with(FACING_RIGHT, east);
        } else {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, north)
                    .with(FACING_RIGHT, south);
        }
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     * @remarks Only finds blocks of the same class.
     */
    private Connection findConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        Connection poweredTarget = Connection.NONE;
        Connection unpoweredTarget = Connection.NONE;

        for(Direction direction: Direction.values()) {
            BlockState other = world.getBlockState(blockPos.offset(direction));
            if(!(other.isOf(this) || other.getBlock() instanceof DungeonWireBlock)) {
                continue;
            }

            if(other.contains(ROOT) && other.get(ROOT)) {
                return Connection.of(direction);
            } else if(other.contains(ROOT1) && other.get(ROOT1)) {
                return Connection.of(direction);
            }


            if(other.contains(CONNECTION) && other.contains(POWERED)) {
                Connection parent = other.get(CONNECTION);
                if(parent != Connection.NONE) {
                    if((parent.asDirection().getOpposite() == direction)) {
                        continue;
                    }

                    if(other.get(POWERED)) {
                        if(poweredTarget == Connection.NONE) {
                            poweredTarget = Connection.of(direction);
                        }
                    } else {
                        if(unpoweredTarget == Connection.NONE) {
                            unpoweredTarget = Connection.of(direction);
                        }
                    }
                }
            } else if(other.contains(CONNECTION1) && other.contains(POWERED1)) {
                Connection parent1 = other.get(CONNECTION1);
                if(parent1 != Connection.NONE) {
                    if((parent1.asDirection().getOpposite() == direction)) {
                        continue;
                    }
                    if(other.get(POWERED1)) {
                        if (poweredTarget == Connection.NONE) {
                            poweredTarget = Connection.of(direction);
                        }
                    } else {
                        if(unpoweredTarget == Connection.NONE) {
                            unpoweredTarget = Connection.of(direction);
                        }
                    }
                }
            }
        }
        if(poweredTarget != Connection.NONE) {
            return poweredTarget;
        }
        if(unpoweredTarget != Connection.NONE) {
            return unpoweredTarget;
        }
        return Connection.NONE;
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private boolean isValidConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        Connection parent = blockState.get(CONNECTION1);
        if(parent != Connection.NONE) {
            BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
            if(other.isOf(this) && other.get(CONNECTION1) != Connection.NONE) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updateConnectionParent(BlockState blockState, World world, BlockPos blockPos) {
        if(isValidConnectionParent(blockState, world, blockPos)) {
            return blockState;
        }
        Connection parent = findConnectionParent(blockState, world, blockPos);
        return blockState.with(CONNECTION1, parent);
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updatePowered(BlockState blockState, World world, BlockPos blockPos) {
        if(blockState.get(ROOT1)) {
            return blockState.with(POWERED1, true);
        }

        if(blockState.get(CONNECTION1) == Connection.NONE) {
            return blockState.with(POWERED1, false);
        }
        Connection parent = blockState.get(CONNECTION1);
        BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
        Connection parent1 = blockState.get(CONNECTION1);
        BlockState other1 = world.getBlockState(blockPos.offset(parent1.asDirection()));

        if(other.getBlock() instanceof DungeonWireBlock && other.get(POWERED)) {
            return blockState.with(POWERED1, true);
        }

        if(other1.isOf(this) && other.contains(POWERED1)) {
            return blockState.with(POWERED1, true);
        }
        return blockState.with(POWERED1, false);
    }


    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(blockState, world, blockPos, sourceBlock, sourcePos, notify);

        if(!world.isClient) {
            BlockState newState = blockState;
            newState = updateFacingState(newState, world, blockPos);
            newState = updateConnectionParent(newState, world, blockPos);
            newState = updatePowered(newState, world, blockPos);

            if(!blockState.equals(newState)) {
                world.setBlockState(blockPos, newState, (NOTIFY_NEIGHBORS | NOTIFY_LISTENERS));
            }
            boolean bl = (Boolean)blockState.get(ROOT1);
            if(blockState.get(POWERABLE_BY_REDSTONE)) {
                if (bl != world.isReceivingRedstonePower(blockPos)) {
                    if (bl) {
                        world.scheduleBlockTick(blockPos, this, 4);
                    } else {
                        world.setBlockState(blockPos, (BlockState) blockState.cycle(ROOT1), 2);
                    }
                }
            }
        }
    }
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Boolean)state.get(ROOT1) && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, (BlockState)state.cycle(ROOT1), 2);
        }
    }
    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWERED1) ? 15 : 0;
    }

    /**
     * Returns whether the block is capable of emitting a redstone signal.
     */
    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

}

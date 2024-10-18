package io.github.tobyrue.btc;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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

    public CopperWireBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
                .with(ROOT1, false)
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

        if (!up && !down && !north && !east && !south && !west) {
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
        } else if (facing == Direction.SOUTH) {
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
    private Connection findConnectionParent(BlockState blockState, World world, BlockPos blockPos)
    {
        Connection poweredTarget = Connection.NONE;
        Connection unpoweredTarget = Connection.NONE;

        for (Direction direction: Direction.values())
        {
            BlockState other = world.getBlockState(blockPos.offset(direction));
            if (!other.isOf(this))
            {
                continue;
            }

            if (other.get(ROOT1))
            {
                return Connection.of(direction);
            }

            Connection parent = other.get(CONNECTION1);
            if (parent != Connection.NONE)
            {
                if (parent.asDirection().getOpposite() == direction)
                {
                    continue;
                }

                if (other.get(POWERED1))
                {
                    if (poweredTarget == Connection.NONE)
                    {
                        poweredTarget = Connection.of(direction);
                    }
                }
                else
                {
                    if (unpoweredTarget == Connection.NONE)
                    {
                        unpoweredTarget = Connection.of(direction);
                    }
                }
            }
        }

        if (poweredTarget != Connection.NONE)
        {
            return poweredTarget;
        }

        if (unpoweredTarget != Connection.NONE)
        {
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
    private boolean isValidConnectionParent(BlockState blockState, World world, BlockPos blockPos)
    {
        Connection parent = blockState.get(CONNECTION1);
        if (parent != Connection.NONE)
        {
            BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
            if (other.isOf(this) && other.get(CONNECTION1) != Connection.NONE)
            {
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
    private BlockState updateConnectionParent(BlockState blockState, World world, BlockPos blockPos)
    {
        if (isValidConnectionParent(blockState, world, blockPos))
        {
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
    private BlockState updatePowered(BlockState blockState, World world, BlockPos blockPos)
    {
        if (blockState.get(ROOT1))
        {
            return blockState.with(POWERED1, true);
        }

        if (blockState.get(CONNECTION1) == Connection.NONE)
        {
            return blockState.with(POWERED1, false);
        }

        Connection parent = blockState.get(CONNECTION1);
        BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
        if (other.isOf(this) && other.get(POWERED1))
        {
            return blockState.with(POWERED1, true);
        }

        return blockState.with(POWERED1, false);
    }


    @Override
    public void neighborUpdate(BlockState blockState, World world, BlockPos blockPos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(blockState, world, blockPos, sourceBlock, sourcePos, notify);

        if (!world.isClient) {
            BlockState newState = blockState;
            newState = updateFacingState(newState, world, blockPos);
            newState = updateConnectionParent(newState, world, blockPos);
            newState = updatePowered(newState, world, blockPos);

            if (!blockState.equals(newState)) {
                world.setBlockState(blockPos, newState, (NOTIFY_NEIGHBORS | NOTIFY_LISTENERS));
            }
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
}

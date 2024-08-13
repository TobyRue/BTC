
package io.github.tobyrue.btc;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DungeonWireBlock extends Block {
    public static final EnumProperty<RootWhere> ROOT_WHERE = EnumProperty.of("root_where", RootWhere.class);
    public static final MapCodec<DungeonWireBlock> CODEC = createCodec(DungeonWireBlock::new);
    public static final BooleanProperty FACING_DOWN = BooleanProperty.of("facing_down");
    public static final BooleanProperty FACING_UP = BooleanProperty.of("facing_up");
    public static final BooleanProperty FACING_LEFT = BooleanProperty.of("facing_left");
    public static final BooleanProperty FACING_RIGHT = BooleanProperty.of("facing_right");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final BooleanProperty ROOT = BooleanProperty.of("root");
    public static final BooleanProperty MAIN = BooleanProperty.of("main");
    public static DirectionProperty FACING;

    public DungeonWireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
                .with(ROOT, false)
                .with(MAIN, false)
                .with(ROOT_WHERE, RootWhere.NONE)
                .with(POWERED, false));
    }

    public MapCodec<DungeonWireBlock> getCodec() {
        return CODEC;
    }



    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_LEFT, false)
                .with(FACING_RIGHT, false)
                .with(ROOT, false)
                .with(MAIN, false)
                .with(ROOT_WHERE, RootWhere.NONE)
                .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()))
                .with(FACING, ctx.getSide().getOpposite().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING_DOWN, FACING_UP, FACING_LEFT, FACING_RIGHT, ROOT, MAIN, FACING, ROOT_WHERE, POWERED);
    }

    static {
        FACING = Properties.FACING;
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updateStateBasedOnNeighbors(state, world, pos);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        updateStateBasedOnNeighbors(state, world, pos);
    }

    private void updateStateBasedOnNeighbors(BlockState state, World world, BlockPos pos) {
        boolean facingDown = false;
        boolean facingUp = false;
        boolean facingLeft = false;
        boolean facingRight = false;
        boolean powered = false;
        RootWhere rootWhere = RootWhere.NONE; // Default or initial value


        Direction[] customOrder = {Direction.UP, Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST};



        for (Direction direction : customOrder) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (direction == Direction.DOWN || direction == Direction.NORTH || direction == Direction.WEST) {
                if(neighborState.getBlock()  instanceof DungeonWireBlock) {
                    if (neighborState.get(POWERED)) {
                        powered = true;
                        break;
                    } else {
                        powered = false;
                    }
                    if (neighborState.get(ROOT)) {
                        powered = true;
                    }
                }
            }
        }
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (direction == Direction.UP || direction == Direction.SOUTH || direction == Direction.EAST) {
                if (neighborState.getBlock() instanceof DungeonWireBlock && !state.get(MAIN)) {

                    if (neighborState.get(POWERED)) {
                        powered = true;
                        break;
                    } else {
                        powered = false;
                    }
                    if (neighborState.get(ROOT)) {
                        powered = true;
                    }
                }
            }
        }


        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if(neighborState.getBlock()  instanceof DungeonWireBlock) {
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.UP) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.UP;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.UP) {
                    if (direction == Direction.UP) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.UP;
                        }
                    }
                }
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.DOWN) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.DOWN;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.DOWN) {
                    if (direction == Direction.DOWN) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.DOWN;
                        }
                    }
                }
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.NORTH) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.NORTH;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.NORTH) {
                    if (direction == Direction.NORTH) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.NORTH;
                        }
                    }
                }
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.EAST) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.EAST;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.EAST) {
                    if (direction == Direction.EAST) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.EAST;
                        }
                    }
                }
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.SOUTH) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.SOUTH;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.SOUTH) {
                    if (direction == Direction.SOUTH) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.SOUTH;
                        }
                    }
                }
                if (neighborState.get(ROOT)) {
                    if (direction == Direction.WEST) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.WEST;
                        }
                    }
                }
                if (neighborState.get(ROOT_WHERE) == RootWhere.WEST) {
                    if (direction == Direction.WEST) {
                        if (state.get(MAIN)) {
                            rootWhere = RootWhere.WEST;
                        }
                    }
                }
            }
            if ((neighborState.getBlock() instanceof DungeonWireBlock)) {
                if (direction == Direction.UP) {
                    if (state.get(ROOT_WHERE) == RootWhere.UP) {
                        powered = true;
                    }
                }
                if (direction == Direction.DOWN) {
                    if (state.get(ROOT_WHERE) == RootWhere.DOWN) {
                        powered = true;
                    }
                }
                if (direction == Direction.NORTH) {
                    if (state.get(ROOT_WHERE) == RootWhere.NORTH) {
                        powered = true;
                    }
                }
                if (direction == Direction.EAST) {
                    if (state.get(ROOT_WHERE) == RootWhere.EAST) {
                        powered = true;
                    }
                }
                if (direction == Direction.SOUTH) {
                    if (state.get(ROOT_WHERE) == RootWhere.SOUTH) {
                        powered = true;
                    }
                }
                if (direction == Direction.WEST) {
                    if (state.get(ROOT_WHERE) == RootWhere.WEST) {
                        powered = true;
                    }
                }
            }
            if (!(neighborState.getBlock() instanceof DungeonWireBlock)) {
                if(direction == Direction.UP) {
                    if(state.get(ROOT_WHERE) == RootWhere.UP){
                        powered = false;
                    }
                }
                if(direction == Direction.DOWN) {
                    if(state.get(ROOT_WHERE) == RootWhere.DOWN){
                        powered = false;
                    }
                }
                if(direction == Direction.NORTH) {
                    if(state.get(ROOT_WHERE) == RootWhere.NORTH){
                        powered = false;
                    }
                }
                if(direction == Direction.EAST) {
                    if(state.get(ROOT_WHERE) == RootWhere.EAST){
                        powered = false;
                    }
                }
                if(direction == Direction.SOUTH) {
                    if(state.get(ROOT_WHERE) == RootWhere.SOUTH){
                        powered = false;
                    }
                }
                if(direction == Direction.WEST) {
                    if(state.get(ROOT_WHERE) == RootWhere.WEST){
                        powered = false;
                    }
                }
            }
            if (state.get(FACING) == Direction.UP){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case SOUTH:
                            facingUp = true;
                            break;
                        case NORTH:
                            facingDown = true;
                            break;
                        case EAST:
                            facingLeft = true;
                            break;
                        case WEST:
                            facingRight = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
            if (state.get(FACING) == Direction.DOWN){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case SOUTH:
                            facingDown = true;
                            break;
                        case NORTH:
                            facingUp = true;
                            break;
                        case EAST:
                            facingLeft = true;
                            break;
                        case WEST:
                            facingRight = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
            if (state.get(FACING) == Direction.NORTH){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case DOWN:
                            facingDown = true;
                            break;
                        case UP:
                            facingUp = true;
                            break;
                        case EAST:
                            facingLeft = true;
                            break;
                        case WEST:
                            facingRight = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
            if (state.get(FACING) == Direction.WEST){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case DOWN:
                            facingDown = true;
                            break;
                        case UP:
                            facingUp = true;
                            break;
                        case SOUTH:
                            facingRight = true;
                            break;
                        case NORTH:
                            facingLeft = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
            if (state.get(FACING) == Direction.SOUTH){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case DOWN:
                            facingDown = true;
                            break;
                        case UP:
                            facingUp = true;
                            break;
                        case EAST:
                            facingRight = true;
                            break;
                        case WEST:
                            facingLeft = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
            if (state.get(FACING) == Direction.EAST){
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    switch (direction) {
                        case DOWN:
                            facingDown = true;
                            break;
                        case UP:
                            facingUp = true;
                            break;
                        case SOUTH:
                            facingLeft = true;
                            break;
                        case NORTH:
                            facingRight = true;
                            break;
                        // Add more cases as needed
                    }
                }
            }
        }

        BlockState newState = state
                .with(FACING_DOWN, facingDown)
                .with(FACING_UP, facingUp)
                .with(FACING_LEFT, facingLeft)
                .with(FACING_RIGHT, facingRight)
                .with(ROOT_WHERE, rootWhere)
                .with(POWERED, powered);
        //System.out.println("Updating state of block at " + pos + " to " + newState);
        world.setBlockState(pos, newState, NOTIFY_ALL_AND_REDRAW);
    }
}
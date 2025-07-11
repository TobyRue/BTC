
package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.ICopperWireConnect;
import io.github.tobyrue.btc.wires.IDungeonWireConstantAction;
import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.enums.Connection;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

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

import static io.github.tobyrue.btc.block.CopperWireBlock.*;

public class DungeonWireBlock extends Block implements IDungeonWireConnect, ICopperWireConnect {
    public static final MapCodec<DungeonWireBlock> CODEC = createCodec(DungeonWireBlock::new);

    public static final DirectionProperty FACING = Properties.FACING;

    public static final BooleanProperty FACING_UP = BooleanProperty.of("up");
    public static final BooleanProperty FACING_DOWN = BooleanProperty.of("down");
    public static final BooleanProperty FACING_LEFT = BooleanProperty.of("left");
    public static final BooleanProperty FACING_RIGHT = BooleanProperty.of("right");

    public static final BooleanProperty ROOT = BooleanProperty.of("root");
    public static final EnumProperty<Connection> CONNECTION = EnumProperty.of("connection", Connection.class);

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public DungeonWireBlock(Settings settings) {
        super(settings);

        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
                .with(ROOT, false)
                .with(CONNECTION, Connection.NONE)
                .with(POWERED, false)
        );
    }

    @Override
    public MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    /**
     * Get the initial block state of the block when first placed.
     * @brief ctx The placement context.
     * @return The block state for the block to be placed.
     */
    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();

        BlockState placementState = this.getDefaultState()
                .with(FACING, ctx.getSide());

        placementState = updateFacingState(placementState, world, blockPos);

        Connection parent = findConnectionParent(placementState, world, blockPos);
        placementState = placementState.with(CONNECTION, parent);

        placementState = updatePowered(placementState, world, blockPos);

        return placementState;
    }

    /**
     * Adds all the properties of the block.
     * @builder The block state builder to add the properties to.
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(
                CONNECTION,
                FACING_DOWN,
                FACING_LEFT,
                FACING_RIGHT,
                FACING_UP,
                FACING,
                POWERED,
                ROOT
        );
    }
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate((Direction)state.get(FACING)));
    }
    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updateFacingState(BlockState blockState, World world, BlockPos blockPos) {
        BlockState upState = world.getBlockState(blockPos.offset(Direction.UP));
        BlockState downState = world.getBlockState(blockPos.offset(Direction.DOWN));
        BlockState northState = world.getBlockState(blockPos.offset(Direction.NORTH));
        BlockState eastState = world.getBlockState(blockPos.offset(Direction.EAST));
        BlockState southState = world.getBlockState(blockPos.offset(Direction.SOUTH));
        BlockState westState = world.getBlockState(blockPos.offset(Direction.WEST));
        boolean up = world.getBlockState(blockPos.offset(Direction.UP)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(upState, world, blockPos);
        boolean down = world.getBlockState(blockPos.offset(Direction.DOWN)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(downState, world, blockPos);
        boolean north = world.getBlockState(blockPos.offset(Direction.NORTH)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(northState, world, blockPos);
        boolean east = world.getBlockState(blockPos.offset(Direction.EAST)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(eastState, world, blockPos);
        boolean south = world.getBlockState(blockPos.offset(Direction.SOUTH)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(southState, world, blockPos);
        boolean west = world.getBlockState(blockPos.offset(Direction.WEST)).getBlock() instanceof IDungeonWireConnect connects && connects.shouldConnect(westState, world, blockPos);

        if(!up && !down && !north && !east && !south && !west) {
            return blockState;
        }

        Direction facing = blockState.get(FACING);

        if(facing == Direction.UP) {
            return blockState
                    .with(FACING_UP, south)
                    .with(FACING_DOWN, north)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        }
        else if(facing == Direction.DOWN) {
            return blockState
                    .with(FACING_UP, north)
                    .with(FACING_DOWN, south)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        }
        else if(facing == Direction.NORTH) {
            return blockState
                    .with(FACING_UP, up)
                    .with(FACING_DOWN, down)
                    .with(FACING_LEFT, east)
                    .with(FACING_RIGHT, west);
        } else if(facing == Direction.EAST) {
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
            assert(facing == Direction.WEST);
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

        for (Direction direction: Direction.values()) {
            BlockState other = world.getBlockState(blockPos.offset(direction));

            if(!(other.isOf(this) || other.getBlock() instanceof CopperWireBlock)) {
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
                        if(poweredTarget == Connection.NONE) {
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
        Connection parent = blockState.get(CONNECTION);
        if(parent != Connection.NONE) {
            BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
            if(other.isOf(this) && other.get(CONNECTION) != Connection.NONE) {
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
        return blockState.with(CONNECTION, parent);
    }

    /**
     *
     * @param blockState
     * @param world
     * @param blockPos
     * @return
     */
    private BlockState updatePowered(BlockState blockState, World world, BlockPos blockPos) {
        if(blockState.get(ROOT)) {
            return blockState.with(POWERED, true);
        }

        if(blockState.get(CONNECTION) == Connection.NONE) {
            return blockState.with(POWERED, false);
        }
        Connection parent = blockState.get(CONNECTION);
        BlockState other = world.getBlockState(blockPos.offset(parent.asDirection()));
        Connection parent1 = blockState.get(CONNECTION);
        BlockState other1 = world.getBlockState(blockPos.offset(parent1.asDirection()));

        if(other.getBlock() instanceof CopperWireBlock && other.get(POWERED1)) {
            return blockState.with(POWERED, true);
        }

        if(other1.isOf(this) && other.contains(POWERED)) {
            return blockState.with(POWERED, true);
        }
        return blockState.with(POWERED, false);
    }

    /**
     * Called when a neighbor update notify has been received.
     * @param blockState The block state of this block.
     * @param world The world the block is located in.
     * @param blockPos The position of the block inside the world.
     * @param sourceBlock The block that send the neighbor update notify.
     * @param sourcePos The position of the block that send the notify.
     * @param notify
     * @remarks onStateReplaced -> neighborUpdate
     * @remarks getPlacementState -> neighborUpdate -> onPlaced
     */
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
        }
        BlockState other = world.getBlockState(blockPos);
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = blockPos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof IDungeonWireConstantAction action) {
                action.onDungeonWireChange(neighborState, world, neighborPos, other.get(POWERED));
            }
        }
//        for (Direction direction : Direction.values()) {
//            BlockPos neighborPos = blockPos.offset(direction);
//            BlockState neighborState = world.getBlockState(neighborPos);
//            System.out.println("Direction: " + direction + ", pos:" + neighborPos);
//            if(neighborState.getBlock() instanceof IDungeonWireConstantAction action) {
//                action.onDungeonWireChange(neighborState, world, neighborPos, neighborState, other.get(POWERED));
//            }
//        }

    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if (!world.isClient && state.getBlock() instanceof DungeonWireBlock && state.getBlock() != newState.getBlock()) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof IDungeonWireConstantAction action) {
                    // notify the neighbor block somehow or handle logic there
                    action.onDungeonWireChange(neighborState, world, neighborPos, false);
                }
            }
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() == ModItems.CREATIVE_WRENCH && player.isCreative()) {
            boolean currentState = state.get(ROOT);
            BlockState newState = state.with(ROOT, !state.get(ROOT));
            String message = "Root has been: " + (!currentState ? "enabled" : "disabled");
            player.sendMessage(Text.literal(message), true);
            world.setBlockState(pos, newState, Block.NOTIFY_NEIGHBORS | Block.NOTIFY_LISTENERS);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if(state.get(POWERED)) {
            return 15;
        }
        return 0;
    }
    public static int getLuminance(BlockState currentBlockState) {
        // Get the value of the "activated" property.
        boolean activated = currentBlockState.get(DungeonWireBlock.POWERED);

        // Return a light level if activated = true
        return activated ? 15 : 0;
    }


    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean shouldCopperConnect(BlockState state, World world, BlockPos pos) {
        return true;
    }
}
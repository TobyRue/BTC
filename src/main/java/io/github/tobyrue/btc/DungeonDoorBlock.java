package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.github.tobyrue.btc.DungeonWireBlock.POWERED;

public class DungeonDoorBlock extends Block {
    public static final BooleanProperty NORMAL = BooleanProperty.of("normal");
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    // Define the 4x4x4 cube shape.
    private static final VoxelShape CUBE_SHAPE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    // Define the full block shape.
    private static final VoxelShape FULL_BLOCK_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    boolean open = false;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DungeonDoorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
                .with(NORMAL, true));
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OPEN, false)
                .with(NORMAL, true);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
        builder.add(NORMAL);
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updateStateBasedOnNeighbors(state, world, pos);
    }

    @Override
    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(NORMAL) && !state.get(OPEN)) {
            BlockState newState = state.with(OPEN, true);
            world.setBlockState(pos, newState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }


//    @Override
//    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
//        if (state.get(OPEN)) {
//            world.setBlockState(pos, state.with(OPEN, false), NOTIFY_ALL_AND_REDRAW);
//        }
//    }
//    public ActionResult onUse(ItemStack stack, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        BlockState state = world.getBlockState(pos);
//
//        if (state.get(NORMAL)) {
//            boolean isOpen = state.get(OPEN);
//            BlockState newState = state.with(OPEN, !isOpen);
//            world.setBlockState(pos, newState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
//
//            // Schedule the task to reset the OPEN state after 7 seconds
//            scheduler.schedule(() -> {
//                world.getServer().execute(() -> {
//                    BlockState updatedState = world.getBlockState(pos);
//                    if (updatedState.get(OPEN)) {
//                        world.setBlockState(pos, updatedState.with(OPEN, false), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
//                    }
//                });
//            }, 7, TimeUnit.SECONDS);
//
//            return ActionResult.SUCCESS;
//        }
//
//        return ActionResult.FAIL;
//    }




//    @Override
//    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
//        // Close the block when the scheduled tick happens.
//        if (state.get(OPEN)) {
//            world.setBlockState(pos, state.with(OPEN, false), NOTIFY_ALL_AND_REDRAW);
//        }
//    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? CUBE_SHAPE : FULL_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(OPEN) ? VoxelShapes.empty() : FULL_BLOCK_SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return state.get(OPEN) ? VoxelShapes.empty() : FULL_BLOCK_SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        updateStateBasedOnNeighbors(state, world, pos);
    }
    private void updateStateBasedOnNeighbors(BlockState state, World world, BlockPos pos) {


        if (!state.get(NORMAL)) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof DungeonWireBlock) {
                    if (neighborState.get(POWERED)) {
                        open = true;
                        break; // No need to check further, as we only need one powered neighbor.
                    } else {
                        open = false;
                    }
                }
            }
        }
        if (state.get(NORMAL)) {



            if (state.get(OPEN)) {
                scheduler.schedule(() -> {
                    world.getServer().execute(() -> {
                        BlockState currentState = world.getBlockState(pos);
                        if (currentState.get(OPEN)) {
                        }

                    });
                }, 4, TimeUnit.SECONDS);
            }
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (neighborState.getBlock() instanceof DungeonDoorBlock) {
                    if (neighborState.get(OPEN) && !state.get(OPEN)) {
                        open = true;
                    }
                    if (neighborState.get(OPEN)) {
                        scheduler.schedule(() -> {
                            world.getServer().execute(() -> {
                                BlockState currentState = world.getBlockState(pos);
                                if (currentState.get(OPEN)) {
                                    world.setBlockState(pos, currentState.with(OPEN, false), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                                }

                            });
                        }, 4, TimeUnit.SECONDS);
                    }
                }
            }
        }

        // Propagate the open state to adjacent blocks if it's being opened.

        // Update the current block's state.
        BlockState newState = state
        .with(OPEN, open);
        world.setBlockState(pos, newState, NOTIFY_ALL_AND_REDRAW);
    }
}


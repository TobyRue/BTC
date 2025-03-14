package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.IDungeonWireAction;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DungeonDoorBlock extends Block implements IDungeonWireAction {
    public static final BooleanProperty WIRED = BooleanProperty.of("wired");
    public static final BooleanProperty OPEN = BooleanProperty.of("open");

    // Define the 4x4x4 cube shape.
    private static final VoxelShape CUBE_SHAPE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    // Define the full block shape.
    private static final VoxelShape FULL_BLOCK_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public static final int MAX_DISTANCE = 7;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DungeonDoorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OPEN, false)
                .with(WIRED, false));
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OPEN, false)
                .with(WIRED, false);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OPEN);
        builder.add(WIRED);
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updateStateBasedOnNeighbors(state, world, pos);
    }

//    @Override
//    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
//        if (state.get(WIRED) && !state.get(OPEN)) {
//            BlockState newState = state.with(OPEN, true);
//            world.setBlockState(pos, newState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
//            return ItemActionResult.SUCCESS;
//        }
//        return ItemActionResult.FAIL;
//    }

    private Set<BlockPos> findDoors(World world, BlockPos originPos) {
        HashSet<BlockPos> found = new HashSet<>();
        Queue<Pair<BlockPos, Integer>> queue = new LinkedList<>();
        BlockState originState = world.getBlockState(originPos);

        queue.add(new Pair<>(originPos, 0));
        found.add(originPos);

        while(!queue.isEmpty()) {
            var entry = queue.poll();
            var pos = entry.getLeft();
            int distance = entry.getRight();

            if(distance < MAX_DISTANCE) {
                for(Direction direction : Direction.values()) {
                    var neighborPos = pos.offset(direction);
                    var neighborState = world.getBlockState(neighborPos);

                    if(!found.contains(neighborPos) && neighborState.getBlock() instanceof DungeonDoorBlock && neighborState.get(WIRED) == originState.get(WIRED)) {
                        queue.add(new Pair<>(neighborPos, distance + 1));
                        found.add(neighborPos);
                    }
                }
            }
        }
        return found;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
//        if(!state.get(WIRED)) {
            //return open(state, world, pos);
//        }
        if(!state.get(WIRED) && !state.get(OPEN)) {
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, true, 4000);
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
                //super.onUse(state, world, pos, player, hit);
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open) {
        return setOpen(state, world, pos, open, null);
    }

    private ActionResult setOpen(BlockState state, World world, BlockPos pos, boolean open, @Nullable Integer delay) {
        if(state.get(OPEN) != open) {
            world.setBlockState(pos, state.with(OPEN, open));
            world.playSound(pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1.0f, 1.0f, true);

            if(open && delay != null) {
                // only executed on server so no sound played on client fix me
                scheduler.schedule(() -> {
                   // world.getServer().execute(() -> {
                        BlockState currentState = world.getBlockState(pos);
                        if (currentState.getBlock() == ModBlocks.DUNGEON_DOOR && currentState.get(OPEN)) {
                            setOpen(currentState, world, pos, false);
                        }
                    //});
                }, delay, TimeUnit.MILLISECONDS);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    


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


//        if (!state.get(WIRED)) {
//            for (Direction direction : Direction.values()) {
//                BlockPos neighborPos = pos.offset(direction);
//                BlockState neighborState = world.getBlockState(neighborPos);
//                if (neighborState.getBlock() instanceof DungeonWireBlock) {
//                    if (neighborState.get(POWERED)) {
//                        open = true;
//                        break; // No need to check further, as we only need one powered neighbor.
//                    } else {
//                        open = false;
//                    }
//                }
//            }
//        }
//        if (state.get(WIRED)) {
//
//
//
//            for (Direction direction : Direction.values()) {
//                BlockPos neighborPos = pos.offset(direction);
//                BlockState neighborState = world.getBlockState(neighborPos);
//
//
//
//            }
//        }

        // Propagate the open state to adjacent blocks if it's being opened.

        // Update the current block's state.
    }

    @Override
    public void onDungeonWireChange(BlockState state, World world, BlockPos pos, Direction from, boolean powered) {
        if(state.get(WIRED)) {
            for (BlockPos offsetPos : findDoors(world, pos)) {
                setOpen(world.getBlockState(offsetPos), world, offsetPos, powered);
            }
        }
    }

}
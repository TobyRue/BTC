package io.github.tobyrue.btc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
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

import static io.github.tobyrue.btc.DungeonWireBlock.POWERED;

public class DungeonDoorBlock extends Block {
    public static final BooleanProperty NORMAL = BooleanProperty.of("normal");
    public static final BooleanProperty OPEN = BooleanProperty.of("open");
    // Define the 4x4x4 cube shape.
    private static final VoxelShape CUBE_SHAPE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
    // Define the full block shape.
    private static final VoxelShape FULL_BLOCK_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    boolean open = false;

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
        if (state.get(NORMAL)) {
            // Toggle the OPEN state when clicked.
            boolean isOpen = state.get(OPEN);
            BlockState newState = state.with(OPEN, !isOpen);
            world.setBlockState(pos, newState, NOTIFY_ALL_AND_REDRAW);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
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

        // Propagate the open state to adjacent blocks if it's being opened.

        // Update the current block's state.
        BlockState newState = state
        .with(OPEN, open);
        world.setBlockState(pos, newState, NOTIFY_ALL_AND_REDRAW);
    }
}


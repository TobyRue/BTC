package io.github.tobyrue.btc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DungeonWireBlock extends Block {
    public static final MapCodec<DungeonWireBlock> CODEC = createCodec(DungeonWireBlock::new);
    public static final BooleanProperty FACING_DOWN = BooleanProperty.of("facing_down");
    public static final BooleanProperty FACING_UP = BooleanProperty.of("facing_up");
    public static final BooleanProperty FACING_LEFT = BooleanProperty.of("facing_left");
    public static final BooleanProperty FACING_RIGHT = BooleanProperty.of("facing_right");
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static DirectionProperty FACING;

    public DungeonWireBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING_DOWN, false)
                .with(FACING_UP, false)
                .with(FACING_RIGHT, false)
                .with(FACING_LEFT, false)
                .with(FACING, Direction.NORTH)
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
                .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()))
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING_DOWN, FACING_UP, FACING_LEFT, FACING_RIGHT, FACING, POWERED);
    }
    /*@Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.isCreative()){
            System.out.println("click");
            if(state.get(FACING_UP)) {
                world.setBlockState(pos, state.with(FACING_UP, false));
                world.setBlockState(pos, state.with(FACING_DOWN, true));
            }
            if(state.get(FACING_DOWN)) {
                world.setBlockState(pos, state.with(FACING_UP, true));
                world.setBlockState(pos, state.with(FACING_DOWN, false));
            }
        }
        return ItemActionResult.FAIL;
    }*/


    /*@Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof DungeonWireBlock) {
                // Do something if the neighboring block is also a DungeonWireBlock
                // For example, you could update the properties of this block based on the neighboring block
                // Example:

                if(state.get(FACING) == Direction.NORTH) {
                    if (direction == Direction.UP) {
                        world.setBlockState(pos, state.with(FACING_UP, true));
                    } else if (direction == Direction.DOWN) {
                        world.setBlockState(pos, state.with(FACING_DOWN, true));
                    } else if (direction == Direction.EAST && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.WEST && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.EAST && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.WEST && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    }
                }
                if(state.get(FACING) == Direction.EAST) {
                    if (direction == Direction.UP) {
                        world.setBlockState(pos, state.with(FACING_UP, true));
                    } else if (direction == Direction.DOWN) {
                        world.setBlockState(pos, state.with(FACING_DOWN, true));
                    } else if (direction == Direction.NORTH && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.SOUTH && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.NORTH && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.SOUTH && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    }
                }
                if(state.get(FACING) == Direction.SOUTH) {
                    if (direction == Direction.UP) {
                        world.setBlockState(pos, state.with(FACING_UP, true));
                    } else if (direction == Direction.DOWN) {
                        world.setBlockState(pos, state.with(FACING_DOWN, true));
                    } else if (direction == Direction.EAST && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.WEST && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.EAST && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.WEST && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    }
                }
                if(state.get(FACING) == Direction.WEST) {
                    if (direction == Direction.UP) {
                        world.setBlockState(pos, state.with(FACING_UP, true));
                    } else if (direction == Direction.DOWN) {
                        world.setBlockState(pos, state.with(FACING_DOWN, true));
                    } else if (direction == Direction.NORTH && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.SOUTH && state.get(FACING_UP)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    } else if (direction == Direction.NORTH && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_LEFT, true));
                    } else if (direction == Direction.SOUTH && state.get(FACING_DOWN)) {
                        world.setBlockState(pos, state.with(FACING_RIGHT, true));
                    }
                }
            }
        }
    }*/
    static {
        FACING = HorizontalFacingBlock.FACING;
    }
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updateStateBasedOnNeighbors(state, world, pos);
    }

    private void updateStateBasedOnNeighbors(BlockState state, World world, BlockPos pos) {
        boolean facingDown = false;
        boolean facingUp = false;
        boolean facingLeft = false;
        boolean facingRight = false;

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);


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
                .with(FACING_RIGHT, facingRight);

        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
    }
}
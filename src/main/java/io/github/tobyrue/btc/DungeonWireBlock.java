package io.github.tobyrue.btc;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
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

    static {
        FACING = HorizontalFacingBlock.FACING;
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

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof DungeonWireBlock) {
                if (direction == Direction.UP || direction == Direction.DOWN || direction == Direction.NORTH || direction == Direction.EAST || direction == Direction.SOUTH || direction == Direction.WEST && powered == true) {
                    powered = true;
                    System.out.println("Powering");
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
                .with(POWERED, powered);

        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
    }
}
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
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
    public static final BooleanProperty ROOT = BooleanProperty.of("root");
    public static final BooleanProperty CONNECTED_ROOT = BooleanProperty.of("connected_root");
    public static final BooleanProperty CONNECTED = BooleanProperty.of("connected");
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
                .with(CONNECTED_ROOT, false)
                .with(CONNECTED, false)
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
                .with(CONNECTED_ROOT, false)
                .with(CONNECTED, false)
                .with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()))
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING_DOWN, FACING_UP, FACING_LEFT, FACING_RIGHT, ROOT, CONNECTED_ROOT, CONNECTED, FACING, POWERED);
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
        boolean connectedRoot = false;
        boolean connected = false;
        boolean powered = false;



        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);


            if(state.get(ROOT)) {
                powered = true; // ROOT is true, so POWERED must be true
                if(!state.get(POWERED)) {
                    powered = true;
                }
            }
            if(neighborState.getBlock() instanceof DungeonWireBlock) {
                if (neighborState.get(ROOT)){
                    powered = true;
                } else if (!neighborState.get(ROOT)) {
                    powered = false;
                }
                if (neighborState.get(POWERED)){
                    powered = true;
                } else if (!neighborState.get(POWERED)){
                    powered = false;
                }


                /*if (neighborState.get(ROOT)) {
                    connected = true;
                }
                if (!neighborState.get(ROOT)) {
                    connected = false;
                }
                if (!state.get(CONNECTED)) {
                    powered = false;
                    connectedRoot = false;
                }
                if (neighborState.get(POWERED) && neighborState.get(CONNECTED_ROOT)) {
                    powered = true;
                }
                if (neighborState.get(POWERED) && neighborState.get(ROOT)) {
                    powered = true;
                }
                if (neighborState.get(ROOT) || neighborState.get(CONNECTED_ROOT)) {
                    connectedRoot = true;
                }
                if (!neighborState.get(CONNECTED_ROOT) && !neighborState.get(ROOT)) {
                    connectedRoot = false;
                }
                if (!neighborState.get(CONNECTED_ROOT) && !neighborState.get(ROOT)) {
                    powered = false;
                }*/
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
                .with(POWERED, powered)
                .with(CONNECTED, connected)
                .with(CONNECTED_ROOT, connectedRoot);
        //System.out.println("Updating state of block at " + pos + " to " + newState);
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS);
    }
}
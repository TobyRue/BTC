package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PowerPillarBlock extends Block implements Waterloggable, IDungeonWire {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    /**
     * This property <code>FACING</code> is in reference to the direction of the output of the block, if the output is <code>UP</code> and input <code>DOWN</code>, the {@link Direction} of the block will be <code>UP</code>.
     */
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final IntProperty DELAY = IntProperty.of("delay", 0 ,7);

    public PowerPillarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false)
                .with(FACING, Direction.UP)
                .with(DELAY, 0)
                .with(Properties.WATERLOGGED, false));
    }

    private static final VoxelShape COLUMN_UP_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape COLUMN_NORTH_SOUTH =  VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 1)
    );
    private static final VoxelShape COLUMN_EAST_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.25, 1, 0.75, 0.75)
    );


    public void updatePower(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        boolean currentlyPowered = state.get(POWERED);
        boolean shouldBePowered = calculatePower(world, pos, state);

        if (currentlyPowered != shouldBePowered) {
            world.setBlockState(pos, state.with(POWERED, shouldBePowered), Block.NOTIFY_ALL);
            for (Direction dir : Direction.values()) {
                if (state.get(FACING) == dir) {
                    world.updateNeighborsAlways(pos.offset(dir), state.getBlock());
                }
            }
        }
    }

    private boolean calculatePower(World world, BlockPos pos, BlockState state) {
        return IDungeonWire.isReceivingDungeonWirePower(state, world, pos, state.get(FACING).getOpposite());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            if (state.get(DELAY) == 0) {
                this.updatePower(world, pos, state);
            } else {
                world.scheduleBlockTick(pos, this, state.get(DELAY));
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        this.updatePower(world, pos, state);
        super.scheduledTick(state, world, pos, random);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(POWERED, false)
                .with(FACING, ctx.getPlayerLookDirection().getOpposite())
                .with(DELAY, 0)
                .with(Properties.WATERLOGGED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, FACING, DELAY, Properties.WATERLOGGED);
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return state.getBlock() instanceof PowerPillarBlock && state.get(POWERED) && state.get(FACING) == face;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING).getAxis()) {
            case X -> {
                return COLUMN_EAST_WEST;
            }
            case Y -> {
                return COLUMN_UP_DOWN;
            }
            case Z -> {
                return COLUMN_NORTH_SOUTH;
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING).getAxis()) {
            case X -> {
                return COLUMN_EAST_WEST;
            }
            case Y -> {
                return COLUMN_UP_DOWN;
            }
            case Z -> {
                return COLUMN_NORTH_SOUTH;
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        switch (state.get(FACING).getAxis()) {
            case X -> {
                return COLUMN_EAST_WEST;
            }
            case Y -> {
                return COLUMN_UP_DOWN;
            }
            case Z -> {
                return COLUMN_NORTH_SOUTH;
            }
        }
        return super.getRaycastShape(state, world, pos);
    }
}

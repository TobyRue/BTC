package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.enums.IWrenchType;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.IHaveWrenchActions;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.IOnBlockUpdate;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireDelayHelper;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PowerPilasterBlock extends Block implements IDungeonWire, Waterloggable, IWireDelayHelper, IOnBlockUpdate {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    /**
     * If this state, <code>INVERTED</code> is true, the {@link Direction} of the <code>FACING</code> property is in reference to the direction of the input rather than the output of the block, if the output is <code>UP</code> and input <code>DOWN</code>, and the state <code>INVERTED</code> is true, the {@link Direction} of the block will be <code>DOWN</code>
     */
    public static final BooleanProperty INVERTED = BooleanProperty.of("inverted");
    /**
     * This property <code>FACING</code> is in reference to the direction of the output of the block, if the output is <code>UP</code> and input <code>DOWN</code>, the {@link Direction} of the block will be <code>UP</code>.
     * However, if the state <code>INVERTED</code> is true, the {@link Direction} of the <code>FACING</code> property is in reference to the direction of the input of the block, if the output is <code>UP</code> and input <code>DOWN</code>, and the state <code>INVERTED</code> is true, the {@link Direction} of the block will be <code>DOWN</code>.
     */
    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final IntProperty DELAY = IntProperty.of("delay", 0 ,7);

    private static final VoxelShape UP_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 0.5, 1),
            VoxelShapes.cuboid(0.25, 0.5, 0.25, 0.75, 1, 0.75)
    );

    private static final VoxelShape DOWN_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.5, 0, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
    );

    private static final VoxelShape SOUTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.5),
            VoxelShapes.cuboid(0.25, 0.25, 0.5, 0.75, 0.75, 1)
    );
    private static final VoxelShape NORTH_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0.5, 1, 1, 1),
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 0.5)
    );
    private static final VoxelShape WEST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0.5, 0, 0, 1, 1, 1),
            VoxelShapes.cuboid(0, 0.25, 0.25, 0.5, 0.75, 0.75)
    );
    private static final VoxelShape EAST_MAIN = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0, 0, 0.5, 1, 1),
            VoxelShapes.cuboid(0.5, 0.25, 0.25, 1, 0.75, 0.75)
    );

    public PowerPilasterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false)
                .with(INVERTED, false)
                .with(FACING, Direction.UP)
                .with(DELAY, 0)
                .with(Properties.WATERLOGGED, false));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(POWERED, false)
                .with(INVERTED, false)
                .with(FACING, ctx.getSide())
                .with(DELAY, 0)
                .with(Properties.WATERLOGGED, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, INVERTED, FACING, DELAY, Properties.WATERLOGGED);
    }

    public void updatePower(World world, BlockPos pos, BlockState state) {
        if (world == null || world.isClient) return;
        boolean currentlyPowered = state.get(POWERED);
        boolean shouldBePowered = calculatePower(world, pos, state);

        if (currentlyPowered != shouldBePowered) {
            world.setBlockState(pos, state.with(POWERED, shouldBePowered), Block.NOTIFY_ALL);
            for (Direction dir : Direction.values()) {
                if (state.get(FACING) == dir && !state.get(INVERTED)) {
                    world.updateNeighbor(pos.offset(dir), world.getBlockState(pos.offset(dir)).getBlock(), pos);
                } else if (state.get(FACING) == dir.getOpposite() && state.get(INVERTED)) {
                    world.updateNeighbor(pos.offset(dir.getOpposite()), world.getBlockState(pos.offset(dir.getOpposite())).getBlock(), pos);
                }
            }
        }
    }

    private boolean calculatePower(World world, BlockPos pos, BlockState state) {
        return IDungeonWire.isReceivingDungeonWirePower(state, world, pos, state.get(INVERTED) ? state.get(FACING) : state.get(FACING).getOpposite());
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return state.getBlock() instanceof PowerPilasterBlock && state.get(POWERED)
                && ((state.get(INVERTED) && state.get(FACING).getOpposite() == face) || (!state.get(INVERTED) && state.get(FACING) == face));
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        this.onUpdate(world, pos, state);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        this.onUpdate(world, pos, state);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, net.minecraft.util.math.random.Random random) {
        this.updatePower(world, pos, state);
        super.scheduledTick(state, world, pos, random);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH -> {
                return NORTH_MAIN;
            }
            case EAST -> {
                return EAST_MAIN;
            }
            case SOUTH -> {
                return SOUTH_MAIN;
            }
            case WEST -> {
                return WEST_MAIN;
            }
            case UP -> {
                return UP_MAIN;
            }
            case DOWN -> {
                return DOWN_MAIN;
            }
        }
        return super.getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH -> {
                return NORTH_MAIN;
            }
            case EAST -> {
                return EAST_MAIN;
            }
            case SOUTH -> {
                return SOUTH_MAIN;
            }
            case WEST -> {
                return WEST_MAIN;
            }
            case UP -> {
                return UP_MAIN;
            }
            case DOWN -> {
                return DOWN_MAIN;
            }
        }
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        switch (state.get(FACING)) {
            case NORTH -> {
                return NORTH_MAIN;
            }
            case EAST -> {
                return EAST_MAIN;
            }
            case SOUTH -> {
                return SOUTH_MAIN;
            }
            case WEST -> {
                return WEST_MAIN;
            }
            case UP -> {
                return UP_MAIN;
            }
            case DOWN -> {
                return DOWN_MAIN;
            }
        }
        return super.getRaycastShape(state, world, pos);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(Properties.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public void setDelay(int delay, World world, BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof PowerPilasterBlock) {
            world.setBlockState(pos, state.with(DELAY, delay));
        }
    }

    @Override
    public int getDelay(World world, BlockState state, BlockPos pos) {
        if (state.getBlock() instanceof PowerPilasterBlock) {
            return state.get(DELAY);
        }
        return 0;
    }

    @Override
    public void onUpdate(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            if (state.get(DELAY) == 0) {
                this.updatePower(world, pos, state);
            } else if (!world.getBlockTickScheduler().isQueued(pos, this)) {
                world.scheduleBlockTick(pos, this, state.get(DELAY));
            }
        }
    }
}

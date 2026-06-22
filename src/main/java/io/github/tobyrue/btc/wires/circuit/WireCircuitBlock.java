package io.github.tobyrue.btc.wires.circuit;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class WireCircuitBlock extends Block implements ModBlockEntityProvider<WireCircuitBlockEntity>, IDungeonWire, IWireConnectionHelper {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);

    public WireCircuitBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(POWERED, false)
                .with(FACING, Direction.NORTH)
                .with(MIRRORED, BlockMirror.NONE));
    }


    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntityType<WireCircuitBlockEntity> getBlockEntityType() {
        return ModBlockEntities.DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
        builder.add(FACING);
        builder.add(MIRRORED);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;

        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            Direction currentFacing = state.get(FACING);
            BlockMirror currentMirror = state.get(MIRRORED);

            if (currentFacing != Direction.NORTH || currentMirror != BlockMirror.NONE) {
                if (currentMirror != BlockMirror.NONE) {
                    circuit.mirrorConnections(currentMirror);
                }

                if (currentFacing != Direction.NORTH) {
                    BlockRotation rot = getRotationBetween(Direction.NORTH, currentFacing);
                    circuit.rotateConnections(rot);
                }

                BlockState newState = state.with(FACING, Direction.NORTH).with(MIRRORED, BlockMirror.NONE);
                world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS | Block.NO_REDRAW);
            }

            circuit.updateCircuitLogic();
        }
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            Direction checkSide = direction.getOpposite();
            if (circuit.getConnection(checkSide, (World) world, state, pos) == WireBlock.ConnectionType.REDSTONE_OUTPUT
                    && circuit.getOutputPowerState(checkSide)) {
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(MIRRORED, mirror);
    }

    private BlockRotation getRotationBetween(Direction oldDir, Direction newDir) {
        if (oldDir == newDir) return BlockRotation.NONE;
        if (oldDir.rotateYClockwise() == newDir) return BlockRotation.CLOCKWISE_90;
        if (oldDir.getOpposite() == newDir) return BlockRotation.CLOCKWISE_180;
        return BlockRotation.COUNTERCLOCKWISE_90;
    }

    @Override
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit && circuit.isEmittingDungeonWirePower(state, world, pos, face);
    }

    @Override
    public void setConnection(Direction face, WireBlock.ConnectionType connectionType, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            circuit.setConnection(face, connectionType, world, state, pos);
        }
    }

    @Override
    public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            return circuit.cycleConnection(face, world, state, pos);
        }
        return WireBlock.ConnectionType.NONE;
    }

    @Override
    public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            return circuit.getConnection(face, world, state, pos);
        }
        return WireBlock.ConnectionType.NONE;
    }

    @Override
    public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WireCircuitBlockEntity circuit) {
            return circuit.getConnections(world, state, pos);
        }
        return Map.of();
    }
}
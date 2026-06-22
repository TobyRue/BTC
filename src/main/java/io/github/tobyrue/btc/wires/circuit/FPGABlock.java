package io.github.tobyrue.btc.wires.circuit;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import io.github.tobyrue.btc.wires.IDungeonWire;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.wire_data_helper.IWireConnectionHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FPGABlock extends Block implements ModBlockEntityProvider<FPGABlockEntity>, IDungeonWire, IWireConnectionHelper {
    public static final BooleanProperty POWERED = BooleanProperty.of("powered");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);

    public FPGABlock(Settings settings) {
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
    public BlockEntityType<FPGABlockEntity> getBlockEntityType() {
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
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            ItemStack heldItem = player.getStackInHand(hand);

            if (fpga.hasBook()) {
                if (!world.isClient) {
                    ItemStack book = fpga.removeBook();
                    if (!player.getInventory().insertStack(book)) {
                        player.dropItem(book, false);
                    }
                }
                return ItemActionResult.SUCCESS;
            } else if (heldItem.isOf(Items.WRITABLE_BOOK) || heldItem.isOf(Items.WRITTEN_BOOK)) {
                if (!world.isClient) {
                    fpga.setBook(heldItem.split(1));
                }
                return ItemActionResult.SUCCESS;
            }
        }
        return ItemActionResult.FAIL;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga && fpga.hasBook()) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), fpga.removeBook());
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            fpga.updateCircuitLogic();
        }
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            Direction checkSide = direction.getOpposite();
            if (fpga.getConnection(checkSide, (World) world, state, pos) == WireBlock.ConnectionType.REDSTONE_OUTPUT
                    && fpga.getOutputPowerState(checkSide)) {
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
    public boolean isEmittingDungeonWirePower(BlockState state, World world, BlockPos pos, Direction face) {
        return world.getBlockEntity(pos) instanceof FPGABlockEntity fpga && fpga.isEmittingDungeonWirePower(state, world, pos, face);
    }

    @Override
    public void setConnection(Direction face, WireBlock.ConnectionType connectionType, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            fpga.setConnection(face, connectionType, world, state, pos);
        }
    }

    @Override
    public WireBlock.ConnectionType cycleConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            return fpga.cycleConnection(face, world, state, pos);
        }
        return WireBlock.ConnectionType.NONE;
    }

    @Override
    public WireBlock.ConnectionType getConnection(Direction face, World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            return fpga.getConnection(face, world, state, pos);
        }
        return WireBlock.ConnectionType.NONE;
    }

    @Override
    public Map<Direction, WireBlock.ConnectionType> getConnections(World world, BlockState state, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof FPGABlockEntity fpga) {
            return fpga.getConnections(world, state, pos);
        }
        return Map.of();
    }
}
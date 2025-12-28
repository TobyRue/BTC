package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.item.SelectorItem;
import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.wires.IWireConnect;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MobDetectorBlock extends Block implements ModBlockEntityProvider<MobDetectorBlockEntity>, ModTickBlockEntityProvider<MobDetectorBlockEntity>, IWireConnect, CornerStorage {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public MobDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(WireBlock.CONNECTION_TO_DIRECTION.get().keySet().stream().reduce(
                this.stateManager.getDefaultState().with(WireBlock.POWERED, false).with(FACING, Direction.NORTH),
                (acc, con) -> acc.with(con, WireBlock.ConnectionType.OUTPUT),
                (lhs, rhs) -> {
                    throw new RuntimeException("Don't fold in parallel");
                }
        ));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        for (var conn : WireBlock.CONNECTION_TO_DIRECTION.get().keySet())
            builder.add(conn);
        builder.add(WireBlock.POWERED);
        builder.add(FACING);
    }

    @Override
    public BlockEntityType<MobDetectorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {


        if (!(stack.getItem() instanceof SelectorItem) || player.isSneaking()) {
            return ItemActionResult.FAIL;
        }

        var corner1 = stack.get(BTC.CORNER_1_POSITION_COMPONENT);
        var corner2 = stack.get(BTC.CORNER_2_POSITION_COMPONENT);

        if (corner1 == null || corner2 == null) {
            return ItemActionResult.FAIL;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof MobDetectorBlockEntity detector) {
            var b1 = new BlockPos(corner1.x(), corner1.y(), corner1.z());
            var b2 = new BlockPos(corner2.x(), corner2.y(), corner2.z());
            detector.setDetectionBox(b1, b2);
            detector.markDirty();
            player.sendMessage(
                    Text.translatable("item.btc.selector.set_box", b1.toShortString(), b2.toShortString(), pos.toShortString()),
                    true
            );
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.FAIL;
    }


    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(WireBlock.POWERED) ? 15 : 0;
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof MobDetectorBlockEntity detector) {
            return detector.getBox(stack, blockPos, state, world);
        }
        return null;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}

package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.misc.CornerStorage;
import io.github.tobyrue.btc.regestries.ModComponents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class TrialShroudBlock extends Block implements CornerStorage, ModBlockEntityProvider<TrialShroudBlockEntity>, ModTickBlockEntityProvider<TrialShroudBlockEntity> {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    /*
     * Mirrored is only switched with the last state if for example it is mirrored along North to South while the block has a facing property of north, otherwise it will not change.
     * This is used to detect if the BlockBox needs to be mirrored, this is done by returning a new distance array with the opposite axis it was mirrored on with the same numbers but negative.
     */
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);
    protected static final VoxelShape SHAPE = Block.createCuboidShape((double)0.0F, (double)0.0F, (double)0.0F, (double)16.0F, (double)1.0F, (double)16.0F);


    public TrialShroudBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(MIRRORED, BlockMirror.NONE));
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
    
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(MIRRORED);
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof TrialShroudBlockEntity detector) {
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
        Direction facing = state.get(FACING);

        return state.with(MIRRORED, mirror);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean isWrenchSelector = stack.getItem() instanceof io.github.tobyrue.btc.item.WrenchItem
                && stack.get(ModComponents.WRENCH_TYPE) == io.github.tobyrue.btc.enums.WrenchType.SELECTOR;

        if (!isWrenchSelector || player.isSneaking()) {
            return ItemActionResult.FAIL;
        }

        var corner1 = stack.get(ModComponents.CORNER_1_POSITION_COMPONENT);
        var corner2 = stack.get(ModComponents.CORNER_2_POSITION_COMPONENT);

        if (corner1 == null || corner2 == null) {
            return ItemActionResult.FAIL;
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TrialShroudBlockEntity detector) {
            var b1 = new BlockPos(corner1.x(), corner1.y(), corner1.z());
            var b2 = new BlockPos(corner2.x(), corner2.y(), corner2.z());
            detector.setDetectionBox(b1, b2);
            detector.markDirty();
            player.sendMessage(
                    Text.translatable("item.btc.wrench.selector.set_box", b1.toShortString(), b2.toShortString(), pos.toShortString()),
                    true
            );
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.FAIL;
    }


    @Override
    public BlockEntityType<TrialShroudBlockEntity> getBlockEntityType() {
        return ModBlockEntities.TRIAL_SHROUD_BLOCK_ENTITY;
    }
}

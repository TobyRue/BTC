package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.enums.AntierType;
import io.github.tobyrue.btc.item.SelectorItem;
import io.github.tobyrue.btc.misc.CornerStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AntierBlock extends Block implements ModBlockEntityProvider<AntierBlockEntity>, ModTickBlockEntityProvider<AntierBlockEntity>, IDungeonWireConnect, CornerStorage {
    public static final EnumProperty<AntierType> ANTIER_TYPE = EnumProperty.of("antier_type", AntierType.class);
    public static final BooleanProperty DISABLE = BooleanProperty.of("disable");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    /*
     * Mirrored is only switched with the last state if for example it is mirrored along North to South while the block has a facing property of north, otherwise it will not change.
     * This is used to detect if the BlockBox needs to be mirrored, this is done by returning a new distance array with the opposite axis it was mirrored on with the same numbers but negative.
     */
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);
    public static final BooleanProperty USES_SELECTOR = BooleanProperty.of("uses_selector");

    private static final VoxelShape MIDDLE;
    private static final VoxelShape TOP;
    private static final VoxelShape BOTTOM;
    private static final VoxelShape SHAPE;
    private static final VoxelShape FULL_SHAPE;

    static {
        MIDDLE = Block.createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
        TOP = Block.createCuboidShape(0.0, 14.0, 0.0, 16.0, 16.0, 16.0);
        BOTTOM = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
        FULL_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        SHAPE = VoxelShapes.union(MIDDLE, TOP, BOTTOM);
    }
    public AntierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(ANTIER_TYPE, AntierType.NO_MINE)
                .with(DISABLE, false)
                .with(USES_SELECTOR, false)
                .with(FACING, Direction.NORTH)
                .with(MIRRORED, BlockMirror.NONE));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(ANTIER_TYPE, AntierType.NO_MINE)
                .with(DISABLE, false)
                .with(USES_SELECTOR, false)
                .with(FACING, Direction.NORTH)
                .with(MIRRORED, BlockMirror.NONE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISABLE);
        builder.add(ANTIER_TYPE);
        builder.add(FACING);
        builder.add(MIRRORED);
        builder.add(USES_SELECTOR);
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return FULL_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return FULL_SHAPE;
    }

    @Override
    public BlockEntityType<AntierBlockEntity> getBlockEntityType() {
        return ModBlockEntities.ANTIER_BLOCK_ENTITY;
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        if (state.get(AntierBlock.DISABLE) && state.getBlock() instanceof AntierBlock) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof AntierBlockEntity detector) {
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
        return state.with(MIRRORED, mirror);
    }
}

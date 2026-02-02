package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.IDungeonWireConnect;
import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.enums.AntierType;
import io.github.tobyrue.btc.item.SelectorItem;
import io.github.tobyrue.btc.misc.CornerStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PotionPillar extends Block implements ModBlockEntityProvider<PotionPillarBlockEntity>, ModTickBlockEntityProvider<PotionPillarBlockEntity>, IDungeonWireConnect, CornerStorage {
//    public static final EnumProperty<AntierType> ANTIER_TYPE = EnumProperty.of("antier_type", AntierType.class);
    public static final BooleanProperty DISABLE = BooleanProperty.of("disable");
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final EnumProperty<Direction.Axis> AXIS = Properties.AXIS;
    /*
     * Mirrored is only switched with the last state if for example it is mirrored along North to South while the block has a facing property of north, otherwise it will not change.
     * This is used to detect if the BlockBox needs to be mirrored, this is done by returning a new distance array with the opposite axis it was mirrored on with the same numbers but negative.
     */
    public static final EnumProperty<BlockMirror> MIRRORED = EnumProperty.of("mirrored", BlockMirror.class);
    public static final BooleanProperty USES_SELECTOR = BooleanProperty.of("uses_selector");

    private static final VoxelShape COLUMN_UP_DOWN = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 1, 0.75)
    );
    private static final VoxelShape COLUMN_NORTH_SOUTH =  VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0.25, 0, 0.75, 0.75, 1)
    );
    private static final VoxelShape COLUMN_EAST_WEST = VoxelShapes.union(
            VoxelShapes.cuboid(0, 0.25, 0.25, 1, 0.75, 0.75)
    );

    public PotionPillar(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(DISABLE, false)
                .with(USES_SELECTOR, false)
                .with(FACING, Direction.NORTH)
                .with(MIRRORED, BlockMirror.NONE).with(AXIS, Direction.Axis.Y));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getPlayerLookDirection();

        return this.getDefaultState()
                .with(DISABLE, false)
                .with(USES_SELECTOR, false)
                .with(AXIS, direction.getAxis())
                .with(FACING, Direction.NORTH)
                .with(MIRRORED, BlockMirror.NONE);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DISABLE);
        builder.add(FACING);
        builder.add(MIRRORED);
        builder.add(AXIS);
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
        if (be instanceof PotionPillarBlockEntity detector) {
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
        switch (state.get(AXIS)) {
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
        switch (state.get(AXIS)) {
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
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        switch (state.get(AXIS)) {
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

    @Override
    public BlockEntityType<PotionPillarBlockEntity> getBlockEntityType() {
        return ModBlockEntities.POTION_PILLAR_BLOCK_ENTITY;
    }

    @Override
    public boolean shouldConnect(BlockState state, World world, BlockPos pos) {
        if (state.get(PotionPillar.DISABLE) && state.getBlock() instanceof PotionPillar) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockBox getBox(ItemStack stack, BlockPos blockPos, BlockState state, World world) {
        BlockEntity be = world.getBlockEntity(blockPos);
        if (be instanceof PotionPillarBlockEntity detector) {
            return detector.getBox(stack, blockPos, state, world);
        }
        return null;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING))).with(AXIS, switch (state.get(AXIS)) {
            case X -> switch (rotation) {
                case NONE, CLOCKWISE_180 -> state.get(AXIS);
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> Direction.Axis.Z;
            };
            case Y -> switch (rotation) {
                case NONE, CLOCKWISE_180, CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.get(AXIS);
            };
            case Z -> switch (rotation) {
                case NONE, CLOCKWISE_180 -> state.get(AXIS);
                case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> Direction.Axis.X;
            };
        });
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(MIRRORED, mirror);
    }
}

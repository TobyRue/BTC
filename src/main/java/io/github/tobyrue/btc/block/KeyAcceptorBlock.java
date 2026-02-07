package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.wires.IDungeonWirePowered;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KeyAcceptorBlock extends Block implements ModBlockEntityProvider<KeyAcceptorBlockEntity>, ModTickBlockEntityProvider<KeyAcceptorBlockEntity>, Waterloggable {
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape TOP_MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_MIDDLE_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape BOTTOM_SHAPE1;

    private static final VoxelShape SHAPE;

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public KeyAcceptorBlock(Settings settings) {
        super(settings);
        this.stateManager.getDefaultState().with(POWERED, false).with(Properties.WATERLOGGED, false);
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, Properties.WATERLOGGED);
    }
    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(POWERED, false).with(Properties.WATERLOGGED, false);
    }

    static {
        BOTTOM_SHAPE1 = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
        BOTTOM_SHAPE = Block.createCuboidShape(2.0, 1.0, 2.0, 14.0, 2.0, 14.0);
        TOP_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 2.0, 3.0, 13.0, 5.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 5.0, 4.0, 12.0, 9.0, 12.0);
        BOTTOM_MIDDLE_SHAPE = Block.createCuboidShape(3.0, 9.0, 3.0, 13.0, 12.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 12.0, 2.0, 14.0, 14.0, 14.0);

        SHAPE = VoxelShapes.union(BOTTOM_SHAPE1, BOTTOM_SHAPE, TOP_MIDDLE_SHAPE, MIDDLE_SHAPE, BOTTOM_MIDDLE_SHAPE, TOP_SHAPE);
    }
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos, ModBlockEntities.KEY_ACCEPTOR_ENTITY).get().getStrongRedstonePower(state, world, pos, direction);
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return world.getBlockEntity(pos, ModBlockEntities.KEY_ACCEPTOR_ENTITY).get().getWeakRedstonePower(state, world, pos, direction);
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }



    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return world.getBlockEntity(pos, ModBlockEntities.KEY_ACCEPTOR_ENTITY).get().onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public BlockEntityType<KeyAcceptorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.KEY_ACCEPTOR_ENTITY;
    }
    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(Properties.WATERLOGGED)) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}

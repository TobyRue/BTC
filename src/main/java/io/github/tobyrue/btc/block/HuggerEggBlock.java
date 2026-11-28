package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HuggerEggBlock extends BlockWithEntity implements ModBlockEntityProvider<HuggerEggBlockEntity>, ModTickBlockEntityProvider<HuggerEggBlockEntity> {
    public static final MapCodec<HuggerEggBlock> CODEC = createCodec(HuggerEggBlock::new);
    public static final IntProperty GROWTH_LEVEL = IntProperty.of("growth", 0, 2);
    private static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0.25, 0, 0.25, 0.75, 0.5, 0.75)
    );

    public HuggerEggBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(GROWTH_LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(GROWTH_LEVEL);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<HuggerEggBlockEntity> getBlockEntityType() {
        return ModBlockEntities.HUGGER_EGG_BLOCK_ENTITY;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

}
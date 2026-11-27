package io.github.tobyrue.btc.block;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.block.entities.HuggerEggBlockEntity;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.entities.ModBlockEntityProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.Direction;

public class HuggerEggBlock extends BlockWithEntity implements ModBlockEntityProvider<HuggerEggBlockEntity> {
    public static final MapCodec<HuggerEggBlock> CODEC = createCodec(HuggerEggBlock::new);
    public static final IntProperty GROWTH_LEVEL = IntProperty.of("growth", 0, 2);

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
}

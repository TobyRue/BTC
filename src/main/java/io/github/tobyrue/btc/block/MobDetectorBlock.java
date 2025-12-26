package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.block.entities.*;
import io.github.tobyrue.btc.wires.IWireConnect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MobDetectorBlock extends Block implements ModBlockEntityProvider<MobDetectorBlockEntity>, ModTickBlockEntityProvider<MobDetectorBlockEntity>, IWireConnect {
    public static final BooleanProperty POWERED  = BooleanProperty.of("powered");


    public MobDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState((this.stateManager.getDefaultState()).with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED);
    }

    @Override
    public BlockEntityType<MobDetectorBlockEntity> getBlockEntityType() {
        return ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(POWERED) ? 15 : 0;
    }

}

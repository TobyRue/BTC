package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class HuggerEggBlockEntity extends BlockEntity {
    public HuggerEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HUGGER_EGG_BLOCK_ENTITY, pos, state);
    }
}

package io.github.tobyrue.btc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class KeyDispenserBlockEntity extends BlockEntity {
    public KeyDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEY_DISPENSER_ENTITY, pos, state);
    }
}

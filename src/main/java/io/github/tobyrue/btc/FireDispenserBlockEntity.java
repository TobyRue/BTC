package io.github.tobyrue.btc;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static io.github.tobyrue.btc.DungeonWireBlock.POWERED;

public class FireDispenserBlockEntity extends BlockEntity implements BlockEntityTicker<FireDispenserBlockEntity> {
    public FireDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIRE_DISPENSER_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FireDispenserBlockEntity blockEntity) {
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.getBlock() instanceof DungeonWireBlock && neighborState.get(POWERED) && state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) == FireDispenserType.SHORT_FIRE) {
                BlockState newState1 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.TALL_FIRE);
                world.setBlockState(pos, newState1);
            } else if(neighborState.getBlock() instanceof DungeonWireBlock && !neighborState.get(POWERED) && state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) == FireDispenserType.TALL_FIRE) {
                BlockState newState2 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.SHORT_FIRE);
                world.setBlockState(pos, newState2);
            }
            if (neighborState.getBlock() instanceof DungeonWireBlock && neighborState.get(POWERED) && state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) == FireDispenserType.SHORT_FIRE_SOUL) {
                BlockState newState3 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.TALL_FIRE_SOUL);
                world.setBlockState(pos, newState3);
            } else if(neighborState.getBlock() instanceof DungeonWireBlock && !neighborState.get(POWERED) && state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) == FireDispenserType.TALL_FIRE_SOUL) {
                BlockState newState4 = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.SHORT_FIRE_SOUL);
                world.setBlockState(pos, newState4);
            }
            if (neighborState.getBlock() instanceof DungeonWireBlock) {
                
            }
        }
    }
}

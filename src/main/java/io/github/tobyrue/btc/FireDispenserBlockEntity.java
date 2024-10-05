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

            if(neighborState.getBlock() instanceof DungeonWireBlock) {
                BlockState no_fire = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.NO_FIRE);
                BlockState short_fire = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.SHORT_FIRE);
                BlockState short_fire_soul = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.SHORT_FIRE_SOUL);
                BlockState tall_fire = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.TALL_FIRE);
                BlockState tall_fire_soul = state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, FireDispenserType.TALL_FIRE_SOUL);

                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_TO_TALL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_SOUL_TO_SHORT) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_SOUL_TO_TALL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_SOUL_TO_TALL_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_TO_SHORT_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.SHORT_TO_TALL_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_SOUL_TO_SHORT) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_SOUL_TO_SHORT_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_SOUL_TO_TALL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_TO_SHORT) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_TO_SHORT_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, short_fire_soul);
                    }
                }
                if(state.get(FireDispenserBlock.FIRE_SWICH) == FireSwich.TALL_TO_TALL_SOUL) {
                    if(!neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire);
                    } else if(neighborState.get(POWERED)) {
                        world.setBlockState(pos, tall_fire_soul);
                    }
                }
            }
        }
    }
}

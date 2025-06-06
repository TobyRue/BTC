package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.enums.FireDispenserType;
import io.github.tobyrue.btc.enums.FireSwich;
import io.github.tobyrue.btc.block.FireDispenserBlock;
import io.github.tobyrue.btc.wires.IDungeonWirePowered;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static io.github.tobyrue.btc.block.DungeonWireBlock.POWERED;

public class FireDispenserBlockEntity extends BlockEntity implements BlockEntityTicker<FireDispenserBlockEntity>, IDungeonWirePowered {
    public FireDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FIRE_DISPENSER_ENTITY, pos, state);
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, FireDispenserBlockEntity blockEntity) {

        FireDispenserType newType = getFireTypeFromSwitch(state.get(FireDispenserBlock.FIRE_SWICH), shouldWirePower(state, world, pos, false, true, true));
        if (state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) != newType) {
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            world.setBlockState(pos, state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, newType));
        }
//            if (neighborState.getBlock() instanceof DungeonWireBlock) {
//                boolean isPowered = neighborState.get(DungeonWireBlock.POWERED);
//                FireDispenserType newType = getFireTypeFromSwitch(state.get(FireDispenserBlock.FIRE_SWICH), isPowered);
//
//                if (state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) != newType) {
//                    world.setBlockState(pos, state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, newType));
//                }
//            }
//            if (neighborState.getBlock() instanceof CopperWireBlock) {
//                boolean isPowered = neighborState.get(CopperWireBlock.POWERED1);
//                FireDispenserType newType = getFireTypeFromSwitch(state.get(FireDispenserBlock.FIRE_SWICH), isPowered);
//
//                if (state.get(FireDispenserBlock.FIRE_DISPENSER_TYPE) != newType) {
//                    world.setBlockState(pos, state.with(FireDispenserBlock.FIRE_DISPENSER_TYPE, newType));
//                }
//            }
    }

    private FireDispenserType getFireTypeFromSwitch(FireSwich fireSwich, boolean isPowered) {
        return switch (fireSwich) {
            case SHORT_TO_TALL -> isPowered ? FireDispenserType.TALL_FIRE : FireDispenserType.SHORT_FIRE;
            case SHORT_SOUL_TO_SHORT -> isPowered ? FireDispenserType.SHORT_FIRE : FireDispenserType.SHORT_FIRE_SOUL;
            case SHORT_SOUL_TO_TALL -> isPowered ? FireDispenserType.TALL_FIRE : FireDispenserType.SHORT_FIRE_SOUL;
            case SHORT_SOUL_TO_TALL_SOUL -> isPowered ? FireDispenserType.TALL_FIRE_SOUL : FireDispenserType.SHORT_FIRE_SOUL;
            case SHORT_TO_SHORT_SOUL -> isPowered ? FireDispenserType.SHORT_FIRE_SOUL : FireDispenserType.SHORT_FIRE;
            case SHORT_TO_TALL_SOUL -> isPowered ? FireDispenserType.TALL_FIRE_SOUL : FireDispenserType.SHORT_FIRE;
            case TALL_SOUL_TO_SHORT -> isPowered ? FireDispenserType.SHORT_FIRE : FireDispenserType.TALL_FIRE_SOUL;
            case TALL_SOUL_TO_SHORT_SOUL -> isPowered ? FireDispenserType.SHORT_FIRE_SOUL : FireDispenserType.TALL_FIRE_SOUL;
            case TALL_SOUL_TO_TALL -> isPowered ? FireDispenserType.TALL_FIRE : FireDispenserType.TALL_FIRE_SOUL;
            case TALL_TO_SHORT -> isPowered ? FireDispenserType.SHORT_FIRE : FireDispenserType.TALL_FIRE;
            case TALL_TO_SHORT_SOUL -> isPowered ? FireDispenserType.SHORT_FIRE_SOUL : FireDispenserType.TALL_FIRE;
            case TALL_TO_TALL_SOUL -> isPowered ? FireDispenserType.TALL_FIRE_SOUL : FireDispenserType.TALL_FIRE;
            case TALL_SOUL_TO_OFF -> isPowered ? FireDispenserType.NO_FIRE : FireDispenserType.TALL_FIRE_SOUL;
            case SHORT_SOUL_TO_OFF -> isPowered ? FireDispenserType.NO_FIRE : FireDispenserType.SHORT_FIRE_SOUL;
            case TALL_TO_OFF -> isPowered ? FireDispenserType.NO_FIRE : FireDispenserType.TALL_FIRE;
            case SHORT_TO_OFF -> isPowered ? FireDispenserType.NO_FIRE : FireDispenserType.SHORT_FIRE;
            case OFF_TO_TALL_SOUL -> isPowered ? FireDispenserType.TALL_FIRE_SOUL : FireDispenserType.NO_FIRE;
            case OFF_TO_SHORT_SOUL -> isPowered ? FireDispenserType.SHORT_FIRE_SOUL : FireDispenserType.NO_FIRE;
            case OFF_TO_TALL -> isPowered ? FireDispenserType.TALL_FIRE : FireDispenserType.NO_FIRE;
            case OFF_TO_SHORT -> isPowered ? FireDispenserType.SHORT_FIRE : FireDispenserType.NO_FIRE;
        };
    }
}




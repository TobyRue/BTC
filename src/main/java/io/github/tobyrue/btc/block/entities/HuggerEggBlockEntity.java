package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.HuggerEggBlock;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HuggerEggBlockEntity extends BlockEntity implements BlockEntityTicker<HuggerEggBlockEntity> {
    private float huggerYaw = 0f;
    private int tickCount = 0;

    public HuggerEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HUGGER_EGG_BLOCK_ENTITY, pos, state);
    }

    public float getHuggerYaw() {
        return huggerYaw;
    }

    public void setHuggerYaw(float huggerYaw) {
        this.huggerYaw = huggerYaw;
    }

    public void tick(World world, BlockPos pos, BlockState state, HuggerEggBlockEntity blockEntity) {
        if (world.isClient()) {
            return;
        }

        blockEntity.tickCount++;
        if (blockEntity.tickCount < 120) {
            return;
        }
        blockEntity.tickCount = 0;

        PlayerEntity closestPlayer = world.getClosestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16.0, true);

        if (closestPlayer != null) {
            int currentGrowth = state.get(HuggerEggBlock.GROWTH_LEVEL);

            if (currentGrowth < 2) {
                world.setBlockState(pos, state.with(HuggerEggBlock.GROWTH_LEVEL, currentGrowth + 1), 3);
            } else {
                world.breakBlock(pos, false);
                HuggerEntity hugger = ModEntities.HUGGER_ENTITY.create(world);
                if (hugger != null) {
                    hugger.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
                    world.spawnEntity(hugger);
                }
            }
        }
    }
}
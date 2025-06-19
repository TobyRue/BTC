package io.github.tobyrue.btc.status_effects;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;

public class FrostWalkerEffect extends StatusEffect {
    public FrostWalkerEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.BENEFICIAL, 0x8CB3FE);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {

        BlockPos centerPos = entity.getBlockPos();
        var world = entity.getWorld();

        BlockState standingBlock = world.getBlockState(centerPos.down());
        BlockState headBlock = world.getBlockState(centerPos);
        BlockState aboveHeadBlock = world.getBlockState(centerPos.up());

        // Only works if standing on solid block AND not underwater
        if (!(!standingBlock.isSolid()
                || headBlock.getFluidState().isStill()
                || aboveHeadBlock.getFluidState().isStill())) {


            int radius = Math.min(Math.max(amplifier, 4), 128);
            BlockPos.Mutable pos = new BlockPos.Mutable();

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (dx * dx + dz * dz <= radius * radius) {
                        pos.set(centerPos.getX() + dx, centerPos.getY() - 1, centerPos.getZ() + dz);

                        BlockState state = world.getBlockState(pos);
                        BlockState above = world.getBlockState(pos.up());

                        if (state.getFluidState().getFluid().matchesType(Fluids.WATER)
                                && above.getFluidState().isEmpty()
                                && above.isAir()) {
                            world.setBlockState(pos, Blocks.FROSTED_ICE.getDefaultState());
                        }
                    }
                }
            }
        }
        return super.applyUpdateEffect(entity, amplifier);
    }
}

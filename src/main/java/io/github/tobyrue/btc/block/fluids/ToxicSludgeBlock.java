package io.github.tobyrue.btc.block.fluids;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToxicSludgeBlock extends FluidBlock {
    public ToxicSludgeBlock(FlowableFluid fluid, Settings settings) {
        super(fluid, settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && entity instanceof LivingEntity livingEntity) {
            if (livingEntity instanceof PlayerEntity player && !player.isCreative() && !player.isSpectator()) {
                livingEntity.damage(world.getDamageSources().genericKill(), player.getMaxHealth() / 4);
                return;
            }
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
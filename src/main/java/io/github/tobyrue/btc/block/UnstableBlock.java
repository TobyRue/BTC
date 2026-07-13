package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UnstableBlock extends Block {
    private final Block turnsInto;

    public UnstableBlock(Settings settings, Block turnsInto) {
        super(settings);
        this.turnsInto = turnsInto;
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        super.onProjectileHit(world, state, hit, projectile);
        if (projectile instanceof ArrowEntity) {
            breakBlock(world, hit.getBlockPos(), state);
        }
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        super.onSteppedOn(world, pos, state, entity);
        if (entity.getType().isIn(BTC.UNSTABLE_BLOCK_WHITELIST)) {
            breakBlock(world, pos, state);
        }
    }

    private void breakBlock(World world, BlockPos pos, BlockState state) {
        if (!world.isClient()) {
            world.breakBlock(pos, false);
            world.setBlockState(pos, turnsInto.getDefaultState());
            world.updateNeighbors(pos, state.getBlock());
        }
    }
}

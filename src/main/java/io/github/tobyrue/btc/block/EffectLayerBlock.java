package io.github.tobyrue.btc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EffectLayerBlock extends Block {
    private final StatusEffectInstance effect;

    public EffectLayerBlock(Settings settings, StatusEffectInstance effect) {
        super(settings);
        this.effect = effect;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0F, 0.0F, 0.0F, 16.0F, 2.0F, 16.0F);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient() && entity instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(this.effect));
        }
        super.onEntityCollision(state, world, pos, entity);
    }
}
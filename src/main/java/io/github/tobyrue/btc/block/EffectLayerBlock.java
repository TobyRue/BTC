package io.github.tobyrue.btc.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EffectLayerBlock extends Block {
    private final StatusEffectInstance effect;
    private final Item result;

    public EffectLayerBlock(Settings settings, StatusEffectInstance effect, Item result) {
        super(settings);
        this.effect = effect;
        this.result = result;
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
        if (entity.shouldSpawnSprintingParticles()) {
            double d = entity.getPos().getX() + (world.getRandom().nextDouble() - (double) 0.5F) * (double) entity.getDimensions(EntityPose.STANDING).width();
            double e = entity.getPos().getZ() + (world.getRandom().nextDouble() - (double) 0.5F) * (double) entity.getDimensions(EntityPose.STANDING).width();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), d, entity.getPos().getY() + 0.1, e, entity.getPos().getX() * (double) -4.0F, (double) 1.5F, entity.getPos().getZ() * (double) -4.0F);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.BUCKET)) {
            if (!player.isCreative()) {
                stack.decrement(1);
                player.getInventory().offerOrDrop(this.result.getDefaultStack());
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
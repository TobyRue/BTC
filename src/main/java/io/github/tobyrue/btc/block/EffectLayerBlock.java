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

import java.util.function.Supplier;

public class EffectLayerBlock extends Block {
    private final StatusEffectInstance effect;
    private final Supplier<Item> resultSupplier;

    public EffectLayerBlock(Settings settings, StatusEffectInstance effect, Supplier<Item> resultSupplier) {
        super(settings);
        this.effect = effect;
        this.resultSupplier = resultSupplier;
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
            double d = entity.getPos().getX() + (world.getRandom().nextDouble() - 0.5D) * entity.getDimensions(EntityPose.STANDING).width();
            double e = entity.getPos().getZ() + (world.getRandom().nextDouble() - 0.5D) * entity.getDimensions(EntityPose.STANDING).width();
            world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, state), d, entity.getPos().getY() + 0.1, e, entity.getPos().getX() * -4.0D, 1.5D, entity.getPos().getZ() * -4.0D);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.BUCKET)) {
            if (!player.isCreative()) {
                stack.decrement(1);
                Item resultItem = this.resultSupplier.get();
                if (resultItem != null) {
                    player.getInventory().offerOrDrop(resultItem.getDefaultStack());
                }
            }
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return ItemActionResult.SUCCESS;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BrazierBlock extends Block {
    private static final VoxelShape BOTTOM_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE;
    private static final VoxelShape MIDDLE_SHAPE2;
    private static final VoxelShape TOP_SHAPE;
    private static final VoxelShape COAL_SHAPE1;
    private static final VoxelShape COAL_SHAPE2;
    private static final VoxelShape COAL_SHAPE3;
    private static final VoxelShape COAL_SHAPE4;

    private static final VoxelShape SHAPE;

    public static final BooleanProperty OMINOUS = BooleanProperty.of("ominous");
    public static final BooleanProperty DAMAGES = BooleanProperty.of("damages");

    public BrazierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(OMINOUS, false)
                .with(DAMAGES, true));
    }

    static {
        BOTTOM_SHAPE = Block.createCuboidShape(3.0, 0.0, 3.0, 13.0, 2.0, 13.0);
        MIDDLE_SHAPE = Block.createCuboidShape(4.0, 2.0, 4.0, 12.0, 6.0, 12.0);
        MIDDLE_SHAPE2 = Block.createCuboidShape(3.0, 6.0, 3.0, 13.0, 7.0, 13.0);
        TOP_SHAPE = Block.createCuboidShape(2.0, 7.0, 2.0, 14.0, 10.0, 14.0);
        COAL_SHAPE1 = Block.createCuboidShape(3.0, 8.0, 3.0, 13.0, 11.0, 13.0);
        COAL_SHAPE2 = Block.createCuboidShape(4.0, 11.0, 4.0, 12.0, 12.0, 12.0);
        COAL_SHAPE3 = Block.createCuboidShape(5.0, 12.0, 6.0, 11.0, 13.0, 10.0);
        COAL_SHAPE4 = Block.createCuboidShape(7.0, 13.0, 7.0, 10.0, 14.0, 9.0);
        SHAPE = VoxelShapes.union(BOTTOM_SHAPE, MIDDLE_SHAPE, MIDDLE_SHAPE2, TOP_SHAPE, COAL_SHAPE1, COAL_SHAPE2, COAL_SHAPE3, COAL_SHAPE4);
    }



    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!state.get(OMINOUS) && stack.isOf(Items.OMINOUS_BOTTLE)) {
            world.setBlockState(pos, state.with(OMINOUS, true));
            stack.decrementUnlessCreative(1, player);
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack(Items.GLASS_BOTTLE);
                player.getInventory().offerOrDrop(dropStack);
            }
            return ItemActionResult.SUCCESS;
        }
        if (state.get(OMINOUS) && stack.isOf(Items.GLASS_BOTTLE)) {
            world.setBlockState(pos, state.with(OMINOUS, false));
            stack.decrementUnlessCreative(1, player);
            if (!player.isCreative()) {
                ItemStack dropStack = new ItemStack(Items.OMINOUS_BOTTLE);
                player.getInventory().offerOrDrop(dropStack);
            }
            return ItemActionResult.SUCCESS;
        }
        if (!state.get(DAMAGES) && stack.isOf(Items.BLAZE_POWDER)) {
            world.setBlockState(pos, state.with(DAMAGES, true));
            stack.decrementUnlessCreative(1, player);
            return ItemActionResult.SUCCESS;
        }
        if (state.get(DAMAGES) && stack.isOf(Items.SNOWBALL)) {
            world.setBlockState(pos, state.with(DAMAGES, false));
            stack.decrementUnlessCreative(1, player);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(OMINOUS, false)
                .with(DAMAGES, true);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(OMINOUS, DAMAGES);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return SHAPE;
    }

    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (state.get(DAMAGES)) {
            if (!state.get(OMINOUS)) {
                if (entity instanceof LivingEntity) {
                    entity.damage(world.getDamageSources().hotFloor(), 2);
                    if (!entity.isFireImmune()) {
                        entity.setFireTicks(entity.getFireTicks() + 1);
                        if (entity.getFireTicks() == 0) {
                            entity.setOnFireFor(8.0F);
                        }
                    }
                }
            } else {
                if (entity instanceof LivingEntity livingEntity) {
                    if (!livingEntity.isInvulnerableTo(world.getDamageSources().wither())) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 120));
                    }
                }
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(8) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 2.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);
        }
        int i;
        double d2;
        double e2;
        double f2;
        for(i = 0; i < 3; ++i) {
            d2 = (double)pos.getX() + random.nextDouble() * 0.35 + 0.35;
            e2 = (double)pos.getY() + random.nextDouble() * 0.5 + 0.5;
            f2 = (double)pos.getZ() + random.nextDouble() * 0.35 + 0.35;
            world.addParticle(ParticleTypes.SMOKE, d2, e2, f2, 0.0, 0.0, 0.0);
            if(state.get(OMINOUS)) {
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d2, e2, f2, 0.0, 0.0, 0.0);
            } else if(!state.get(OMINOUS)) {
                world.addParticle(ParticleTypes.FLAME, d2, e2, f2, 0.0, 0.0, 0.0);
            }
        }
        super.randomDisplayTick(state, world, pos, random);
    }
}
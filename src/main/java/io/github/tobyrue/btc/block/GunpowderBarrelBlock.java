package io.github.tobyrue.btc.block;

import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GunpowderBarrelBlock extends Block {
    public static final IntProperty LEVEL =  IntProperty.of("level", 1, 8);
    public static final IntProperty FUSE = IntProperty.of("fuse", 0, 10);
    public static final BooleanProperty BURNING = BooleanProperty.of("burning");
    public static final DirectionProperty FACING = Properties.FACING;

    public GunpowderBarrelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(LEVEL, 1).with(FUSE, 10).with(BURNING, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!oldState.isOf(state.getBlock())) {
            if (world.isReceivingRedstonePower(pos)) {
                ignite(world, state, pos);
            }
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isReceivingRedstonePower(pos)) {
            ignite(world, state, pos);
        }
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
    }

    public void ignite(World world, BlockState state, BlockPos pos) {
        if (!state.get(BURNING) && !world.isClient()) {
            world.setBlockState(pos, state.with(BURNING, true), Block.NOTIFY_ALL);
            world.playSound(null, pos, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.5f);
            world.scheduleBlockTick(pos, this, 10);
        }
    }
    public void onBurnOut(World world, BlockState state, BlockPos pos) {
        if (!world.isClient()) {
            if (state.get(LEVEL) > 1) {
                world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), state.get(LEVEL), World.ExplosionSourceType.TNT);
            } else {
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.GUNPOWDER)) {
            if (state.get(LEVEL) < 8) {
                world.setBlockState(pos, state.with(LEVEL, state.get(LEVEL) + 1));
                stack.decrementUnlessCreative(1, player);
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                return ItemActionResult.success(true);
            }
        } else if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        } else {
            ignite(world, state, pos);
            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.decrementUnlessCreative(1, player);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.success(true);
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    public int getColor(BlockState state) {
        if (!state.get(BURNING)) {
            return 0x505050;
        }
        int fuse = state.get(FUSE);
        float factor = 1.0f - (fuse / 10.0f);

        int r = (int) (139 + (116 * factor));
        int g = (int) (64 + (101 * factor));
        int b = 0;

        return (r << 16) | (g << 8) | b;
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (!world.isClient) {
            BlockPos blockPos = hit.getBlockPos();
            if (projectile.isOnFire() && projectile.canModifyAt(world, blockPos)) {
                ignite(world, state, blockPos);
            }
        }
    }


    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(BURNING)) return;

        int fuse = state.get(FUSE);

        if (fuse > 0) {
            world.setBlockState(pos, state.with(FUSE, fuse - 1), Block.NOTIFY_ALL);
            world.scheduleBlockTick(pos, this, 10);
        } else {
            this.onBurnOut(world, state, pos);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(BURNING)) {
            var newPos = pos.offset(state.get(FACING));
            double d = (double)newPos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            double e = (double)newPos.getY() + 0.15;
            double f = (double)newPos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.4;
            world.addParticle(ParticleTypes.SMOKE, d, e, f, 0.0, 0.0, 0.0);
            if (random.nextInt(3) == 0) {
                world.addParticle(ParticleTypes.FLAME, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }


    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(LEVEL, 1).with(FUSE, 10).with(BURNING, false).with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }
    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LEVEL, FUSE, BURNING, FACING);
    }
}

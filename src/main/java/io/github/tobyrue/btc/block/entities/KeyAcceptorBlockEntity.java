package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.KeyAcceptorBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class KeyAcceptorBlockEntity extends BlockEntity implements BlockEntityTicker<KeyAcceptorBlockEntity> {
    public int delay = 0;

    public KeyAcceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEY_ACCEPTOR_ENTITY, pos, state);
    }

    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Condition checking if it is already active
        if (state.get(KeyAcceptorBlock.POWERED)) {
            return ItemActionResult.FAIL;
        }

        boolean isOminous = state.get(KeyAcceptorBlock.IS_OMINOUS);

        // Checks match requirements: Normal block accepts Normal Key, Ominous block accepts Ominous Key
        if ((isOminous && stack.isOf(Items.OMINOUS_TRIAL_KEY)) || (!isOminous && stack.isOf(Items.TRIAL_KEY))) {
            if (!world.isClient) {
                world.setBlockState(pos, state.with(KeyAcceptorBlock.POWERED, true), Block.NOTIFY_ALL);
                world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
                if (!player.isCreative()) {
                    stack.decrement(1);
                }
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.FAIL;
    }

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(KeyAcceptorBlock.POWERED) ? 15 : 0;
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(KeyAcceptorBlock.POWERED) ? 15 : 0;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos, Direction direction) {
        world.updateNeighborsAlways(pos, state.getBlock());
        world.updateNeighborsAlways(pos.offset(direction), state.getBlock());
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, KeyAcceptorBlockEntity blockEntity) {
        for (Direction direction : Direction.values()) {
            updateNeighbors(state, world, pos, direction);
        }

        if (state.get(KeyAcceptorBlock.POWERED)) {
            if (state.get(KeyAcceptorBlock.STAYS_POWERED)) {
                if (delay < 40) {
                    ++delay;
                }
            } else {
                ++delay;
            }

            double random1 = Math.random();
            double random2 = Math.random();
            double random3 = Math.random();

            double d2 = (double)pos.getX() + random1;
            double e2 = (double)pos.getY() + random2 + 1;
            double f2 = (double)pos.getZ() + random3;
            world.addParticle(ParticleTypes.ENCHANTED_HIT, d2, e2, f2, 0.0, 0.0, 0.0);

            if (!state.get(KeyAcceptorBlock.STAYS_POWERED) && delay >= 40) {
                world.updateNeighborsAlways(pos, state.getBlock());
                world.addParticle(ParticleTypes.GUST, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0, 0, 0);
                delay = 0;
                world.setBlockState(pos, state.with(KeyAcceptorBlock.POWERED, false), Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Emitter.of(state));
            }
        } else {
            if (delay > 0) {
                delay = 0;
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Delay", this.delay);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.delay = nbt.getInt("Delay");
    }
}
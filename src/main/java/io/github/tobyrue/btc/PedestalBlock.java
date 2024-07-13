package io.github.tobyrue.btc;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PedestalBlock extends Block implements BlockEntityProvider{

    public static final EnumProperty<StateVariant> VARIANT = EnumProperty.of("pedestal_state", StateVariant.class);
    public static final IntProperty KEYS = IntProperty.of("keys", 0, 4); // Range from 0 to 4

    public PedestalBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(VARIANT, StateVariant.ACTIVE));
        this.setDefaultState(this.stateManager.getDefaultState().with(KEYS, 0));
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
        builder.add(KEYS);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        /*if(stack.getItem() == ModItems.RUBY_TRIAL_KEY && state.get(KEYS) == 0) {
            stack.decrement(1);
            world.setBlockState(pos, state.with(KEYS, 1));
            player.playSound(SoundEvents.BLOCK_VAULT_ACTIVATE);
            return ItemActionResult.CONSUME;
        }
        if(stack.getItem() == ModItems.RUBY_TRIAL_KEY && state.get(KEYS) == 1) {
            stack.decrement(1);
            world.setBlockState(pos, state.with(KEYS, 2));
            player.playSound(SoundEvents.BLOCK_VAULT_ACTIVATE);
            return ItemActionResult.CONSUME;
        }
        if(stack.getItem() == ModItems.RUBY_TRIAL_KEY && state.get(KEYS) == 2) {
            stack.decrement(1);
            world.setBlockState(pos, state.with(KEYS, 3));
            player.playSound(SoundEvents.BLOCK_VAULT_ACTIVATE);
            return ItemActionResult.CONSUME;
        }
        if(stack.getItem() == ModItems.RUBY_TRIAL_KEY && state.get(KEYS) == 3) {
            stack.decrement(1);
            world.setBlockState(pos, state.with(KEYS, 4).with(VARIANT, StaffPedestalBlock.StateVariant.INACTIVE));
            player.playSound(SoundEvents.BLOCK_VAULT_OPEN_SHUTTER);

            if (!world.isClient) {
                // Create the item stack to drop
                ItemStack dropItem = new ItemStack(ModItems.STAFF); // Replace with your custom item

                // Create the item entity to spawn in the world
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY() + 1, pos.getZ(), dropItem);

                // Spawn the item entity in the world
                world.spawnEntity(itemEntity);

                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.CONSUME;
        }
        if(stack.getItem() == ModItems.RUBY_TRIAL_KEY && state.get(KEYS) == 4 && state.get(VARIANT) == StateVariant.INACTIVE) {
            stack.decrement(1);
            world.setBlockState(pos, state.with(KEYS, 0).with(VARIANT, StateVariant.ACTIVE));
            player.playSound(SoundEvents.BLOCK_VAULT_ACTIVATE);
            return ItemActionResult.CONSUME;
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit); */

        return world.getBlockEntity(pos, ModBlockEntities.PEDESTAL_BLOCK_ENTITY).get().onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PedestalBlockEntity(pos, state);
    }


    public enum StateVariant implements StringIdentifiable {
        ACTIVE("active"),
        INACTIVE("inactive");

        private final String name;

        StateVariant(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }

    }
}

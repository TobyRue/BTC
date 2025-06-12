package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.KeyAcceptorBlock;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.wires.IDungeonWirePowered;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;

public class KeyAcceptorBlockEntity extends BlockEntity implements BlockEntityTicker<KeyAcceptorBlockEntity> {
    public int delay = 0;


    public KeyAcceptorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEY_ACCEPTOR_ENTITY, pos, state);
    }

    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.getItem() == Items.TRIAL_KEY && delay == 0) {
            world.setBlockState(pos, state.with(KeyAcceptorBlock.POWERED, true), Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);
            if (!player.isCreative()) {
                stack.decrement(1);
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

//        if (state.get(KeyAcceptorBlock.POWERED)) {
//            getWorld().updateNeighborsAlways(pos, state.getBlock());
////            updateNeighbors(state, getWorld(), pos);
//            return 15;
//        }
//        return 0;
    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos, Direction direction) {
        world.updateNeighborsAlways(pos, state.getBlock());
        world.updateNeighborsAlways(pos.offset(direction), state.getBlock());
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState state, KeyAcceptorBlockEntity blockEntity) {
//        if (world.getBlockState(pos.offset(Direction.DOWN, 2)).getBlock() == Blocks.STICKY_PISTON) {
//            world.updateNeighborsAlways(pos.offset(Direction.DOWN), state.getBlock());
//        }
        for (Direction direction : Direction.values()) {
            updateNeighbors(state, getWorld(), pos, direction);
        }
        if (state.get(KeyAcceptorBlock.POWERED)) {
            ++delay;
            double random1 = Math.random();
            double random2 = Math.random();
            double random3 = Math.random();

            double d2;
            double e2;
            double f2;
            d2 = (double)pos.getX() + random1;
            e2 = (double)pos.getY() + random2 + 1;
            f2 = (double)pos.getZ() + random3;
            world.addParticle(ParticleTypes.ENCHANTED_HIT, d2, e2, f2, 0.0, 0.0, 0.0);
            if (delay >= 40) {
                getWorld().updateNeighborsAlways(pos, state.getBlock());
//                this.updateNeighbors(state, world, pos);
                world.addParticle(ParticleTypes.GUST, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0, 0, 0);
                delay = 0;
                world.setBlockState(pos, state.with(KeyAcceptorBlock.POWERED, false), Block.NOTIFY_ALL);
//                world.emitGameEvent(player, GameEvent.BLOCK_DEACTIVATE, pos);
                world.emitGameEvent(GameEvent.BLOCK_DEACTIVATE, pos, GameEvent.Emitter.of(state));
            }
        }
    }
}

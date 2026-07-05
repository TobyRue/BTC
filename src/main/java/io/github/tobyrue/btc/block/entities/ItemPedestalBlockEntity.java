package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.ItemPedestalBlock;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.wires.IDungeonWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.HashSet;


public class ItemPedestalBlockEntity extends BlockEntity {

    public final HashSet<String> HASH_SET = new HashSet<>();
    private ItemStack stack = ModItems.RUBY_TRIAL_KEY.getDefaultStack();

    public ItemStack getStack() {
        return stack;
    }

    public ItemPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ITEM_PEDESTAL_BLOCK_ENTITY, pos, state);
    }

    public boolean canGiveTo(PlayerEntity player) {
        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof ItemPedestalBlock) {
            return (!HASH_SET.contains(player.getUuid().toString()) && IDungeonWire.isReceivingDungeonWirePower(world.getBlockState(pos), world, pos, Direction.DOWN)) || world.getBlockState(pos).get(ItemPedestalBlock.ALWAYS_ACCEPTABLE);
        }
        return false;
    }
    public boolean canShowTo(PlayerEntity player) {
        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof ItemPedestalBlock) {
            return (!HASH_SET.contains(player.getUuid().toString())) || world.getBlockState(pos).get(ItemPedestalBlock.ALWAYS_ACCEPTABLE);
        }
        return false;
    }
    public ActionResult onUse(PlayerEntity player, BlockHitResult hit) {
        if (world == null) return ActionResult.FAIL;
        var uuid = player.getUuid().toString();
        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof ItemPedestalBlock) {
            if (canGiveTo(player) && !player.isCreative()) {
                if (!state.get(ItemPedestalBlock.ALWAYS_ACCEPTABLE)) {
                    HASH_SET.add(uuid);
                }
                world.addParticle(ParticleTypes.GUST, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
                world.emitGameEvent(GameEvent.ENTITY_INTERACT, pos, GameEvent.Emitter.of(state));
                if (!world.isClient) {
                    player.getInventory().offerOrDrop(stack.copy());
                }
                markDirty();
                return ActionResult.SUCCESS;
            } else if (player.isCreative() && !player.getMainHandStack().isEmpty()) {
                this.stack = player.getMainHandStack().copy();

                this.markDirty();

                if (!world.isClient) {
                    world.updateListeners(pos, state, state, 3);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        NbtList nbtList = new NbtList();
        for (String uuid : HASH_SET) {
            nbtList.add(NbtString.of(uuid));
        }
        nbt.put("Players", nbtList);
        if (!stack.isEmpty()) {
            nbt.put("Item", stack.encode(registryLookup));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        HASH_SET.clear();
        NbtList nbtList = nbt.getList("Players", NbtElement.STRING_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            String uuid = nbtList.getString(i);
            HASH_SET.add(uuid);
        }

        if (nbt.contains("Item", 10)) {
            NbtCompound nbtCompound = nbt.getCompound("Item");
            stack = ItemStack.fromNbt(registryLookup, nbtCompound).orElse(ModItems.RUBY_TRIAL_KEY.getDefaultStack());
        } else {
            stack = ModItems.RUBY_TRIAL_KEY.getDefaultStack();
        }
    }


    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }


}

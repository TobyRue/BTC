package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;


public class PedestalBlockEntity extends BlockEntity {

    public final HashMap<String, Integer> HASH_MAP = new HashMap<>();

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, pos, state);
    }


    public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        var uuid = player.getUuid().toString();
        var c = HASH_MAP.getOrDefault(uuid, 0);

        if(stack.getItem() == ModItems.RUBY_TRIAL_KEY) {
            if(c >= 0 && c < 4) {
                HASH_MAP.put(uuid, c+1);
                player.playSound(SoundEvents.BLOCK_VAULT_ACTIVATE);
                // Add redstone particle effects
                DustParticleEffect dust = new DustParticleEffect(new Vector3f(1.0F, 0.0F, 0.0F), 2.0F); // Red color
                world.addParticle(dust, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, 0, 0.5, 0);
                if(!player.isCreative()) {
                    stack.decrement(1);
                }
                this.markDirty();
                return ItemActionResult.SUCCESS;
            }
        }
        if(c == 4) {
            HASH_MAP.put(uuid, -1);
            player.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
            ItemStack dropStack = new ItemStack(ModItems.STAFF);
            world.addParticle(ParticleTypes.GUST, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);
            if(!world.isClient) {
                player.getInventory().offerOrDrop(dropStack);
            }
            this.markDirty();
            return ItemActionResult.SUCCESS;
        }
        if(c == -1 || !(stack.getItem() == ModItems.RUBY_TRIAL_KEY)) {
            player.playSound(SoundEvents.BLOCK_VAULT_INSERT_ITEM_FAIL);
            world.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0, 0, 0);

        }

        return ItemActionResult.FAIL;
    }


    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        // Serialize HashMap<String, Integer> to NbtList
        NbtList nbtList = new NbtList();
        for (var entry : HASH_MAP.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putString("UUID", entry.getKey());
            entryNbt.putInt("Value", entry.getValue());
            nbtList.add(entryNbt);
        }
        nbt.put("CustomData", nbtList);
        System.out.println("NBT data written: " + nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);

        // Deserialize NbtList to HashMap<String, Integer>
        HASH_MAP.clear();
        NbtList nbtList = nbt.getList("CustomData", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound entryNbt = nbtList.getCompound(i);
            String uuid = entryNbt.getString("UUID");
            int value = entryNbt.getInt("Value");
            HASH_MAP.put(uuid, value);
        }
        System.out.println("NBT data read: " + nbt);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createNbt(registryLookup);
    }

    protected void readComponents(BlockEntity.ComponentsAccess components) {
        super.readComponents(components);
        System.out.println("Read Component");
    }

    protected void addComponents(ComponentMap.Builder componentMapBuilder) {
        super.addComponents(componentMapBuilder);
        System.out.println("Add Component");
    }
}
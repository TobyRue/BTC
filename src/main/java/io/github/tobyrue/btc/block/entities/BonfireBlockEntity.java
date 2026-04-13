package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BonfireBlockEntity extends BlockEntity {
    private List<UUID> activatedBy = new ArrayList<>();

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE_BLOCK_ENTITY, pos, state);
    }

    public void addActivatedBy(UUID uuid) {
        this.activatedBy.add(uuid);
        markDirty();
    }

    public void removeActivatedBy(UUID uuid) {
        if (this.activatedBy.remove(uuid)) {
            markDirty();
        }
    }

    public void setActivatedByListAndRemoveAll(List<UUID> uuid) {
        this.activatedBy.clear();
        this.activatedBy.addAll(uuid);
        markDirty();
    }

    public List<UUID> getActivatedBy() { return activatedBy; }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.activatedBy.clear();
        if (nbt.contains("activator", NbtElement.LIST_TYPE)) {
            NbtList list = nbt.getList("activator", NbtElement.STRING_TYPE);
            for (int i = 0; i < list.size(); i++) {
                try {
                    this.activatedBy.add(UUID.fromString(list.getString(i)));
                } catch (IllegalArgumentException ignored) {}
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        NbtList list = new NbtList();
        for (UUID uuid : activatedBy) {
            list.add(NbtString.of(uuid.toString()));
        }
        nbt.put("activator", list);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
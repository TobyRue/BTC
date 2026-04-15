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
    private int radius = 64;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE_BLOCK_ENTITY, pos, state);
    }

    public void setRadius(int radius) {
        this.radius = radius;
        markDirty();
    }

    public int getRadius() {
        return radius;
    }


    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("radius")) {
            this.radius = nbt.getInt("radius");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("radius", radius);
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
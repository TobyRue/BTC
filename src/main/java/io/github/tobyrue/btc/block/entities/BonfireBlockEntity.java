package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class BonfireBlockEntity extends BlockEntity {
    private UUID lastActivatedBy = null;

    public BonfireBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BONFIRE_BLOCK_ENTITY, pos, state);
    }

    public void setLastActivatedBy(UUID uuid) {
        this.lastActivatedBy = uuid;
        markDirty();
    }

    public UUID getLastActivatedBy() { return lastActivatedBy; }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("activator")) this.lastActivatedBy = nbt.getUuid("activator");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (lastActivatedBy != null) nbt.putUuid("activator", lastActivatedBy);
    }
}
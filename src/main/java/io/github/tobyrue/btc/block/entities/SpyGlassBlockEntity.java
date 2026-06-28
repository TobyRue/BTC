package io.github.tobyrue.btc.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class SpyGlassBlockEntity extends BlockEntity {
    private float pitch = 0.0F;
    private float yaw = 0.0F;

    public static final float MAX_YAW_LIMIT = 45.0F;
    public static final float MAX_PITCH_LIMIT = 35.0F;

    public SpyGlassBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPY_GLASS_BLOCK_ENTITY, pos, state);
    }

    public float getPitch() { return this.pitch; }
    public float getYaw() { return this.yaw; }

    public void setAngles(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.markDirty();

        if (this.world != null && this.world.isClient) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
        }
    }

    public void syncAngles(float pitch, float yaw) {
        this.pitch = pitch;
        this.yaw = yaw;
        this.markDirty();

        if (this.world != null && this.world.isClient) {
            this.world.updateListeners(this.pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putFloat("Pitch", this.pitch);
        nbt.putFloat("Yaw", this.yaw);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.pitch = nbt.getFloat("Pitch");
        this.yaw = nbt.getFloat("Yaw");
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
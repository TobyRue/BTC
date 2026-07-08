package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BonfireSyncPayload(NbtCompound bonfireData) implements CustomPayload {
    public static final Id<BonfireSyncPayload> ID = new CustomPayload.Id<>(BTC.identifierOf("bonfire_sync"));

    public static final PacketCodec<RegistryByteBuf, BonfireSyncPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, BonfireSyncPayload::bonfireData,
            BonfireSyncPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
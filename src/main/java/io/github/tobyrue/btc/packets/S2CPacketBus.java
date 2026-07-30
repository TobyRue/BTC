package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record S2CPacketBus(String value) implements CustomPayload {
    public static final Id<S2CPacketBus> ID = new Id<>(BTC.identifierOf("s2cpacketbus"));

    public static final PacketCodec<RegistryByteBuf, S2CPacketBus> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, S2CPacketBus::value,
            S2CPacketBus::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
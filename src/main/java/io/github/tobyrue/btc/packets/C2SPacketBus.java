package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record C2SPacketBus(String value) implements CustomPayload {
    public static final Id<C2SPacketBus> ID = new Id<>(BTC.identifierOf("c2spacketbus"));

    public static final PacketCodec<RegistryByteBuf, C2SPacketBus> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, C2SPacketBus::value,
            C2SPacketBus::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
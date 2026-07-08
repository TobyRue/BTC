package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record OpenFavoritePayload(String empty) implements CustomPayload {
    public static final Id<OpenFavoritePayload> ID = new Id<>(BTC.identifierOf("open_favorite"));

     public static final PacketCodec<RegistryByteBuf, OpenFavoritePayload> CODEC = PacketCodec.tuple(
             PacketCodecs.STRING, OpenFavoritePayload::empty,
             OpenFavoritePayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

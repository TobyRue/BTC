package io.github.tobyrue.btc.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OpenFavoritePayload(String empty) implements CustomPayload {
    public static final Id<OpenFavoritePayload> ID = new Id<>(ModPackets.OPEN_FAV);

     public static final PacketCodec<RegistryByteBuf, OpenFavoritePayload> CODEC = PacketCodec.tuple(
             PacketCodecs.STRING, OpenFavoritePayload::empty,
             OpenFavoritePayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

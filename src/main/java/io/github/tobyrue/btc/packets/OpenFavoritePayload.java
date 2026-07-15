package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import java.util.ArrayList;
import java.util.List;

public record OpenFavoritePayload(List<String> spellNames, List<String> spellIds) implements CustomPayload {
    public static final Id<OpenFavoritePayload> ID = new Id<>(BTC.identifierOf("open_favorite"));

    public static final PacketCodec<RegistryByteBuf, OpenFavoritePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), OpenFavoritePayload::spellNames,
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), OpenFavoritePayload::spellIds,
            OpenFavoritePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
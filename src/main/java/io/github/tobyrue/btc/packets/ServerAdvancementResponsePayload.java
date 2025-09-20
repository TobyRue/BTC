package io.github.tobyrue.btc.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ServerAdvancementResponsePayload(Boolean has, Identifier advancement) implements CustomPayload {
    public static final Id<ServerAdvancementResponsePayload> ID = new Id<>(ModPackets.ADVANCEMENT_RESPONSE_SPELL);

     public static final PacketCodec<RegistryByteBuf, ServerAdvancementResponsePayload> CODEC = PacketCodec.tuple(
             PacketCodecs.BOOL, ServerAdvancementResponsePayload::has,
             Identifier.PACKET_CODEC, ServerAdvancementResponsePayload::advancement,
             ServerAdvancementResponsePayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

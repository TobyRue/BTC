package io.github.tobyrue.btc.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AdvancementPayload(Identifier advancement) implements CustomPayload {
    public static final Id<AdvancementPayload> ID = new Id<>(ModPackets.ADVANCEMENT_SPELL);

     public static final PacketCodec<RegistryByteBuf, AdvancementPayload> CODEC = PacketCodec.tuple(
             Identifier.PACKET_CODEC, AdvancementPayload::advancement,
             AdvancementPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

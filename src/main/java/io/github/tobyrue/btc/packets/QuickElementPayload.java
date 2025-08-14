package io.github.tobyrue.btc.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record QuickElementPayload(Integer slot) implements CustomPayload {
    public static final Id<QuickElementPayload> ID = new Id<>(ModPackets.QUICK_SPELL_PACKET_ID);

     public static final PacketCodec<RegistryByteBuf, QuickElementPayload> CODEC = PacketCodec.tuple(
             PacketCodecs.INTEGER, QuickElementPayload::slot,
             QuickElementPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record MarkPlayerLootedS2CPayload (BlockPos pos, UUID uuid) implements CustomPayload {
    public static final CustomPayload.Id<MarkPlayerLootedS2CPayload> ID = new CustomPayload.Id<>(BTC.identifierOf("mark_looted_sync"));

    public static final PacketCodec<RegistryByteBuf, MarkPlayerLootedS2CPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, MarkPlayerLootedS2CPayload::pos,
            Uuids.PACKET_CODEC, MarkPlayerLootedS2CPayload::uuid,
            MarkPlayerLootedS2CPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

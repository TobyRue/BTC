package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record MobDetectorSyncPayload(
        BlockPos pos,
        List<Integer> entityIds
) implements CustomPayload {

    public static final Id<MobDetectorSyncPayload> ID = new Id<>(BTC.identifierOf("mob_detector_sync"));

    public static final PacketCodec<RegistryByteBuf, MobDetectorSyncPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC, MobDetectorSyncPayload::pos,
                    PacketCodecs.collection(ArrayList::new, PacketCodecs.INTEGER), MobDetectorSyncPayload::entityIds,
                    MobDetectorSyncPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

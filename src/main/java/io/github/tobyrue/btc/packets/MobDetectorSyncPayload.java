package io.github.tobyrue.btc.packets;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record MobDetectorSyncPayload(
        BlockPos pos,
        List<Integer> entityIds
) implements CustomPayload {

    public static final Id<MobDetectorSyncPayload> ID = new Id<>(ModPackets.MOB_DETECTOR_SYNC_ID);

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

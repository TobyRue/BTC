package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.block.entities.StructureCoreBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record StructureCoreUpdatePayload(BlockPos pos, String inputData) implements CustomPayload {
    public static final CustomPayload.Id<StructureCoreUpdatePayload> ID =
            new CustomPayload.Id<>(Identifier.of("btc", "update_structure_core"));

    public static final PacketCodec<PacketByteBuf, StructureCoreUpdatePayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, StructureCoreUpdatePayload::pos,
            PacketCodecs.STRING, StructureCoreUpdatePayload::inputData,
            StructureCoreUpdatePayload::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(StructureCoreUpdatePayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (context.player().getWorld().getBlockEntity(payload.pos()) instanceof StructureCoreBlockEntity core) {
                core.parseAndSetData(payload.inputData());
            }
        });
    }
}
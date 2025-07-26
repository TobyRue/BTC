package io.github.tobyrue.btc.packets;

import com.mojang.datafixers.types.Type;
import io.github.tobyrue.btc.BTC;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public record SetElementPayload(String elementName, UUID uuid) implements CustomPayload {
    public static final CustomPayload.Id<SetElementPayload> ID = new CustomPayload.Id<>(ModPackets.SPELL_PACKET_ID);

     public static final PacketCodec<RegistryByteBuf, SetElementPayload> CODEC = PacketCodec.tuple(
             PacketCodecs.STRING, SetElementPayload::elementName,
             Uuids.PACKET_CODEC, SetElementPayload::uuid,
             SetElementPayload::new
     );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

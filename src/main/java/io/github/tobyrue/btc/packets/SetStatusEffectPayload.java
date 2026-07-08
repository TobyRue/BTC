package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;

public record SetStatusEffectPayload(
        BlockPos pos,
        RegistryEntry<StatusEffect> effect,
        int duration,
        int amplifier
) implements CustomPayload {

    public static final Id<SetStatusEffectPayload> ID =
            new Id<>(BTC.identifierOf("status_effect_sync"));

    public static final PacketCodec<RegistryByteBuf, SetStatusEffectPayload> CODEC =
            PacketCodec.tuple(
                    BlockPos.PACKET_CODEC,
                    SetStatusEffectPayload::pos,

                    PacketCodecs.registryEntry(RegistryKeys.STATUS_EFFECT),
                    SetStatusEffectPayload::effect,

                    PacketCodecs.INTEGER,
                    SetStatusEffectPayload::duration,

                    PacketCodecs.INTEGER,
                    SetStatusEffectPayload::amplifier,

                    SetStatusEffectPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
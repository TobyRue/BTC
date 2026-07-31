package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record ChannelHudPayload(
        boolean active,
        String spellNameKey,
        int currentTick,
        int maxTicks,
        int activeCancelReasonsMask,
        int spellColor,
        int currentHoldTicks,
        int requiredHoldTicks,
        String sourceItemId,
        int spellIndex
) implements CustomPayload {

    public static final CustomPayload.Id<ChannelHudPayload> ID =
            new CustomPayload.Id<>(BTC.identifierOf("channel_hud_sync"));

    public static final PacketCodec<RegistryByteBuf, ChannelHudPayload> CODEC = CustomPayload.codecOf(
            ChannelHudPayload::write,
            ChannelHudPayload::new
    );

    private ChannelHudPayload(RegistryByteBuf buf) {
        this(
                buf.readBoolean(),
                buf.readString(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readInt(),
                buf.readVarInt(),
                buf.readVarInt(),
                buf.readString(),
                buf.readVarInt()
        );
    }

    private void write(RegistryByteBuf buf) {
        buf.writeBoolean(this.active);
        buf.writeString(this.spellNameKey);
        buf.writeVarInt(this.currentTick);
        buf.writeVarInt(this.maxTicks);
        buf.writeVarInt(this.activeCancelReasonsMask);
        buf.writeInt(this.spellColor);
        buf.writeVarInt(this.currentHoldTicks);
        buf.writeVarInt(this.requiredHoldTicks);
        buf.writeString(this.sourceItemId);
        buf.writeVarInt(this.spellIndex);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
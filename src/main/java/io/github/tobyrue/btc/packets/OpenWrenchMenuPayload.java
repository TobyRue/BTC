package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.WrenchType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public record OpenWrenchMenuPayload(Hand hand) implements CustomPayload {
    public static final CustomPayload.Id<OpenWrenchMenuPayload> ID =
            new CustomPayload.Id<>(BTC.identifierOf("open_wrench_menu"));

    public static final PacketCodec<RegistryByteBuf, OpenWrenchMenuPayload> CODEC = CustomPayload.codecOf(
            (value, buf) -> buf.writeEnumConstant(value.hand()),
            buf -> new OpenWrenchMenuPayload(buf.readEnumConstant(Hand.class))
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
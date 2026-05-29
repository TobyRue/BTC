package io.github.tobyrue.btc.regestries;

import com.mojang.serialization.Codec;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.component.BlockPosComponent;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.enums.IWrenchType;
import io.github.tobyrue.btc.enums.WrenchType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Direction;

import java.util.UUID;

public class ModComponents {
    @Deprecated
    public static final ComponentType<Direction> WRENCH_DIRECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_direction"),
            ComponentType.<Direction>builder()
                    .codec(Direction.CODEC)
                    .build()
    );
    public static final ComponentType<WrenchType> WRENCH_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_type"),
            ComponentType.<WrenchType>builder()
                    .codec(WrenchType.CODEC)
                    .build()
    );
    public static final ComponentType<Integer> WRENCH_DELAY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_delay"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .build()
    );
    public static final ComponentType<String> WRENCH_OPERATOR = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_operator"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .build()
    );
    public static final ComponentType<String> WRENCH_CONNECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_connection"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .build()
    );
    public static final ComponentType<WrenchType.WireSubtype> WRENCH_SUBTYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_subtype"),
            ComponentType.<WrenchType.WireSubtype>builder()
                    .codec(WrenchType.WireSubtype.CODEC)
                    .packetCodec(PacketCodecs.indexed(i -> WrenchType.WireSubtype.values()[i], Enum::ordinal))
                    .build()
    );

    public static final ComponentType<WrenchType.WrenchClipboardComponent> WRENCH_CLIPBOARD = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("wrench_clipboard"),
            ComponentType.<WrenchType.WrenchClipboardComponent>builder()
                    .codec(WrenchType.WrenchClipboardComponent.CODEC)
                    .build()
    );

    public static final ComponentType<NbtComponent> SPELL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("spell"),
            ComponentType.<NbtComponent>builder().codec(NbtComponent.CODEC).build()
    );
    //    public static final ComponentType<Identifier> UNLOCK_SPELL_COMPONENT = Registry.register(
//            Registries.DATA_COMPONENT_TYPE,
//            BTC.identifierOf("unlock_spell"),
//            ComponentType.<Identifier>builder().codec(Identifier.CODEC).build()
//    );
//
    public static final ComponentType<UnlockSpellComponent> UNLOCK_SPELL_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("unlock_spell"),
            ComponentType.<UnlockSpellComponent>builder().codec(UnlockSpellComponent.CODEC).build()
    );
    public static final ComponentType<BlockPosComponent> CORNER_1_POSITION_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("corner_1_position"),
            ComponentType.<BlockPosComponent>builder().codec(BlockPosComponent.CODEC).build()
    );
    public static final ComponentType<BlockPosComponent> CORNER_2_POSITION_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("corner_2_position"),
            ComponentType.<BlockPosComponent>builder().codec(BlockPosComponent.CODEC).build()
    );
    public static final ComponentType<Text> KEY_UUID = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("key_uuid"),
            ComponentType.<Text>builder().codec(TextCodecs.STRINGIFIED_CODEC).build()
    );
    public static final ComponentType<Text> PLAYER_NAME = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            BTC.identifierOf("player_name"),
            ComponentType.<Text>builder().codec(TextCodecs.STRINGIFIED_CODEC).build()
    );
    public static final ComponentType<UUID> STORED_MOB_UUID =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    BTC.identifierOf("stored_mob_uuid"),
                    ComponentType.<UUID>builder()
                            .codec(Uuids.CODEC)
                            .packetCodec(Uuids.PACKET_CODEC)
                            .build()
            );
    public static final ComponentType<NbtCompound> STORED_MOB_NBT =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    BTC.identifierOf("stored_mob_nbt"),
                    ComponentType.<NbtCompound>builder()
                            .codec(NbtCompound.CODEC)
                            .packetCodec(PacketCodecs.NBT_COMPOUND)
                            .build()
            );
    public static final ComponentType<EntityType<?>> STORED_ENTITY_TYPE =
            Registry.register(
                    Registries.DATA_COMPONENT_TYPE,
                    BTC.identifierOf("stored_entity_type"),
                    ComponentType.<EntityType<?>>builder()
                            .codec(Registries.ENTITY_TYPE.getCodec())
                            .packetCodec(PacketCodecs.registryValue(RegistryKeys.ENTITY_TYPE))
                            .build()
            );
}

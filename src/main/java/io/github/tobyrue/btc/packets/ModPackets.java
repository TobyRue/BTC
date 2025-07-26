package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.util.EnumHelper;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class ModPackets {
    public static final Identifier SPELL_PACKET_ID = BTC.identifierOf("selected_spell");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(SetElementPayload.ID, SetElementPayload.CODEC);




        ServerPlayNetworking.registerGlobalReceiver(
                SetElementPayload.ID,
                (payload, context) -> {

                    context.server().execute(() -> {
                        PlayerEntity playerByUuid = context.player().getWorld().getPlayerByUuid(payload.uuid());
                        String value = payload.elementName();
                        ItemStack stack = playerByUuid.getMainHandStack();
                        if (stack.isEmpty()) return;

                        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
                        NbtCompound originalNbt = component.copyNbt();

                        originalNbt.putString("Element", value);

                        NbtCompound cooldowns = originalNbt.getCompound("Cooldowns");
                        for (String key : cooldowns.getKeys()) {
                            NbtCompound entry = cooldowns.getCompound(key);
                            if (key.equals(value)) {
                                entry.putBoolean("visible", true);
                            } else {
                                entry.remove("visible");
                            }
                            cooldowns.put(key, entry);
                        }

                        originalNbt.put("Cooldowns", cooldowns);

                        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(originalNbt));

                    });
                });
    }
}

package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.packets.AdvancementPayload;
import io.github.tobyrue.btc.packets.ModPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class AdvancementUtils {
    public static final Map<Identifier, Boolean> advancementCache = new HashMap<>();

    public static boolean hasAdvancement(PlayerEntity player, String namespace, String path) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerWorld world = serverPlayer.getServerWorld();
            AdvancementEntry advancement = world.getServer().getAdvancementLoader().get(Identifier.of(namespace, path));

            if (advancement == null) {
                // Advancement doesn't exist
                return false;
            }

            AdvancementProgress progress = serverPlayer.getAdvancementTracker().getProgress(advancement);
            return progress.isDone();
        } else {
            requestAdvancementCheck(namespace, path);
            return advancementCache.getOrDefault(Identifier.of(namespace, path), false);
        }
    }


    public static void requestAdvancementCheck(String namespace, String path) {
        ClientPlayNetworking.send(new AdvancementPayload(Identifier.of(namespace, path)));
    }
}

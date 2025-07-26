package io.github.tobyrue.btc.util;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class AdvancementUtils {
    public static boolean hasAdvancement(ServerPlayerEntity player, String namespace, String path) {
        ServerWorld world = player.getServerWorld();
        AdvancementEntry advancement = world.getServer().getAdvancementLoader().get(Identifier.of(namespace, path));

        if (advancement == null) {
            // Advancement doesn't exist
            return false;
        }

        AdvancementProgress progress = player.getAdvancementTracker().getProgress(advancement);
        return progress.isDone();
    }
}

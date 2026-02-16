package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ModEvents {
    public static void init() {

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {

            if (!(entity instanceof PlayerEntity player)) return;

            ServerWorld world = (ServerWorld) player.getWorld();

            world.iterateEntities().forEach(e -> {
                if (e instanceof KeyGolemEntity keyGolemEntity) {
                    keyGolemEntity.onPlayerDeath(player, damageSource);
                }
            });

        });
    }
}

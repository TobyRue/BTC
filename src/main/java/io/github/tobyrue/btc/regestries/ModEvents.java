package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

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

        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            NbtCompound left = player.getShoulderEntityLeft();
            NbtCompound right = player.getShoulderEntityRight();
            if (!world.isClient && player.isSneaking() && (isKeyGolem(left) || isKeyGolem(right))) {
                if (isKeyGolem(left)) {
                    player.dropShoulderEntity(player.getShoulderEntityLeft());
                    player.setShoulderEntityLeft(new NbtCompound());
                    if (!isKeyGolem(right)) {
                        return ActionResult.SUCCESS;
                    }
                }
                if (isKeyGolem(right)) {
                    player.dropShoulderEntity(player.getShoulderEntityRight());
                    player.setShoulderEntityRight(new NbtCompound());
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
    private static boolean isKeyGolem(NbtCompound nbt) {
        return nbt != null && nbt.getString("id").equals("btc:key_golem");
    }
}

package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.block.entities.MobDetectorBlockEntity;
import io.github.tobyrue.btc.block.entities.ObsidianChestBlockEntity;
import io.github.tobyrue.btc.misc.StatusEffectHolderBlockEntity;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ModClientPackets {

    public static void initialize() {

        ClientPlayNetworking.registerGlobalReceiver(
                S2CPacketBus.ID, (payload, context) -> {
                    var client = context.client();
                    var player = context.player();
                    var stack = player.getMainHandStack();
                    var item = stack.getItem();

                    switch (payload.value()) {
                        case "" -> {}
                        default -> {}
                    }
                });

        ClientPlayNetworking.registerGlobalReceiver(
                BonfireSyncPayload.ID, (payload, context) -> {
            NbtCompound nbt = payload.bonfireData();
            var client = context.client();
            client.execute(() -> {
                if (client.player instanceof BonfirePlayerData data) {
                    data.bTC$setBonfireData(nbt);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(
                ServerAdvancementResponsePayload.ID, (payload, context) -> {
                    Identifier adv = payload.advancement();
                    boolean has = payload.has();

                    context.client().execute(() -> AdvancementUtils.advancementCache.put(adv, has));
                });

        ClientPlayNetworking.registerGlobalReceiver(
                MobDetectorSyncPayload.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        var world = context.client().world;
                        if (world == null) return;

                        var be = world.getBlockEntity(payload.pos());
                        if (be instanceof MobDetectorBlockEntity detector) {
                            detector.getTrackedEntityIds().clear();
                            detector.getTrackedEntityIds().addAll(payload.entityIds());
                        }
                    });
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                SetStatusEffectPayload.ID, (payload, context) -> {
                    var blockEntity = Objects.requireNonNull(context.client().world).getBlockEntity(payload.pos());

                    if (blockEntity != null) {
                        if (blockEntity instanceof StatusEffectHolderBlockEntity statusEffectHolder) {
                            statusEffectHolder.setEffect(payload.effect(), payload.duration(), payload.amplifier());
                            blockEntity.markDirty();
                        }
                    }
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                MarkPlayerLootedS2CPayload.ID, (payload, context) -> {
                    var uuid = payload.uuid();
                    var world = context.player().getWorld();
                    var pos = payload.pos();
                    if (world.getBlockEntity(pos) instanceof ObsidianChestBlockEntity be && !be.hasPlayerLooted(uuid)) {
                        be.markPlayerLooted(uuid);
                    }
                }
        );
    }
}

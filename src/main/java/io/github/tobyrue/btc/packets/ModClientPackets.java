package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.MobDetectorBlockEntity;
import io.github.tobyrue.btc.block.entities.ObsidianChestBlockEntity;
import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.client.screen.RadialMenuWithPrefixNoHover;
import io.github.tobyrue.btc.client.screen.RadialNoHoverValues;
import io.github.tobyrue.btc.client.screen.RadialValues;
import io.github.tobyrue.btc.item.WrenchItem;
import io.github.tobyrue.btc.misc.StatusEffectHolderBlockEntity;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.UnknownCustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class ModClientPackets {

    public static void initialize() {


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
                OpenFavoritePayload.ID, (payload, context) -> {
                    var client = context.client();
                    var player = context.player();
                    var stack = player.getMainHandStack();
                    var item = stack.getItem();

                    if (item instanceof MinimalPredefinedSpellsItem minimal) {
                        List<RadialNoHoverValues.PrefixValueNoHover> spellValues = new ArrayList<>();

                        for (int i = 0; i < payload.spellNames().size(); i++) {
                            String rawName = payload.spellNames().get(i);
                            String spellId = payload.spellIds().get(i);
                            String key = rawName.replaceAll(".*'([^']+)'.*", "$1");

                            String commandPrefix = "selectspell " + spellId + " {} ";
                            List<RadialNoHoverValues.SuffixValueNoHover> suffixValues =
                                    java.util.stream.IntStream.rangeClosed(0, 5)
                                            .mapToObj(j -> new RadialNoHoverValues.SuffixValueNoHover(
                                                    Text.literal(String.valueOf(j + 1)).formatted(Formatting.BLACK),
                                                    String.valueOf(j)
                                            )).toList();

                            spellValues.add(new RadialNoHoverValues.PrefixValueNoHover(
                                    Text.translatable(key).formatted(Formatting.BLACK),
                                    commandPrefix,
                                    suffixValues
                            ));
                        }

                        int maxSlots = spellValues.size();
                        client.execute(() -> {
                            client.setScreen(new RadialMenuWithPrefixNoHover(
                                    Text.translatable("radial.btc.spell.select_spell"),
                                    new ArrayList<>(spellValues),
                                    0,
                                    Math.min(maxSlots, 6),
                                    new RadialValues.RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb_outline_book.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_book.png"), 215f, BTC.identifierOf("textures/gui/honeycomb_book_sector_"), 180f, 60, 30, 40, 6, false, true, 582, 603, 0.3f),
                                    Text.translatable("radial.btc.spell.select_slot")
                            ));
                        });
                    }
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

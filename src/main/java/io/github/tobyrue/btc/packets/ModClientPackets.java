package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.MobDetectorBlockEntity;
import io.github.tobyrue.btc.block.entities.ObsidianChestBlockEntity;
import io.github.tobyrue.btc.client.screen.RadialMenuWithPrefixNoHover;
import io.github.tobyrue.btc.client.screen.RadialNoHoverValues;
import io.github.tobyrue.btc.client.screen.RadialValues;
import io.github.tobyrue.btc.misc.StatusEffectHolderBlockEntity;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModClientPackets {
    public static final Identifier ADVANCEMENT_RESPONSE_SPELL = BTC.identifierOf("advancement_response");
    public static final Identifier OPEN_FAV = BTC.identifierOf("open_favorite");
    public static final Identifier MOB_DETECTOR_SYNC_ID = BTC.identifierOf("mob_detector_sync");
    public static final Identifier STATUS_EFFECT_SYNC = BTC.identifierOf("status_effect_sync");
    public static final Identifier MARK_LOOTED = BTC.identifierOf("mark_looted_sync");
    public static final Identifier BONFIRE_SYNC_S2C = BTC.identifierOf("bonfire_sync");

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(BonfireSyncPayload.ID, BonfireSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ServerAdvancementResponsePayload.ID, ServerAdvancementResponsePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenFavoritePayload.ID, OpenFavoritePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MobDetectorSyncPayload.ID, MobDetectorSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SetStatusEffectPayload.ID, SetStatusEffectPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MarkPlayerLootedS2CPayload.ID, MarkPlayerLootedS2CPayload.CODEC);

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
                    //TODO crashes on server not on single player
                    var server = client.getServer().getOverworld().getServer();

                    SpellPersistentState spellState = SpellPersistentState.get(server);
                    PlayerSpellData playerData = spellState.getPlayerData(client.player);

                    if (item instanceof MinimalPredefinedSpellsItem minimal) {
                        var spells = PredefinedSpellsItem.getKnownSpells(playerData);

                        var spellValues = spells.stream()
                                .map(inst -> {

                                    String raw = inst.spell().getName(inst.args()).toString();

                                    String key = raw.replaceAll(".*'([^']+)'.*", "$1");

                                    System.out.println("Spell: " + inst.spell() + " Args as NBT: " + GrabBag.toNBT(inst.args()));
                                    String spellId = Spell.getId(inst.spell()).toString();
                                    NbtCompound nbtArgs = GrabBag.toNBT(inst.args());
                                    String commandPrefix = "selectspell " + spellId + " " + nbtArgs + " ";

                                    List<RadialNoHoverValues.SuffixValueNoHover> suffixValues =
                                            java.util.stream.IntStream.rangeClosed(0, 5)
                                                    .mapToObj(i -> new RadialNoHoverValues.SuffixValueNoHover(
                                                            Text.literal(String.valueOf(i + 1)).formatted(Formatting.BLACK),
                                                            String.valueOf(i)  // suffixClick
                                                    ))
                                                    .toList();

                                    return new RadialNoHoverValues.PrefixValueNoHover(
                                            Text.translatable(key, inst.args()).formatted(Formatting.BLACK),
                                            commandPrefix,
                                            suffixValues
                                    );
                                })
                                .toList();

                        int maxSlots = spellValues.size();

                        client.setScreen(new RadialMenuWithPrefixNoHover(
                                Text.translatable("radial.btc.spell.select_spell"),
                                new ArrayList<>(spellValues),
                                0,
                                Math.min(maxSlots, 6),
                                new RadialValues.RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb_outline_book.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_book.png"), 215f, BTC.identifierOf("textures/gui/honeycomb_book_sector_"), 180f, 60, 30, 40, 6, false, true, 582, 603, 0.3f),
                                Text.translatable("radial.btc.spell.select_slot")
                        ));
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

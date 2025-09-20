package io.github.tobyrue.btc.packets;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.AdvancementUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModPackets {

    public static final Identifier SPELL_PACKET_ID = BTC.identifierOf("selected_spell");
    public static final Identifier QUICK_SPELL_PACKET_ID = BTC.identifierOf("quick_spell");
    public static final Identifier ADVANCEMENT_SPELL = BTC.identifierOf("advancement");
    public static final Identifier ADVANCEMENT_RESPONSE_SPELL = BTC.identifierOf("advancement_response");

    public static void initialize() {
        PayloadTypeRegistry.playC2S().register(SetElementPayload.ID, SetElementPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(QuickElementPayload.ID, QuickElementPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(AdvancementPayload.ID, AdvancementPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ServerAdvancementResponsePayload.ID, ServerAdvancementResponsePayload.CODEC);


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
        ServerPlayNetworking.registerGlobalReceiver(
                QuickElementPayload.ID,
                (payload, context) -> {
                    ServerPlayerEntity player = context.player();
                    int slot = payload.slot();

                    context.server().execute(() -> {

                        var stack = player.getMainHandStack();
                        if (stack.getItem() instanceof PredefinedSpellsItem item) {
                            if (player instanceof ServerPlayerEntity serverPlayer) {
                                MinecraftServer server = serverPlayer.getServer();
                                SpellPersistentState spellState = SpellPersistentState.get(server);
                                PlayerSpellData playerData = spellState.getPlayerData(player);
                                var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);
                                if (slot - 1 < spells.size()) {
                                    var spell = spells.get(slot - 1);
                                    if (PredefinedSpellsItem.getKnownSpells(playerData).contains(spell) || player.hasPermissionLevel(2)) {
                                        player.server.getCommandManager().executeWithPrefix(
                                                player.getCommandSource(),
                                                "selectspell " + spell.spell() + " " + GrabBag.toNBT(spell.args())
                                        );
                                    }
                                }
                            }
                        }
                    });
                }
        );
        ServerPlayNetworking.registerGlobalReceiver(
                AdvancementPayload.ID,
                (payload, context) -> {

                    context.server().execute(() -> {
                        var player = context.player();
                        var adv = payload.advancement();
                        var stack = context.player().getMainHandStack();
                        boolean has = AdvancementUtils.hasAdvancement(player, adv.getNamespace(), adv.getPath());

                        ServerPlayNetworking.send(player, new ServerAdvancementResponsePayload(has, adv));
                    });
                });

        ClientPlayNetworking.registerGlobalReceiver(
                ServerAdvancementResponsePayload.ID, (payload, context) -> {
            Identifier adv = payload.advancement();
            boolean has = payload.has();

            context.client().execute(() -> AdvancementUtils.advancementCache.put(adv, has));
        });
    }
}

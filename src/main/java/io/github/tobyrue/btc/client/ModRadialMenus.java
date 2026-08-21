package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.radial_menus.RadialMenu;
import io.github.tobyrue.btc.client.screen.codex.CodexScreen;
import io.github.tobyrue.btc.item.SpellBookItem;
import io.github.tobyrue.btc.item.TestItem;
import io.github.tobyrue.btc.item.WrenchItem;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.wires.WireBlock;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModRadialMenus {
    public static void initializeRadialMenus() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BTCClient.radialMenuKeyBinding.wasPressed()) {
                assert client.player != null;
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;
                    if (!user.isSpectator()) {
                        if (item instanceof MinimalPredefinedSpellsItem minimal && !(item instanceof PredefinedSpellsItem)) {
                            openSpellMenu(user, stack, h, client, minimal,
                                    BTC.identifierOf("textures/gui/honeycomb.png"),
                                    BTC.identifierOf("textures/gui/honeycomb_copper_no_center.png"),
                                    BTC.identifierOf("textures/gui/honeycomb_copper_sector_"),
                                    BTC.identifierOf("textures/gui/copper_title_plate.png")
                            );
                        } else if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            openPredefinedSpellMenu(user, stack, h, client, predefinedSpellsItem);
                        } else if (item instanceof WrenchItem) {
                            openWrenchMenu(stack);
                        }
//                        else if (item instanceof TestItem) {
//                            MinecraftClient.getInstance().setScreen(new CodexScreen());
//                        }
                    }
                }
            }
        });

    }
    public static void openSpellMenu(LivingEntity user, ItemStack stack, Hand hand, MinecraftClient client, MinimalPredefinedSpellsItem minimal, Identifier outline, Identifier base, Identifier sector, Identifier titlePlate) {
        var item = stack.getItem();
        var world = client.world;
        var spells = minimal.getAvailableSpells(stack, world, user);
        var spellValues = spells.stream()
                .map(inst -> {
                    String raw = inst.spell().getName(inst.args()).toString();
                    String key = raw.replaceAll(".*'([^']+)'.*", "$1");
                    return new RadialMenu.RadialValue(Text.translatable(key),
                            (menu, type) -> {
                                if (type == RadialMenu.TriggerType.KEY_RELEASE) {
                                    menu.sendCommand("selectspell " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()));
                                } else {
                                    menu.sendCommand("cast " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()));
                                }
                            }
                    ).withColor(0xFFB47416).enableHoverEffects(true).withHoverFormatting(Formatting.BOLD);
                })
                .toList();

        MinecraftClient.getInstance().setScreen(new RadialMenu(
                Text.translatable("item.btc.spells"),
                spellValues,
                stack,
                BTCClient.radialMenuKeyBinding,
                new RadialMenu.Config(
                        outline,
                        base,
                        sector,
                        titlePlate,
                        200f / 255f, 255f / 255f, 150f / 255f,
                        60,  30, 6, 0.0f, 603, 582, 0.3f,
                        0xFFD67B5B, 100, true
                ),
                false,
                0,
                null,
                0
        ));
    }


    public static void openPredefinedSpellMenu(LivingEntity user, ItemStack stack, Hand hand, MinecraftClient client, PredefinedSpellsItem predefined) {
        var item = stack.getItem();
        var world = client.world;
        MinecraftServer server = client.getServer().getOverworld().getServer();
        SpellPersistentState spellState = SpellPersistentState.get(server);
        PlayerSpellData playerData = spellState.getPlayerData(client.player);
        var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);

        var spellValues = spells.stream()
                .map(inst -> {
                    String raw = inst.spell().getName(inst.args()).toString();
                    String key = raw.replaceAll(".*'([^']+)'.*", "$1");
                    return new RadialMenu.RadialValue(Text.translatable(key),
                            (menu, type) -> {
                                if (type == RadialMenu.TriggerType.KEY_RELEASE) {
                                    menu.sendCommand("selectspell " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()));
                                } else {
                                    menu.sendCommand("cast " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()));
                                }
                            }
                    ).withColor(0xFFB47416).enableHoverEffects(true).withHoverFormatting(Formatting.BOLD);
                })
                .toList();
        MinecraftClient.getInstance().setScreen(new RadialMenu(
                Text.translatable("item.btc.spells"),
                spellValues,
                stack,
                BTCClient.radialMenuKeyBinding,
                0xFFD67B5B,
                true,
                false,
                0
        ));
    }




    public static void openWrenchMenu(ItemStack stack) {

        //CONNECTIONS
        List<RadialMenu.RadialValue> connTypes = new ArrayList<>();
        connTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire connection")));
        for (var type : WireBlock.ConnectionType.values()) {
            connTypes.add(new RadialMenu.RadialValue(Text.translatable("block.btc.wire.connection." + type.asString()),
                    (menu,triggerType) -> menu.sendCommand("btcwrench wire connection " + type.asString())));
        }
        connTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"),  (menu, type) -> menu.goBack()));

        //OPERATOR
        List<RadialMenu.RadialValue> opTypes = new ArrayList<>();
        opTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire operator")));
        for (var op : WireBlock.Operator.values()) {
            opTypes.add(new RadialMenu.RadialValue(Text.translatable("block.btc.wire.operator." + op.asString()),
                    (menu,type) -> menu.sendCommand("btcwrench wire operator " + op.asString())));
        }
        opTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //DELAY
        List<RadialMenu.RadialValue> delayTypes = new ArrayList<>();
        delayTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire delay")));
        for (int i = 0; i <= 7; i++) {
            final int val = i;
            delayTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench wire delay " + val)));
        }
        delayTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //CONNECTION, OPERATOR, AND DELAY, NESTED TOGETHER
        List<RadialMenu.RadialValue> wireOptions = List.of(
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.connections"), connTypes),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.operator"), opTypes),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.delay"), delayTypes),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> selectorOptions = List.of(
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.auto_mode"), (m, t) ->  m.sendCommand("btcwrench selector selector_auto")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_1_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos1")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_2_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos2")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.clear"), (m, t) -> m.sendCommand("btcwrench selector selector_clear")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> fanDepthTypes = new ArrayList<>();
        fanDepthTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan depth")));
        for (int i = 1; i <= 16; i++) {
            final int val = i;
            fanDepthTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan depth " + val)));
        }
        fanDepthTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanBaseRadiusTypes = new ArrayList<>();
        fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan base_radius")));
        fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.literal("0.5"), (menu, type) -> menu.sendCommand("btcwrench fan base_radius 0.5")));
        for (int i = 0; i <= 8; i++) {
            final int val = i;
            fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan base_radius " + val)));
        }
        fanBaseRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanFarRadiusTypes = new ArrayList<>();
        fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan far_radius")));
        for (int i = 1; i <= 12; i++) {
            final int val = i;
            fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan far_radius " + val)));
        }
        fanFarRadiusTypes.add(new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<RadialMenu.RadialValue> fanOptions = List.of(
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.depth"), fanDepthTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.base_radius"), fanBaseRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.far_radius"), fanFarRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.mode"), (m, t) -> m.sendCommand("btcwrench fan mode")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.toggle"), (m, t) -> m.sendCommand("btcwrench fan toggle")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.show_cone"), (m, t) -> m.sendCommand("btcwrench fan show_cone")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<RadialMenu.RadialValue> mainCategories = List.of(
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.rotate"), (m, t) -> m.sendCommand("btcwrench rotate")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.mirror"), (m, t) -> m.sendCommand("btcwrench mirror")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.copy"), (m, t) -> m.sendCommand("btcwrench copy")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.paste"), (m, t) -> m.sendCommand("btcwrench paste")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.wire"), wireOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.selector"), selectorOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.fan"), fanOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new RadialMenu.RadialValue(Text.translatable("item.btc.wrench.close"), (menu, type) -> menu.close()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        MinecraftClient.getInstance().setScreen(new RadialMenu(
                Text.translatable("item.btc.wrench.title.modes"),
                mainCategories,
                stack,
                BTCClient.radialMenuKeyBinding,
                0xFFD67B5B,
                true,
                false,
                0
        ));
    }
}

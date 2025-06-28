package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Arrays;

public class ScreenTestItem extends Item {
    public String string;

    public ScreenTestItem(Settings settings) {
        super(settings);
    }
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
//            Identifier id = BTC.identifierOf("adventure/enter_btc_trial_chambers");
//            var advancement = serverPlayer.getServer().getAdvancementLoader().get(id);
//            if (advancement != null) {
//                var progress = serverPlayer.getAdvancementTracker().getProgress(advancement);
//                if (progress.isDone()) {
//                    BTC.println(serverPlayer.getName().getString() + " has completed " + id);
//                } else {
//                    BTC.println(serverPlayer.getName().getString() + " has NOT completed " + id);
//                }
//            }
//        }
//
//        return TypedActionResult.success(player.getStackInHand(hand));
//    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        if (player.isSneaking()) {
//            if (world.isClient) {
//                MinecraftClient.getInstance().setScreen(new CodexScreen());
//            }
//            return TypedActionResult.success(player.getStackInHand(hand));
//        }
//        return TypedActionResult.pass(player.getStackInHand(hand));
//    }


    {
        try {
            final var parser = new XMLParser<>(Codex.Text.class);
            ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
                var text = message.getContent().getString();
                sender.sendMessage(Text.literal("Git Gud"), true);
                if (text.startsWith("!")) {
                    try {
                        var strings = text.substring(1).split(" ");

                        if (strings.length < 1) {
                            throw new Exception("Missing command after '!'");
                        }

                        var command = strings[0].toLowerCase();
                        var args = Arrays.copyOfRange(strings, 1, strings.length);

                        switch (command) {
                            case "say":
//                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
                                this.string = text.substring(5);
//                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
                                break;
                            default:
                                throw new Exception("Unknown command '" + command + "'");
                        }
                    } catch (Throwable t) {
                        sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
                    }
                }
            });
        } catch (XMLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        System.out.println(SpellRegistryEnum.byId(0).getSpellType().asString());
        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
            final XMLParser<Codex.Text> parser;
            try {
                parser = new XMLParser<>(Codex.Text.class);
            } catch (XMLException e) {
                throw new RuntimeException(e);
            }
            try {
                var otherParser = parser.parse(string);
                var advancement = serverPlayer.getServer().getAdvancementLoader().get(otherParser.getAdvancement());
                if (advancement != null) {
                    var progress = serverPlayer.getAdvancementTracker().getProgress(advancement);
                    boolean inverted = Codex.Text.isInvertedAdvancementText();
                    if ((progress.isDone() && !inverted) || (!progress.isDone() && inverted)) {
                        player.sendMessage(Text.of("Show something"), false);
                        player.sendMessage(otherParser.toText(), false);
                    } else if ((progress.isDone() && inverted) || (!progress.isDone() && !inverted)) {
                        player.sendMessage(Text.of("Don't show something"), false);
                    }
                } else {
                    player.sendMessage(Text.of("Show something because no advancement was found"), false);
                    player.sendMessage(otherParser.toText(), false);
                }
            } catch (XMLException e) {
                throw new RuntimeException(e);
            }
        }

        return TypedActionResult.success(player.getStackInHand(hand));
    }

}

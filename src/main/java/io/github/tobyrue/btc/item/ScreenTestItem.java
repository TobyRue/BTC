package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.AdvancementParser;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.client.screen.SpellScreenTest;
import io.github.tobyrue.btc.client.screen.SpellSelectorScreen;
import io.github.tobyrue.btc.client.screen.codex.Codex;
import io.github.tobyrue.btc.client.screen.codex.CodexScreen;
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
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.io.*;
import java.util.Arrays;

public class ScreenTestItem extends Item {
    public String string;
    private SpellRegistryEnum currentSpell = SpellRegistryEnum.FIREBALL_WEAK;
    private SpellRegistryEnum nextSpell;

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

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\test.xml")) {
            player.sendMessage(Codex.Text.parse(reader));

//            int red = 255;
//            int green = 100;
//            int blue = 200;
//
//            TextColor rgbColor = TextColor.fromRgb((red << 16) | (green << 8) | blue);
//
//            Text message = Text.literal("This is RGB text!")
//                    .setStyle(Style.EMPTY.withColor(rgbColor));
//
//            player.sendMessage(message, false);
//            CodexScreen.codex = Codex.parse(reader);
        } catch (Throwable t) {
            t.printStackTrace();
            player.sendMessage(Text.literal(String.format("[%s]: %s", t.getClass().getSimpleName(), t.getMessage())).formatted(Formatting.RED));
        }
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient.getInstance().setScreen(new CodexScreen());
        });
        return TypedActionResult.success(player.getStackInHand(hand));
    }


//    {
//        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
//            var text = message.getContent().getString();
//            sender.sendMessage(Text.literal("Git Gud"), true);
//            if (text.startsWith("!")) {
//                try {
//                    var strings = text.substring(1).split(" ");
//
//                    if (strings.length < 1) {
//                        throw new Exception("Missing command after '!'");
//                    }
//
//                    var command = strings[0].toLowerCase(Locale.ROOT);
//                    var args = Arrays.copyOfRange(strings, 1, strings.length);
//
//                    switch (command) {
//                        case "say":
//                            this.string = text.substring(5);
////TODO This should replace things in xml file with things that work in parser
////                            InputStream inputStream = CodexScreen.class.getResourceAsStream("/text.xml");
////                            if (inputStream == null) throw new IllegalStateException("Text resource not found!");
////                            String xmlText;
////                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
////                                xmlText = reader.lines().collect(Collectors.joining("\n"));
////                            }
////                            // Do your replacement for the requires attribute values
////                            xmlText = xmlText
////                                    .replace(":", ".")
////                                    .replace("&", "-and-")
////                                    .replace("|", "-or-");
////                            // Now parse the adjusted XML string
////                            Codex.Text parsedText = XMLParser.parse(xmlText, Codex.Text.class);
////                            // Finally send the message
////                            sender.sendMessage(parsedText.toText());
//
////                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
//                            break;
//                        case "run":
//                            var parsed = AdvancementParser.parse(text.substring(5));
//                            System.out.println(String.format("%s: %s", parsed, parsed.evaluate(sender)));
//                            break;
//                        default:
//                            throw new Exception("Unknown command '" + command + "'");
//                    }
//                } catch (Throwable t) {
//                    sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
//                    t.printStackTrace();
//                }
//            }
//        });
//    }
//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//        System.out.println(SpellRegistryEnum.byId(0).getSpellType().asString());
//        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
//            final XMLParser<Codex.Text> parser;
//            try {
//                parser = new XMLParser<>(Codex.Text.class);
//            } catch (XMLException e) {
//                throw new RuntimeException(e);
//            }
//            try {
//                var otherParser = parser.parse(string);
//                var advancement = serverPlayer.getServer().getAdvancementLoader().get(otherParser.getAdvancement());
//                if (advancement != null) {
//                    var progress = serverPlayer.getAdvancementTracker().getProgress(advancement);
//                    boolean inverted = Codex.Text.isInvertedAdvancementText();
//                    if ((progress.isDone() && !inverted) || (!progress.isDone() && inverted)) {
//                        player.sendMessage(Text.of("Show something"), false);
//                        player.sendMessage(otherParser.toText(), false);
//                    } else if ((progress.isDone() && inverted) || (!progress.isDone() && !inverted)) {
//                        player.sendMessage(Text.of("Don't show something"), false);
//                    }
//                } else {
//                    player.sendMessage(Text.of("Show something because no advancement was found"), false);
//                    player.sendMessage(otherParser.toText(), false);
//                }
//            } catch (XMLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return TypedActionResult.success(player.getStackInHand(hand));
//    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
//
////        if (player instanceof ServerPlayerEntity) {
////            player.sendMessage(Text.literal("Current spell: " + currentSpell.asString()), false);
////            nextSpell = SpellRegistryEnum.nextUnlockedOrCurrent((ServerPlayerEntity) player, currentSpell);
////            player.sendMessage(Text.literal("Next available spell: " + nextSpell.asString()), false);
////            currentSpell = nextSpell;
////        }
//        if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
//            final XMLParser<Codex.Text> parser;
//
//            try {
//                parser = new XMLParser<>(Codex.Text.class);
//            } catch (XMLException e) {
//                throw new RuntimeException(e);
//            }
//
//            try {
//                var otherParser = parser.parse(string);
//
//                // use new method here — fully handles null requires, inversion, multi-condition logic
//                if (otherParser.requirementMet(serverPlayer)) {
//                    player.sendMessage(Text.of("Requirement met! Showing text..."), false);
//                    player.sendMessage(otherParser.toText(), false);
//                } else {
//                    player.sendMessage(Text.of("Requirement NOT met!"), false);
//                }
//
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return TypedActionResult.success(player.getStackInHand(hand));
//    }
}

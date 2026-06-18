package io.github.tobyrue.btc.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collection;

public class ApplyRandomNameCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("applyrandomname")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.argument("namePatternJson", StringArgumentType.greedyString())
                                .executes(context -> {
                                    ServerCommandSource source = context.getSource();
                                    Collection<? extends Entity> targets = EntityArgumentType.getEntities(context, "targets");
                                    String jsonString = StringArgumentType.getString(context, "namePatternJson");

                                    JsonArray patterns;
                                    try {
                                        patterns = JsonParser.parseString(jsonString).getAsJsonArray();
                                    } catch (Exception e) {
                                        source.sendError(Text.literal("Invalid JSON format supplied. Must be a valid JSON array."));
                                        return 0;
                                    }

                                    int affectedEntities = 0;

                                    for (Entity entity : targets) {
                                        MutableText finalCustomName = Text.empty();

                                        for (int i = 0; i < patterns.size(); i++) {
                                            JsonObject segment = patterns.get(i).getAsJsonObject();
                                            JsonArray pool = segment.getAsJsonArray("pool");

                                            if (pool == null || pool.isEmpty()) continue;

                                            JsonElement randomElement = pool.get(source.getWorld().getRandom().nextInt(pool.size()));

                                            String pickedText = "";
                                            String separator = "";

                                            String activeColor = segment.has("color") ? segment.get("color").getAsString() : null;
                                            boolean activeBold = segment.has("bold") && segment.get("bold").getAsBoolean();
                                            boolean activeItalic = segment.has("italic") && segment.get("italic").getAsBoolean();
                                            boolean activeUnderlined = segment.has("underlined") && segment.get("underlined").getAsBoolean();

                                            if (randomElement.isJsonObject()) {
                                                JsonObject choiceObj = randomElement.getAsJsonObject();
                                                pickedText = choiceObj.has("text") ? choiceObj.get("text").getAsString() : "";

                                                if (choiceObj.has("separator")) separator = choiceObj.get("separator").getAsString();
                                                if (choiceObj.has("color")) activeColor = choiceObj.get("color").getAsString();
                                                if (choiceObj.has("bold")) activeBold = choiceObj.get("bold").getAsBoolean();
                                                if (choiceObj.has("italic")) activeItalic = choiceObj.get("italic").getAsBoolean();
                                                if (choiceObj.has("underlined")) activeUnderlined = choiceObj.get("underlined").getAsBoolean();
                                            } else {
                                                pickedText = randomElement.getAsString();
                                                if (segment.has("separator")) separator = segment.get("separator").getAsString();
                                            }

                                            MutableText fullSegmentText = Text.empty();
                                            if (i > 0 && !separator.isEmpty()) {
                                                fullSegmentText.append(Text.literal(separator));
                                            }
                                            fullSegmentText.append(Text.literal(pickedText));

                                            if (activeColor != null) {
                                                Formatting color = Formatting.byName(activeColor.toUpperCase());
                                                if (color != null) fullSegmentText.formatted(color);
                                            }
                                            if (activeBold) fullSegmentText.formatted(Formatting.BOLD);
                                            if (activeItalic) fullSegmentText.formatted(Formatting.ITALIC);
                                            if (activeUnderlined) fullSegmentText.formatted(Formatting.UNDERLINE);

                                            finalCustomName.append(fullSegmentText);
                                        }

                                        entity.setCustomName(finalCustomName);
                                        entity.setCustomNameVisible(true);
                                        affectedEntities++;
                                    }

                                    int finalAffected = affectedEntities;
                                    source.sendFeedback(() -> Text.literal("Applied distinct choice pattern names to " + finalAffected + " targets."), true);
                                    return affectedEntities;
                                })
                        )
                )
        );
    }
}
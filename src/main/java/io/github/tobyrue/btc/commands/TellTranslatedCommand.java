package io.github.tobyrue.btc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;

import java.util.Collection;

public class TellTranslatedCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("telltranslated")
                .requires(source -> source.hasPermissionLevel(2))

                .then(CommandManager.argument("targets", EntityArgumentType.players())

                        .then(CommandManager.literal("chat")
                                .then(CommandManager.argument("key", StringArgumentType.string())
                                        .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), null, DisplayType.CHAT))
                                        .then(CommandManager.argument("format", StringArgumentType.greedyString())
                                                .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "format"), DisplayType.CHAT)))))

                        .then(CommandManager.literal("actionbar")
                                .then(CommandManager.argument("key", StringArgumentType.string())
                                        .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), null, DisplayType.ACTIONBAR))
                                        .then(CommandManager.argument("format", StringArgumentType.greedyString())
                                                .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "format"), DisplayType.ACTIONBAR)))))

                        .then(CommandManager.literal("title")
                                .then(CommandManager.argument("key", StringArgumentType.string())
                                        .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), null, DisplayType.TITLE))
                                        .then(CommandManager.argument("format", StringArgumentType.greedyString())
                                                .executes(context -> sendTranslated(context.getSource(), EntityArgumentType.getPlayers(context, "targets"), StringArgumentType.getString(context, "key"), StringArgumentType.getString(context, "format"), DisplayType.TITLE)))))
                )
        );
    }

    private enum DisplayType { CHAT, ACTIONBAR, TITLE }

    private static int sendTranslated(ServerCommandSource source, Collection<ServerPlayerEntity> targets, String key, String format, DisplayType type) {
        Language language = Language.getInstance();
        if (!language.hasTranslation(key)) {
            source.sendError(Text.literal("Translation key not found: " + key));
            return 0;
        }

        MutableText translatedText = Text.literal(language.get(key));

        if (format != null && !format.isEmpty()) {
            Formatting formatting = Formatting.byName(format.trim().toUpperCase());
            if (formatting != null) {
                translatedText.formatted(formatting);
            }
        }

        int successCount = 0;

        for (ServerPlayerEntity player : targets) {
            try {
                switch (type) {
                    case CHAT -> player.sendMessage(translatedText, false);
                    case ACTIONBAR -> player.sendMessage(translatedText, true);
                    case TITLE -> player.networkHandler.sendPacket(new TitleS2CPacket(translatedText));
                }
                successCount++;
            } catch (Exception ignored) {

            }
        }

        return successCount;
    }
}
package io.github.tobyrue.btc.regestries;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.*;

public class ModCommands {
    public static void initialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("foo").then(argument("value", IntegerArgumentType.integer()))
                .executes(context -> {
                    // For versions below 1.19, replace "Text.literal" with "new LiteralText".
                    // For versions below 1.20, remode "() ->" directly.
                    context.getSource().sendFeedback(() -> Text.literal("Called /foo with no arguments"), false);

                    return 1;
                })));
    }
}

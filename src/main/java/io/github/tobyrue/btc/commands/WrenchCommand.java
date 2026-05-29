package io.github.tobyrue.btc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.WrenchItem;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WrenchCommand {
    private static final SimpleCommandExceptionType NOT_HOLDING_WRENCH = new SimpleCommandExceptionType(Text.translatable("command.btc.wrench.error.no_item"));

    private static final SuggestionProvider<ServerCommandSource> OPERATOR_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(Arrays.stream(WireBlock.Operator.values()).map(WireBlock.Operator::asString), builder);
    private static final SuggestionProvider<ServerCommandSource> CONNECTION_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(Arrays.stream(WireBlock.ConnectionType.values()).map(WireBlock.ConnectionType::asString), builder);
    private static final SuggestionProvider<ServerCommandSource> DELAY_SUGGESTIONS = (context, builder) -> {
        for (int i = 0; i <= 7; i++) {
            builder.suggest(String.valueOf(i));
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var baseCommand = literal("btcwrench").executes(ctx -> setType(ctx, WrenchType.NULL)).requires(source -> source.hasPermissionLevel(1));

        for (WrenchType type : WrenchType.values()) {
            if (type == WrenchType.WIRE || type == WrenchType.NULL) continue;
            baseCommand.then(literal(type.asString()).executes(ctx -> setType(ctx, type)));
        }


        baseCommand.then(literal("wire")
                .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.NULL, 0, null, "null"))
                .then(literal("connection")
                        .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.CONNECTION, 0, null, "null")) // Default
                        .then(argument("connection", StringArgumentType.word())
                                .suggests(CONNECTION_SUGGESTIONS)
                                .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.CONNECTION, 0, null, StringArgumentType.getString(ctx, "connection")))))

                .then(literal("delay")
                        .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.DELAY, -1, null, "null")) // Default
                        .then(argument("ticks", IntegerArgumentType.integer(0, 7))
                                .suggests(DELAY_SUGGESTIONS)
                                .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.DELAY, IntegerArgumentType.getInteger(ctx, "ticks"), null, "null"))))

                .then(literal("operator")
                        .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.OPERATOR, 0, "add", "null")) // Default
                        .then(argument("operator", StringArgumentType.word())
                                .suggests(OPERATOR_SUGGESTIONS)
                                .executes(ctx -> setWire(ctx, WrenchType.WireSubtype.OPERATOR, 0, StringArgumentType.getString(ctx, "operator"), "null"))))
        );
        dispatcher.register(baseCommand);
    }

    private static ItemStack getWrench(PlayerEntity player) throws CommandSyntaxException {
        if (player.getMainHandStack().getItem() instanceof WrenchItem) return player.getMainHandStack();
        if (player.getOffHandStack().getItem() instanceof WrenchItem) return player.getOffHandStack();
        throw NOT_HOLDING_WRENCH.create();
    }

    private static int setType(CommandContext<ServerCommandSource> context, WrenchType type) throws CommandSyntaxException {
        ItemStack wrench = getWrench(context.getSource().getPlayerOrThrow());
        wrench.set(ModComponents.WRENCH_TYPE, type);
        return 1;
    }

    private static int setWire(CommandContext<ServerCommandSource> context, WrenchType.WireSubtype subtype, int delay, String operator, String connection) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack wrench = getWrench(player);

        wrench.remove(ModComponents.WRENCH_DELAY);
        wrench.remove(ModComponents.WRENCH_OPERATOR);
        wrench.remove(ModComponents.WRENCH_CONNECTION);

        wrench.set(ModComponents.WRENCH_TYPE, WrenchType.WIRE);
        wrench.set(ModComponents.WRENCH_SUBTYPE, subtype);

        if (subtype == WrenchType.WireSubtype.DELAY) {
            wrench.set(ModComponents.WRENCH_SUBTYPE, WrenchType.WireSubtype.DELAY);
            wrench.set(ModComponents.WRENCH_DELAY, delay);
        } else if (subtype == WrenchType.WireSubtype.OPERATOR) {
            wrench.set(ModComponents.WRENCH_SUBTYPE, WrenchType.WireSubtype.OPERATOR);
            wrench.set(ModComponents.WRENCH_OPERATOR, operator);
        } else if (subtype == WrenchType.WireSubtype.CONNECTION) {
            wrench.set(ModComponents.WRENCH_SUBTYPE, WrenchType.WireSubtype.CONNECTION);
            wrench.set(ModComponents.WRENCH_CONNECTION, connection);
        }

        player.getInventory().markDirty();

        return 1;
    }
}
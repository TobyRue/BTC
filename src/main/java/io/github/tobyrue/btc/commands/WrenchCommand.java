package io.github.tobyrue.btc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.tobyrue.btc.component.BlockPosComponent;
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
import net.minecraft.world.World;

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

        baseCommand.then(literal("selector")
                .executes(ctx -> setSelector(ctx, WrenchType.SelectorSubtype.NULL))
                .then(literal("selector_auto").executes(ctx -> setSelector(ctx, WrenchType.SelectorSubtype.AUTO)))
                .then(literal("selector_pos1").executes(ctx -> setSelector(ctx, WrenchType.SelectorSubtype.POS1)))
                .then(literal("selector_pos2").executes(ctx -> setSelector(ctx, WrenchType.SelectorSubtype.POS2)))
                .then(literal("selector_clear").executes(ctx -> setSelector(ctx, WrenchType.SelectorSubtype.CLEAR)))
        );

        baseCommand.then(literal("fan")
                .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.SHOW_CONE, 0, 0, 0))
                .then(literal("depth")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.DEPTH, -1.0, 0, 0))
                        .then(argument("value", IntegerArgumentType.integer(1, 16))
                                .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.DEPTH, (double) IntegerArgumentType.getInteger(ctx, "value"), -1.0, -1.0))))
                .then(literal("base_radius")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.BASE_RADIUS, 0, -1.0, 0))
                        .then(argument("value", IntegerArgumentType.integer(0, 8))
                                .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.BASE_RADIUS, -1.0, (double) IntegerArgumentType.getInteger(ctx, "value"), -1.0))))
                .then(literal("far_radius")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.FAR_RADIUS, 0, 0, -1.0))
                        .then(argument("value", IntegerArgumentType.integer(1, 12))
                                .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.FAR_RADIUS, -1.0, -1.0, (double) IntegerArgumentType.getInteger(ctx, "value")))))
                .then(literal("mode")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.MODE, 0, 0, 0)))
                .then(literal("toggle")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.TOGGLE, 0, 0, 0)))
                .then(literal("show_cone")
                        .executes(ctx -> setFan(ctx, WrenchType.FanSubtype.SHOW_CONE, 0, 0, 0)))
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

    private static int setSelector(CommandContext<ServerCommandSource> context, WrenchType.SelectorSubtype subtype) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack wrench = getWrench(player);

        if (subtype == WrenchType.SelectorSubtype.CLEAR) {
            World world = context.getSource().getWorld();
            int clearY = world.getBottomY() - 50;

            wrench.set(ModComponents.CORNER_1_POSITION_COMPONENT, new BlockPosComponent(0, clearY, 0));
            wrench.set(ModComponents.CORNER_2_POSITION_COMPONENT, new BlockPosComponent(0, clearY, 0));
        }

        wrench.remove(ModComponents.WRENCH_DELAY);
        wrench.remove(ModComponents.WRENCH_OPERATOR);
        wrench.remove(ModComponents.WRENCH_CONNECTION);
        wrench.remove(ModComponents.WRENCH_SUBTYPE);

        wrench.set(ModComponents.WRENCH_TYPE, WrenchType.SELECTOR);
        wrench.set(ModComponents.WRENCH_SELECTOR_SUBTYPE, subtype);

        player.getInventory().markDirty();
        return 1;
    }
    private static int setFan(CommandContext<ServerCommandSource> context, WrenchType.FanSubtype subtype, double depth, double baseRad, double farRad) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack wrench = getWrench(player);

        wrench.set(ModComponents.WRENCH_TYPE, WrenchType.FAN);
        wrench.set(ModComponents.WRENCH_FAN_SUBTYPE, subtype);

        if (depth != 0) wrench.set(ModComponents.WRENCH_FAN_DEPTH, depth);
        if (baseRad != 0) wrench.set(ModComponents.WRENCH_FAN_BASE_RADIUS, baseRad);
        if (farRad != 0) wrench.set(ModComponents.WRENCH_FAN_FAR_RADIUS, farRad);

        player.getInventory().markDirty();
        return 1;
    }


}
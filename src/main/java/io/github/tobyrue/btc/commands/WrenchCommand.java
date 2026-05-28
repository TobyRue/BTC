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
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WrenchCommand {
    private static final SimpleCommandExceptionType NOT_HOLDING_WRENCH = new SimpleCommandExceptionType(Text.literal("You must be holding a wrench!"));

    private static final SuggestionProvider<ServerCommandSource> WRENCH_TYPE_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(Arrays.stream(WrenchType.values()).map(WrenchType::asString), builder);

    private static final SuggestionProvider<ServerCommandSource> DIRECTION_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(Arrays.stream(Direction.values()).map(Direction::asString), builder);

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("btcwrench")
                .requires(source -> source.hasPermissionLevel(1))

                .then(literal("type")
                        .then(argument("wrench_type", StringArgumentType.word())
                                .suggests(WRENCH_TYPE_SUGGESTIONS)
                                .executes(context -> {
                                    String typeStr = StringArgumentType.getString(context, "wrench_type");
                                    WrenchType type = Arrays.stream(WrenchType.values())
                                            .filter(e -> e.asString().equals(typeStr))
                                            .findFirst().orElse(WrenchType.ROTATE);
                                    return setType(context, type);
                                })))
        );
    }

    private static int setType(CommandContext<ServerCommandSource> context, WrenchType type) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack stack = player.getMainHandStack();
        ItemStack stackO = player.getOffHandStack();
        if (!(stack.getItem() instanceof WrenchItem) && !(stackO.getItem() instanceof WrenchItem)) throw NOT_HOLDING_WRENCH.create();

        stack.set(ModComponents.WRENCH_TYPE, type);
        context.getSource().sendFeedback(() -> Text.translatable("item.btc.wrench.set.type", Text.translatable("item.btc.wrench.type." + type.asString())), false);
        return 1;
    }

    private static int setDirection(CommandContext<ServerCommandSource> context, Direction dir) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack stack = player.getMainHandStack();
        ItemStack stackO = player.getOffHandStack();
        if (!(stack.getItem() instanceof WrenchItem) && !(stackO.getItem() instanceof WrenchItem)) throw NOT_HOLDING_WRENCH.create();

        stack.set(ModComponents.WRENCH_TYPE, WrenchType.WIRE_COMPLEX);
        stack.set(ModComponents.WRENCH_DIRECTION, dir);
        context.getSource().sendFeedback(() -> Text.translatable("item.btc.wrench.set.wire_complex", Text.translatable( "block.btc.wire.face." + dir.asString())), false);
        return 1;
    }
}
package io.github.tobyrue.btc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.tobyrue.btc.misc.StatusEffectHolderBlockEntity;
import io.github.tobyrue.btc.packets.SetStatusEffectPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.EffectCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class SetStatusEffectCommand {
    private static final SimpleCommandExceptionType NO_VALID_BLOCK_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.btc.block_effect.no_block"));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->  dispatcher.register(
                CommandManager.literal("setstatuseffect")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .then(CommandManager.argument("effect", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.STATUS_EFFECT))
                                        .executes(context -> execute(
                                                context.getSource(),
                                                BlockPosArgumentType.getBlockPos(context, "pos"),
                                                RegistryEntryReferenceArgumentType.getStatusEffect(context, "effect"),
                                                null,
                                                0
                                        ))
                                        .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1, 1000000))
                                                .executes(context -> execute(
                                                        context.getSource(),
                                                        BlockPosArgumentType.getBlockPos(context, "pos"),
                                                        RegistryEntryReferenceArgumentType.getStatusEffect(context, "effect"),
                                                        IntegerArgumentType.getInteger(context, "seconds"),
                                                        0
                                                ))
                                                .then(CommandManager.argument("amplifier", IntegerArgumentType.integer(0, 255))
                                                        .executes(context -> execute(
                                                                context.getSource(),
                                                                BlockPosArgumentType.getBlockPos(context, "pos"),
                                                                RegistryEntryReferenceArgumentType.getStatusEffect(context, "effect"),
                                                                IntegerArgumentType.getInteger(context, "seconds"),
                                                                IntegerArgumentType.getInteger(context, "amplifier")
                                                        ))
                                                )
                                        )
                                )
                        )
        ));
    }

    private static int execute(
            ServerCommandSource source,
            BlockPos pos,
            RegistryEntry<StatusEffect> effect,
            Integer seconds,
            int amplifier
    ) throws CommandSyntaxException {

        StatusEffect statusEffect = effect.value();

        int duration;
        if (seconds != null) {
            if (statusEffect.isInstant()) {
                duration = seconds;
            } else {
                duration = seconds * 20;
            }
        } else {
            duration = statusEffect.isInstant() ? 1 : 600;
        }

        var blockEntity = source.getWorld().getBlockEntity(pos);

        if (blockEntity instanceof StatusEffectHolderBlockEntity statusEffectHolder && source.isExecutedByPlayer()) {
            statusEffectHolder.setEffect(effect, duration, amplifier);
            ServerPlayNetworking.send(
                    Objects.requireNonNull(source.getPlayer()),
                    new SetStatusEffectPayload(pos, effect, duration, amplifier)
            );
            blockEntity.markDirty();
            return 1;
        }

        if (!(blockEntity instanceof StatusEffectHolderBlockEntity)) {
            throw NO_VALID_BLOCK_EXCEPTION.create();
        }
        return 0;
    }
}
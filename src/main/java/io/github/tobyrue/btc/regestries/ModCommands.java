package io.github.tobyrue.btc.regestries;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.spell.*;
import io.github.tobyrue.xml.util.Nullable;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.*;

public class ModCommands {
    public static final SuggestionProvider<ServerCommandSource> SPELLS_SUGGESTIONS = SuggestionProviders.register(BTC.identifierOf("spells"), (context, builder) -> CommandSource.suggestFromIdentifier(ModRegistries.SPELL.stream(), builder, Spell::getId, Spell::getName));
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.literal("Item does not support spells / Just failed"));
    private static final SimpleCommandExceptionType FAILED_SPELL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("command.btc.common.no_such_spell"));
    private static final SimpleCommandExceptionType FAILED_ARGS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("command.btc.common.bad_nbt"));
    public static final SuggestionProvider<ServerCommandSource> DIGIT_SUGGESTIONS = (context, builder) -> {
        for (int i = 0; i <= 9; i++) {
            builder.suggest(String.valueOf(i));
        }
        return builder.buildFuture();
    };

    public static void initialize() {
        ArgumentTypeRegistry.registerArgumentType(
                BTC.identifierOf("spell"),
                SpellArgumentType.class,
                ConstantArgumentSerializer.of(SpellArgumentType::new)
        );
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                    literal("selectspell")
                    .then(
                            argument("spell", SpellArgumentType.spell())
                            .suggests((context, builder) -> {
                                if (context.getSource().hasPermissionLevel(2)) {
                                    return ModCommands.SPELLS_SUGGESTIONS.getSuggestions(context, builder);
                                } else if (context.getSource().getEntity() instanceof LivingEntity l && l.getStackInHand(Hand.MAIN_HAND) instanceof ItemStack stack && stack.getItem() instanceof PredefinedSpellsItem item) {
                                    return CommandSource.suggestMatching(item.getAvailableSpells(stack, context.getSource().getWorld(), l).stream().map(i -> Spell.getId(i.spell()).toString()).toList(), builder);
                                } else {
                                    return CommandSource.suggestMatching(List.of(), builder);
                                }
                            })
                            .executes(context -> selectSpell(context.getSource(), SpellArgumentType.getSpell(context, "spell"), new NbtCompound(), null))
                            .then(
                                    argument("args", NbtElementArgumentType.nbtElement())
                                    .suggests((context, builder) -> {
                                        // Suggest some example NBT compounds
                                        if (context.getSource().getEntity() instanceof LivingEntity l && l.getStackInHand(Hand.MAIN_HAND) instanceof ItemStack stack && stack.getItem() instanceof PredefinedSpellsItem item) {
                                            return CommandSource.suggestMatching(item.getAvailableSpells(stack, context.getSource().getWorld(), l).stream().filter(i -> i.spell() == SpellArgumentType.getSpell(context, "spell")).map(i -> GrabBag.toNBT(i.args()).toString()).toList(), builder);
                                        } else {
                                            return CommandSource.suggestMatching(List.of(), builder);
                                        }
                                    })
                                    .executes(context -> selectSpell(context.getSource(), SpellArgumentType.getSpell(context, "spell"), NbtElementArgumentType.getNbtElement(context, "args"), null))
                                    .then(
                                            argument("slot", IntegerArgumentType.integer(0, 9))
                                            .suggests(ModCommands.DIGIT_SUGGESTIONS)
                                            .executes(context -> selectSpell(context.getSource(), SpellArgumentType.getSpell(context, "spell"), NbtElementArgumentType.getNbtElement(context, "args"), IntegerArgumentType.getInteger(context, "slot")))
                                    )
                            )
                    )
            ));

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("cast")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                argument("spell", SpellArgumentType.spell())
                                        .suggests(ModCommands.SPELLS_SUGGESTIONS)
                                        .executes(context -> castSpell(context.getSource(), SpellArgumentType.getSpell(context, "spell"), null))
                                        .then(
                                                argument("args", NbtElementArgumentType.nbtElement())
                                                        .executes(context -> castSpell(context.getSource(), SpellArgumentType.getSpell(context, "spell"), NbtElementArgumentType.getNbtElement(context, "args")))
                                        )
                        )
        ));
    }

    private static int selectSpell(final ServerCommandSource source, final Spell spell, @Nullable final NbtElement nbt, @Nullable final Integer slot) throws CommandSyntaxException {
        System.out.println("[DEBUG] selectSpell called with: spell=" + (spell == null ? "null" : spell.getPureName()) + ", nbt=" + nbt + ", slot=" + slot);

        if (source.getEntity() instanceof LivingEntity entity && entity.getStackInHand(Hand.MAIN_HAND) instanceof ItemStack stack && stack.getItem() instanceof SpellItem item) {
            System.out.println("[DEBUG] Entity is living + holding a SpellItem: " + stack);

            var data = item.getSpellDataStore(stack);
            if (spell != null) {
                // Admin override
                if (nbt instanceof NbtCompound compound) {
                    var args = GrabBag.fromNBT(compound);
                    if (source.hasPermissionLevel(2)) {
                        System.out.println("[DEBUG] Source has permission level 2");
                        if (source.getPlayer() != null) {
                            try {
                                var player = source.getPlayer();
                                MinecraftServer server = source.getWorld().getServer();
                                SpellPersistentState spellState = SpellPersistentState.get(server);
                                PlayerSpellData playerData = spellState.getPlayerData(player);
                                if (slot != null) {
                                    PredefinedSpellsItem.addFavoriteSpellWithIndex(player, spellState, new Spell.InstancedSpell(spell, args), slot);
                                    System.out.println("[DEBUG] Fav Spell set by admin: " + spell.getPureName() + " with args " + GrabBag.fromNBT(compound));
                                    return 1;
                                }
                            } catch (Exception e) {
                                System.out.println("[DEBUG][ERROR] Failed to set spell: " + e.getMessage());
                                e.printStackTrace();
                                throw FAILED_EXCEPTION.create();
                            }
                        }
                        data.setSpell(spell, GrabBag.fromNBT(compound));
                        System.out.println("[DEBUG] Spell set by admin: " + spell.getPureName());
                        return 1;
                    } else {
                        // Non-admin path
                        System.out.println("[DEBUG] Non-admin args parsed: " + args);

                        if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            // Player path
                            if (entity instanceof PlayerEntity) {
                                var player = source.getPlayer();
                                MinecraftServer server = source.getWorld().getServer();
                                SpellPersistentState spellState = SpellPersistentState.get(server);
                                PlayerSpellData playerData = spellState.getPlayerData(player);

                                System.out.println("[DEBUG] Player path, known spells size: " + PredefinedSpellsItem.getKnownSpells(playerData).size());

                                if (slot != null) {
                                    System.out.println("[DEBUG] Adding to favorites at slot " + slot);
                                    boolean found = PredefinedSpellsItem.getKnownSpells(playerData).stream()
                                            .anyMatch(inst -> inst.spell() == spell && inst.args().equalsOther(args));

                                    if (found) {
                                        PredefinedSpellsItem.addFavoriteSpellWithIndex(player, spellState, new Spell.InstancedSpell(spell, args), slot);
                                        System.out.println("[DEBUG] Added spell " + spell.getPureName() + " to favorites");
                                        return 1;
                                    } else {
                                        System.out.println("[DEBUG] Spell not found in known spells");
                                        throw FAILED_EXCEPTION.create();
                                    }
                                } else {
                                    boolean found = PredefinedSpellsItem.getKnownSpells(playerData).stream()
                                            .anyMatch(inst -> inst.spell() == spell && inst.args().equalsOther(args));

                                    if (found) {
                                        data.setSpell(spell, args);
                                        System.out.println("[DEBUG] Spell set directly on item: " + spell.getPureName());
                                        return 1;
                                    } else {
                                        System.out.println("[DEBUG] Spell not found in known spells (no slot)");
                                        throw FAILED_EXCEPTION.create();
                                    }
                                }
                            } else {
                                System.out.println("[DEBUG] Non-player entity path (e.g. mob/command block)");
                                boolean found = predefinedSpellsItem.getAvailableSpells(stack, source.getWorld(), entity).stream()
                                        .anyMatch(inst -> inst.spell() == spell && inst.args().equalsOther(args));

                                if (found) {
                                    data.setSpell(spell, args);
                                    System.out.println("[DEBUG] Spell set from available spells: " + spell.getPureName());
                                    return 1;
                                } else {
                                    System.out.println("[DEBUG] Spell not found in available spells for entity");
                                    throw FAILED_EXCEPTION.create();
                                }
                            }
                        } else {
                            System.out.println("[DEBUG] Item is not a PredefinedSpellsItem");
                            throw FAILED_EXCEPTION.create();
                        }
                    }
                } else {
                    throw FAILED_EXCEPTION.create();
                }
            } else {
                System.out.println("[DEBUG] Spell was null");
                throw FAILED_SPELL_EXCEPTION.create();
            }
        } else {
            System.out.println("[DEBUG] Entity not holding a valid SpellItem");
            throw FAILED_EXCEPTION.create();
        }
    }

    private static int castSpell(final ServerCommandSource source, final Spell spell, @Nullable final NbtElement nbt) throws CommandSyntaxException {
        if (spell != null) {
            if (nbt instanceof NbtCompound || nbt == null) {
                var args = nbt instanceof NbtCompound compound ? GrabBag.fromNBT(compound) : GrabBag.empty();
                var data = new SpellDataStore() {
                    @Override
                    public Spell getSpell() {
                        return spell;
                    }

                    @Override
                    public GrabBag getArgs() {
                        return args;
                    }

                    @Override
                    public void setSpell(Spell spell, GrabBag args) {}

                    @Override
                    public int getCooldown(Spell.SpellCooldown cooldown) {
                        return 0;
                    }

                    @Override
                    public float getCooldownPercent(Spell.SpellCooldown cooldown) {
                        return 0;
                    }

                    @Override
                    public void setCooldown(Spell.SpellCooldown cooldown) {}
                };
                return spell.tryUse(new Spell.SpellContext(source.getWorld(), source.getPosition(), Vec3d.fromPolar(source.getRotation()), data, source.getEntity() instanceof LivingEntity l ? l : null), args) ? 1 : 0;
            } else {
                throw FAILED_ARGS_EXCEPTION.create();
            }
        } else {
            throw FAILED_SPELL_EXCEPTION.create();
        }
    }
}

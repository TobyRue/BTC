package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.SpellScreenTest;
import io.github.tobyrue.btc.client.screen.codex.Codex;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.SpellItem;
import io.github.tobyrue.btc.util.AdvancementUtils;
import io.github.tobyrue.xml.util.Nullable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.io.*;
import java.util.*;

public class ScreenTestItem extends PredefinedSpellsItem {
    public String string;
    private final SpellRegistryEnum currentSpell = SpellRegistryEnum.FIREBALL_WEAK;
    private SpellRegistryEnum nextSpell;

    public ScreenTestItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        final var stack = player.getStackInHand(hand);

        System.out.println(this.getAvailableSpells(stack, world, player));
        if (!player.isSneaking()) {
            if (this.tryUseSpell(world, player.getEyePos(), player.getRotationVec(1.0F).normalize(), player, stack)) {
                return TypedActionResult.success(stack);
            } else {
                return TypedActionResult.fail(stack);
            }
        }//        System.out.println("Current spell: "+ getElement(player.getStackInHand(hand)) + " Client: " + world.isClient);
//        try (var reader = new FileReader("C:\\Users\\tobin\\IdeaProjects\\BTC\\test.xml")) {
//            player.sendMessage(Codex.Text.parse(reader));
//            SpellScreenTest.string = this.string;
//        } catch (Throwable t) {
//            t.printStackTrace();
//            player.sendMessage(Text.literal(String.format("[%s]: %s", t.getClass().getSimpleName(), t.getMessage())).formatted(Formatting.RED));
//        }
//        MinecraftClient.getInstance().execute(() -> {
//            MinecraftClient.getInstance().setScreen(new SpellScreenTest());
//        });
        return TypedActionResult.success(player.getStackInHand(hand));
    }


    {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            var text = message.getContent().getString();
            sender.sendMessage(Text.literal("Git Gud"), true);
            if (text.startsWith("!")) {
                try {
                    var strings = text.substring(1).split(" ");

                    if (strings.length < 1) {
                        throw new Exception("Missing command after '!'");
                    }

                    var command = strings[0].toLowerCase(Locale.ROOT);
                    var args = Arrays.copyOfRange(strings, 1, strings.length);

                    switch (command) {
                        case "say":
                            this.string = text.substring(5);
                            break;
                        default:
                            throw new Exception("Unknown command '" + command + "'");
                    }
                } catch (Throwable t) {
                    sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
                    t.printStackTrace();
                }
            }
        });
    }

    private SpellRegistryEnum getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (SpellRegistryEnum attack : SpellRegistryEnum.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return SpellRegistryEnum.FIREBALL_WEAK;
    }

    public static void setElement(ItemStack stack, SpellRegistryEnum attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        nbt.putString("Element", attack.asString());

        // Manage cooldown bar visibility on element swap
        NbtCompound cooldowns = nbt.getCompound("Cooldowns");
        String activeKey = attack.getCooldownKey();

        boolean found = false;
        for (String key : cooldowns.getKeys()) {
            NbtCompound entry = cooldowns.getCompound(key);
            if (key.equals(activeKey)) {
                entry.putBoolean("visible", true);
                found = true;
            } else {
                entry.remove("visible");
            }
            cooldowns.put(key, entry);
        }

        // If no active cooldown for new element, hide all bars
        if (!found) {
            for (String key : cooldowns.getKeys()) {
                NbtCompound entry = cooldowns.getCompound(key);
                entry.remove("visible");
                cooldowns.put(key, entry);
            }
        }
        nbt.put("Cooldowns", cooldowns);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static void addSpell(ServerPlayerEntity player, List<InstancedSpell> spellList, @Nullable Identifier id, InstancedSpell spell) {
        boolean exists = spellList.stream()
                .anyMatch(s -> s.spell() == spell.spell() && s.args() == spell.args());
        if (id != null) {
            if (AdvancementUtils.hasAdvancement(player, id.getNamespace(), id.getPath())) {
                if (!exists) {
                    spellList.add(spell);
                }
            }
        } else {
            if (!exists) {
                spellList.add(spell);
            }
        }
    }

    @Override
    public List<InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity) {
       List<InstancedSpell> s = new ArrayList<>();

        // Always ensure default WATER_WAVE exists

//        return List.of(new InstancedSpell[] {
//                new InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 1);}})),
//                new InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 5);}})),
//                new InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
//                    put("aimingForgiveness", 0.3d);
//                    put("range", 24d);
//                    put("duration", 400);
//                    put("amplifier", 4);
//                    put("cooldown", 600);
//                }})),
//                new InstancedSpell(ModSpells.WATER_WAVE, GrabBag.fromMap(new HashMap<>() {{
//                    put("maxRadius", 8d);
//                    put("maxDuration", 600);
//                    put("duration", 2);
//                    put("amplifier", 1);
//                    put("cooldown", 600);
//                }})),
//        });

        if (entity instanceof ServerPlayerEntity serverPlayer) {
            addSpell(serverPlayer, s, BTC.identifierOf("adventure/get_earth_spike_scroll"), new InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
                put("spikeCount", 8);
                put("yRange", 12);
                put("cooldown", 400);
            }})));
            addSpell(serverPlayer, s, null, new InstancedSpell(ModSpells.WATER_WAVE, GrabBag.fromMap(new HashMap<>() {{
                put("maxRadius", 8d);
                put("maxDuration", 600);
                put("duration", 2);
                put("amplifier", 1);
                put("cooldown", 600);
            }})));
            addSpell(serverPlayer, s, null, new InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 1);}})));
            addSpell(serverPlayer, s, null, new InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 5);}})));
            addSpell(serverPlayer, s, null, new InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("aimingForgiveness", 0.3d);
                put("range", 24d);
                put("duration", 400);
                put("amplifier", 4);
                put("cooldown", 600);
            }})));

        }
        return s;
    }
}

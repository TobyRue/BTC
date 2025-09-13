package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellItem;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;

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

        if (player instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            SpellPersistentState spellState = SpellPersistentState.get(server);
            PlayerSpellData playerData = spellState.getPlayerData(serverPlayer);
            if (getKnownSpells(playerData).isEmpty()) {
                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                    put("level", 1);
                    put("cooldown", 0);
                }})), null);

                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{
                    put("level", 5);
                    put("cooldown", 0);
                }})), null);

                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                    put("aimingForgiveness", 0.3d);
                    put("range", 24d);
                    put("duration", 400);
                    put("amplifier", 4);
                    put("cooldown", 0);
                }})), null);

                addKnownSpell(serverPlayer, spellState, new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
                    put("spikeCount", 8);
                    put("yRange", 12);
                    put("cooldown", 0);
                }})), BTC.identifierOf("adventure/get_earth_spike_scroll"));
            }
        }

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

    @Override
    public List<Spell.InstancedSpell> getAvailableSpells(ItemStack stack, World world, LivingEntity entity) {
       List<Spell.InstancedSpell> s = new ArrayList<>();
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            addSpellToItem(serverPlayer, s, BTC.identifierOf("adventure/get_earth_spike_scroll"), new Spell.InstancedSpell(ModSpells.EARTH_SPIKE_LINE, GrabBag.fromMap(new HashMap<>() {{
                put("spikeCount", 8);
                put("yRange", 12);
                put("cooldown", 0);
            }})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.WATER_WAVE, GrabBag.fromMap(new HashMap<>() {{
                put("maxRadius", 8d);
                put("maxDuration", 600);
                put("duration", 2);
                put("amplifier", 1);
                put("cooldown", 0);
            }})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 1); put("cooldown", 0);}})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.FIREBALL, GrabBag.fromMap(new HashMap<>() {{put("level", 5); put("cooldown", 0);}})));
            addSpellToItem(serverPlayer, s, null, new Spell.InstancedSpell(ModSpells.ICE_BLOCK, GrabBag.fromMap(new HashMap<>() {{
                put("aimingForgiveness", 0.3d);
                put("range", 24d);
                put("duration", 400);
                put("amplifier", 4);
                put("cooldown", 0);
            }})));

        }
        return s;
    }
}

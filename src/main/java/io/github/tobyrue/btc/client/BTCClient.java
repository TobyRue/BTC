package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.client.screen.HexagonRadialMenu;
import io.github.tobyrue.btc.client.screen.codex.CodexScreen;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.ScreenTestItem;
import io.github.tobyrue.btc.item.SpellstoneItem;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.packets.QuickElementPayload;
import io.github.tobyrue.btc.packets.SetElementPayload;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.BTCModelLoadingPlugin;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.EnumHelper;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.screen.option.MouseOptionsScreen;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BTCClient implements ClientModInitializer {
    public static KeyBinding keyBinding;
    public static KeyBinding keyBinding1;


    public static final EntityModelLayer WIND_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "wind_staff"), "main");
    public static final EntityModelLayer FIRE_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "fire_staff"), "main");
    public static final EntityModelLayer DRAGON_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "dragon_staff"), "main");
    public static final EntityModelLayer WATER_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "water_staff"), "main");
    public static final EntityModelLayer EARTH_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "earth_staff"), "main");

    public static final EntityModelLayer BOOK_LAYER = new EntityModelLayer(Identifier.of("btc", "spell_book"), "main");



//    static {
//        try {
//            final var parser = new XMLParser<>(Codex.Text.class);
//            ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
//                var text = message.getContent().getString();
//                sender.sendMessage(Text.literal("Git Gud"), true);
//                if (text.startsWith("!")) {
//                    try {
//                        var strings = text.substring(1).split(" ");
//
//                        if (strings.length < 1) {
//                            throw new Exception("Missing commandHover after '!'");
//                        }
//
//                        var commandHover = strings[0].toLowerCase(Locale.ROOT);
//                        var args = Arrays.copyOfRange(strings, 1, strings.length);
//
//                        switch (commandHover) {
//                            case "say":
////                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
//
////                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
//                                sender.sendMessage(parser.parse(text.substring(5)).toText());
//                                break;
//                            default:
//                                throw new Exception("Unknown commandHover '" + commandHover + "'");
//                        }
//                    } catch (Throwable t) {
//                        sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
//                    }
//                }
//            });
//        } catch (XMLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public void onInitializeClient() {

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.open_spellbook", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_Z, // The keycode of the key
                "category.btc.spell" // The translation key of the keybinding's category.
        ));

        keyBinding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.quick_spell", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
                "category.btc.spell" // The translation key of the keybinding's category.
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                assert client.player != null;
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;

                    if (item instanceof MinimalPredefinedSpellsItem minimal) {
                        var spells = minimal.getAvailableSpells(stack, world, user);

                        // Convert the available spells into SpellValue objects
                        var spellValues = spells.stream()
                                .map(inst -> {
                                    // Get the raw string (includes translation{key='...', args=[...]})
                                    String raw = inst.spell().getName(inst.args()).toString();

                                    // Extract just the translation key with regex
                                    String key = raw.replaceAll(".*'([^']+)'.*", "$1");

                                    return new HexagonRadialMenu.SpellValue(
                                            Text.translatable(key), // pass inst.args() so it renders correctly
                                            "selectspell " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()),
                                            "cast " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args())
                                    );
                                })
                                .toList();

                        int maxSlots = spellValues.size(); // dynamically adjust max slots

                        client.setScreen(new HexagonRadialMenu(
                                Text.of("radial menu"),
                                new ArrayList<>(spellValues),
                                0, // starting index
                                maxSlots
                        ));
                    }

//                    if (client.player.getStackInHand(h).getItem() == ModItems.TEST) {
//                        client.setScreen(new HexagonRadialMenu(Text.of("radial menu"),  new ArrayList<>(List.of(
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell fireball"), "selectspell btc:fireball {level:1}", "cast btc:fireball {level:1}"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell fireball 2"), "selectspell btc:fireball {level:5}", "cast btc:fireball {level:5}"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell fire storm"), "selectspell btc:fire_storm", "cast btc:fire_storm"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell water wave"), "selectspell btc:water_wave", "cast btc:water_wave"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell earth spike"), "selectspell btc:earth_spike_line", "cast btc:earth_spike_line"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell water blast"), "selectspell btc:water_blast", "cast btc:water_blast"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell dragons breath"), "selectspell btc:dragon_fireball", "cast btc:dragon_fireball"),
//                                new HexagonRadialMenu.SpellValue(Text.translatable("spell clustered wind charge"), "selectspell btc:cluster_wind_charge", "cast btc:cluster_wind_charge")
//                        )), 0, 6));
//                    }
                }
            }
        });



        ModelPredicateProviderRegistry.register(ModItems.UNLOCK_SCROLL, Identifier.ofVanilla("texture"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof UnlockScrollItem item && stack.contains(BTC.UNLOCK_SPELL_COMPONENT)) {
                        var number = Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).textureInt();
                        return (number / 100f);
                    }
                    return 0;
                });

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof UnlockScrollItem item && tintIndex == 1 && stack.contains(DataComponentTypes.DYED_COLOR) && stack.contains(BTC.UNLOCK_SPELL_COMPONENT)) {
                if (Objects.requireNonNull(stack.get(BTC.UNLOCK_SPELL_COMPONENT)).textureInt() == 0) {
                    return Objects.requireNonNull(stack.get(DataComponentTypes.DYED_COLOR)).rgb();
                }
            }
            return 0xFFFFFFFF;
        }, ModItems.UNLOCK_SCROLL);


        ModelPredicateProviderRegistry.register(ModItems.SPELLSTONE, Identifier.ofVanilla("spelltype"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof SpellstoneItem item && item.getSpellDataStore(stack).getSpell() instanceof Spell spell) {
                        return Math.round((spell.getSpellType().ordinal() / (float) (SpellTypes.values().length - 1)) * 100f) / 100f;
                    }
                    return 0; // no spell
                });


        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof SpellstoneItem spellstoneItem) {
                var data = spellstoneItem.getSpellDataStore(stack);
                var spell = data.getSpell();

                if (spell != null && tintIndex == 1) {
                    return 0xFF000000 | spell.getColor(data.getArgs());
                } else if (tintIndex == 1) {
                    return 0x00000000;
                }
                if (spell == null && tintIndex == 2) {
                    return 0x00000000;
                }
            }

            return 0xFFFFFFFF; // base always visible
        }, ModItems.SPELLSTONE);

//        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
//            if (stack.getItem() instanceof SpellstoneItem spellstoneItem) {
//                var data = spellstoneItem.getSpellDataStore(stack);
//                var spell = data.getSpell();
//
//                if (spell != null) {
//                    int color = spell.getColor(data.getArgs());
//                    SpellTypes type = spell.getSpellType();
//                    System.out.println("Spell: " + spell + ", Type: " + type + ", TintIndex: " + tintIndex + ", Color: " + color);
//
//                    // Determine the correct overlay layer index for this spell type
//                    int activeLayer = switch (type) {
//                        case GENERIC -> 1;
//                        case FIRE    -> 2;
//                        case WIND    -> 3;
//                        case WATER   -> 4;
//                        case EARTH   -> 5;
//                        case ENDER   -> 6;
//                    };
//
//                    // If the current layer matches the spell type, return the ARGB color
//                    if (tintIndex == activeLayer) {
//                        return 0xFF000000 | color; // Force alpha to FF (fully opaque)
//                    } else if (tintIndex >= 1 && tintIndex <= 6) {
//                        return 0x00000000; // Transparent for all other overlays
//                    }
//                } else {
//                    if (tintIndex >= 1 && tintIndex <= 6) {
//                        return 0x00000000; // Transparent for all other overlays
//                    }
//                }
//            }
//
//            // layer0 base always visible (or any unexpected layer)
//            return 0xFFFFFFFF;
//        }, ModItems.SPELLSTONE);


        DrowningEffectOverlay.register();
        ModelLoadingPlugin.register(new BTCModelLoadingPlugin());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MELTING_ICE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_FIRE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FIRE_DISPENSER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTIER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_DOOR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KEY_DISPENSER_BLOCK, RenderLayer.getCutoutMipped());
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.KEY_DISPENSER_ENTITY, KeyDispenserBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.KEY_ACCEPTOR_ENTITY, KeyAcceptorBlockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(WIND_STAFF_LAYER, WindStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WIND_STAFF_LAYER);
            new WindStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });
        EntityModelLayerRegistry.registerModelLayer(FIRE_STAFF_LAYER, FireStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.FIRE_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(FIRE_STAFF_LAYER);
            new FireStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });
        EntityModelLayerRegistry.registerModelLayer(DRAGON_STAFF_LAYER, DragonStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.DRAGON_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(DRAGON_STAFF_LAYER);
            new DragonStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityModelLayerRegistry.registerModelLayer(WATER_STAFF_LAYER, WaterStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WATER_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WATER_STAFF_LAYER);
            new WaterStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityModelLayerRegistry.registerModelLayer(EARTH_STAFF_LAYER, EarthStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.EARTH_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EARTH_STAFF_LAYER);
            new EarthStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityModelLayerRegistry.registerModelLayer(BOOK_LAYER, SpellBookModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPELL_BOOK, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(BOOK_LAYER);
            new SpellBookModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityRendererRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryRenderer::new);
        EntityRendererRegistry.register(ModEntities.WATER_BLAST, WaterBlastEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.EARTH_SPIKE, EarthSpikeRenderer::new);
        EntityRendererRegistry.register(ModEntities.CREEPER_PILLAR, CreeperPillarRenderer::new);
        EntityRendererRegistry.register(ModEntities.WIND_TORNADO, WindTornadoEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ELDRITCH_LUMINARY, EldritchLuminaryModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WATER_BURST, WaterBlastEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.COPPER_GOLEM, CopperGolemModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TUFF_GOLEM, TuffGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.EARTH_SPIKE, EarthSpikeModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CREEPER_PILLAR, CreeperPillarModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WIND_TORNADO, WindTornadoEntityModel::getTexturedModelData);
    }
}

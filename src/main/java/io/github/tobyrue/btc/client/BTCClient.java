package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellstoneItem;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.packets.SetElementPayload;
import io.github.tobyrue.btc.regestries.BTCModelLoadingPlugin;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.EnumHelper;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Arrays;
import java.util.Objects;

@Environment(EnvType.CLIENT)
public class BTCClient implements ClientModInitializer {

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
//                            throw new Exception("Missing command after '!'");
//                        }
//
//                        var command = strings[0].toLowerCase(Locale.ROOT);
//                        var args = Arrays.copyOfRange(strings, 1, strings.length);
//
//                        switch (command) {
//                            case "say":
////                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
//
////                                sender.sendMessage(XMLParser.parse(new InputStreamReader(Objects.requireNonNull(CodexScreen.class.getResourceAsStream("/text.xml"))), Codex.Text.class).toText());
//                                sender.sendMessage(parser.parse(text.substring(5)).toText());
//                                break;
//                            default:
//                                throw new Exception("Unknown command '" + command + "'");
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

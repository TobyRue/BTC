package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.GunpowderBarrelBlock;
import io.github.tobyrue.btc.block.GunpowderDustBlock;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.block.fluids.ModFluids;
import io.github.tobyrue.btc.client.screen.RadialMenu;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.item.*;
import io.github.tobyrue.btc.packets.ModClientPackets;
import io.github.tobyrue.btc.player_data.PlayerSpellData;
import io.github.tobyrue.btc.player_data.SpellPersistentState;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.PredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.util.ClientOreRadar;
import io.github.tobyrue.btc.util.UnlockScrollCache;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.WireBlockEntityRenderer;
import io.github.tobyrue.rtc.RTC;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import io.github.tobyrue.btc.client.screen.RadialValues.*;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Environment(EnvType.CLIENT)
public class BTCClient implements ClientModInitializer {
    public static KeyBinding radialMenuKeyBinding;
    private static KeyBinding spellKeyBind1;
    private static KeyBinding spellKeyBind2;
    private static KeyBinding spellKeyBind3;

    public static final EntityModelLayer WIND_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "wind_staff"), "main");
    public static final EntityModelLayer FIRE_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "fire_staff"), "main");
    public static final EntityModelLayer DRAGON_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "dragon_staff"), "main");
    public static final EntityModelLayer WATER_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "water_staff"), "main");
    public static final EntityModelLayer EARTH_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "earth_staff"), "main");
    public static final SimpleParticleType WATER_BLAST = FabricParticleTypes.simple();
    public static final SimpleParticleType WATER_DROP = FabricParticleTypes.simple();

    public static final EntityModelLayer BOOK_LAYER = new EntityModelLayer(Identifier.of("btc", "spell_book"), "main");



    static {
        AtomicReference<File> file = new AtomicReference<>();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
                var text = message.getContent().getString();
                try {
                    if (text.trim().equals("!scratch")) {
                        if (file.get() != null) {
                            file.get().delete();
                        }
                        file.set(File.createTempFile("scratch-", ".java"));
                        file.get().deleteOnExit();
                        Files.writeString(file.get().toPath(), """
                        import net.minecraft.server.network.ServerPlayerEntity;
                        
                        class Scratch {
                            public static Object run(ServerPlayerEntity player) {
                                
                            }
                        }
                        """);
                        try {
                            java.awt.Desktop.getDesktop().edit(file.get());
                        } catch (Throwable ignored) {}
                        sender.sendMessage(Text.literal("Created scratch file: " + file.get().toPath()));
                    } else if (text.trim().equals("!run")) {
                        var value = RTC.run(Files.readString(file.get().toPath()), "Scratch.run", sender);
                        sender.sendMessage(Text.literal(String.valueOf(value)));
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    sender.sendMessage(Text.literal("Error: ").setStyle(Style.EMPTY.withColor(0xFF0000)).append(Text.literal(t.toString())));
                }
            });
        }
    }

    @Override
    public void onInitializeClient() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            MinecraftClient client = MinecraftClient.getInstance();

            if (client.world == null) return;

            RecipeManager recipes = client.world.getRecipeManager();
            RegistryWrapper.WrapperLookup registries = client.world.getRegistryManager();

            for (RecipeEntry<?> recipe : recipes.values()) {
                ItemStack result = recipe.value().getResult(registries);

                if (result.isOf(ModItems.UNLOCK_SCROLL)) {
                    entries.add(result);
                }
            }
        });
        FluidRenderHandlerRegistry.INSTANCE.register(
                ModFluids.TOXIC_SLUDGE_SOURCE,
                ModFluids.FLOWING_TOXIC_SLUDGE,
                new SimpleFluidRenderHandler(
                        BTC.identifierOf("block/toxic_sludge_still"),
                        BTC.identifierOf("block/toxic_sludge_flow"),
                        BTC.identifierOf("block/toxic_sludge_overlay"),
                        0xFF553B0C
                )
        );
        BlockRenderLayerMap.INSTANCE.putFluids(
                RenderLayer.getTranslucent(),
                ModFluids.TOXIC_SLUDGE_SOURCE,
                ModFluids.FLOWING_TOXIC_SLUDGE
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (SpyGlassCameraController.isActive() && client.player != null) {
                SpyGlassCameraController.updateTelescopeRotation();
                if (client.options.sneakKey.isPressed()) {
                    SpyGlassCameraController.stopZooming();
                }
            }
        });
//        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
//            if (SpyGlassCameraController.isActive()) {
//                int width = drawContext.getScaledWindowWidth();
//                int height = drawContext.getScaledWindowHeight();
//
//                int scaledSize = Math.min(width, height);
//                int x = (width - scaledSize) / 2;
//                int y = (height - scaledSize) / 2;
//
//                Identifier scopeTexture = Identifier.of("minecraft", "textures/misc/spyglass_scope.png");
//
//                drawContext.drawTexture(
//                        scopeTexture,
//                        x, y,
//                        0.0F, 0.0F,
//                        scaledSize, scaledSize,
//                        scaledSize, scaledSize
//                );
//
//                if (x > 0) {
//                    drawContext.fill(0, 0, x, height, 0xFF000000);
//                    drawContext.fill(x + scaledSize, 0, width, height, 0xFF000000);
//                }
//                if (y > 0) {
//                    drawContext.fill(0, 0, width, y, 0xFF000000);
//                    drawContext.fill(0, y + scaledSize, width, height, 0xFF000000);
//                }
//            }
//        });


        WorldRenderEvents.AFTER_SETUP.register(context -> {
            ClientLensHandler.setActiveFrustum(context.frustum());
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientOreRadar.clientTick();
        });
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(BTC.MOD_ID, "water_blast"), WATER_BLAST);
        ParticleFactoryRegistry.getInstance().register(WATER_BLAST, GustParticle.Factory::new);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(BTC.MOD_ID, "water_drop"), WATER_DROP);
        ParticleFactoryRegistry.getInstance().register(WATER_DROP, FlameParticle.Factory::new);

        ModClientPackets.initialize();
        spellKeyBind1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.spell1",
                InputUtil.UNKNOWN_KEY.getCode(),
                "category.btc.spell"
        ));


        spellKeyBind2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.spell2",
                InputUtil.UNKNOWN_KEY.getCode(),
                "category.btc.spell"
        ));
        spellKeyBind3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.spell3",
                InputUtil.UNKNOWN_KEY.getCode(),
                "category.btc.spell"
        ));


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            assert client.player != null;
            while (spellKeyBind1.wasPressed()) {
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;
                    if (!user.isSpectator()) {
                        if (item instanceof MinimalPredefinedSpellsItem minimal && !(item instanceof PredefinedSpellsItem)) {
                            var spells = minimal.getAvailableSpells(stack, world, user);
                            var spell1 = spells.get(0);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell1.spell()) + " " + GrabBag.toNBT(spell1.args()));
                        } else if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            MinecraftServer server = client.getServer().getOverworld().getServer();
                            SpellPersistentState spellState = SpellPersistentState.get(server);
                            PlayerSpellData playerData = spellState.getPlayerData(client.player);
                            var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);
                            var spell1 = spells.get(0);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell1.spell()) + " " + GrabBag.toNBT(spell1.args()));
                        }
                    }
                }
            }
            while (spellKeyBind2.wasPressed()) {
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;
                    if (!user.isSpectator()) {
                        if (item instanceof MinimalPredefinedSpellsItem minimal && !(item instanceof PredefinedSpellsItem)) {
                            var spells = minimal.getAvailableSpells(stack, world, user);
                            var spell2 = spells.get(1);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell2.spell()) + " " + GrabBag.toNBT(spell2.args()));
                        } else if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            MinecraftServer server = client.getServer().getOverworld().getServer();
                            SpellPersistentState spellState = SpellPersistentState.get(server);
                            PlayerSpellData playerData = spellState.getPlayerData(client.player);
                            var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);
                            var spell2 = spells.get(1);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell2.spell()) + " " + GrabBag.toNBT(spell2.args()));
                        }
                    }
                }
            }
            while (spellKeyBind3.wasPressed()) {
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;
                    if (!user.isSpectator()) {
                        if (item instanceof MinimalPredefinedSpellsItem minimal && !(item instanceof PredefinedSpellsItem)) {
                            var spells = minimal.getAvailableSpells(stack, world, user);
                            var spell3 = spells.get(2);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell3.spell()) + " " + GrabBag.toNBT(spell3.args()));
                        } else if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            MinecraftServer server = client.getServer().getOverworld().getServer();
                            SpellPersistentState spellState = SpellPersistentState.get(server);
                            PlayerSpellData playerData = spellState.getPlayerData(client.player);
                            var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);
                            var spell3 = spells.get(2);
                            client.player.networkHandler.sendCommand("cast " + Spell.getId(spell3.spell()) + " " + GrabBag.toNBT(spell3.args()));
                        }
                    }
                }
            }
        });

        radialMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.btc.open_spellbook", // The translation key of the keybinding's name
                GLFW.GLFW_KEY_LEFT_ALT, // The keycode of the key
                "category.btc.spell" // The translation key of the keybinding's category.
        ));

        //TODO add another keybind that opens the spell book

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (radialMenuKeyBinding.wasPressed()) {
                assert client.player != null;
                for (var h : Hand.values()) {
                    var item = client.player.getStackInHand(h).getItem();
                    var stack = client.player.getStackInHand(h);
                    var world = client.world;
                    var user = client.player;
                    if (!user.isSpectator()) {
                        if (item instanceof MinimalPredefinedSpellsItem minimal && !(item instanceof PredefinedSpellsItem)) {
                            var spells = minimal.getAvailableSpells(stack, world, user);

                            var spellValues = spells.stream()
                                    .map(inst -> {
                                        String raw = inst.spell().getName(inst.args()).toString();

                                        String key = raw.replaceAll(".*'([^']+)'.*", "$1");

                                        return new Value(
                                                Text.translatable(key).formatted(Formatting.BLACK),
                                                "selectspell " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()),
                                                "cast " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args())
                                        );
                                    })
                                    .toList();

                            if (item instanceof SpellBookItem) {
                                client.setScreen(new RadialMenu(
                                        Text.translatable("radial.btc.spell.select_spell"),
                                        spellValues,
                                        radialMenuKeyBinding,
                                        new RadialIdentifiers(
                                                BTC.identifierOf("textures/gui/honeycomb_outline_book.png"),
                                                255f,
                                                BTC.identifierOf("textures/gui/honeycomb_book.png"),
                                                215f,
                                                BTC.identifierOf("textures/gui/honeycomb_book_sector_"),
                                                180f,
                                                60,
                                                30,
                                                40,
                                                6,
                                                false,
                                                true,
                                                582,
                                                603,
                                                0.3f)
                                ));
                            } else {
                                client.setScreen(new RadialMenu(
                                        Text.translatable("radial.btc.spell.select_spell"),
                                        spellValues,
                                        radialMenuKeyBinding,
                                        new RadialIdentifiers(
                                                BTC.identifierOf("textures/gui/honeycomb.png"),
                                                255f,
                                                BTC.identifierOf("textures/gui/honeycomb_gold.png"),
                                                200f,
                                                BTC.identifierOf("textures/gui/honeycomb_gold_sector_"),
                                                150f,
                                                60,
                                                30,
                                                40,
                                                6,
                                                false,
                                                false,
                                                582,
                                                603,
                                                0.3f)
                                ));
                            }
                        } else if (item instanceof PredefinedSpellsItem predefinedSpellsItem) {
                            //TODO crashes on server not on single player
                            MinecraftServer server = client.getServer().getOverworld().getServer();
                            SpellPersistentState spellState = SpellPersistentState.get(server);
                            PlayerSpellData playerData = spellState.getPlayerData(client.player);
                            var spells = PredefinedSpellsItem.getFavoriteSpells(playerData);

                            var spellValues = spells.stream()
                                    .map(inst -> {
                                        String raw = inst.spell().getName(inst.args()).toString();

                                        String key = raw.replaceAll(".*'([^']+)'.*", "$1");

                                        return new Value(
                                                Text.translatable(key),
                                                "selectspell " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args()),
                                                "cast " + Spell.getId(inst.spell()) + " " + GrabBag.toNBT(inst.args())
                                        );
                                    })
                                    .toList();

                            int maxSlots = spellValues.size();

                            client.setScreen(new RadialMenu(
                                    Text.translatable("radial.btc.spell.select_spell"),
                                    new ArrayList<>(spellValues),
                                    0, // starting index
                                    Math.min(maxSlots, 6),
                                    radialMenuKeyBinding
                            ));
                        } else if (item instanceof WrenchItem) {
                            openWrenchMenu(stack);
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
            }
        });



        ModelPredicateProviderRegistry.register(ModItems.UNLOCK_SCROLL, Identifier.ofVanilla("texture"),
                (stack, world, entity, seed) -> {
                    if (stack.getItem() instanceof UnlockScrollItem item && stack.contains(ModComponents.UNLOCK_SPELL_COMPONENT)) {
                        var number = Objects.requireNonNull(stack.get(ModComponents.UNLOCK_SPELL_COMPONENT)).textureInt();
                        return (number / 100f);
                    }
                    return 0;
                });
        ModelPredicateProviderRegistry.register(ModItems.SCOPED_CROSSBOW, Identifier.ofVanilla("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (CrossbowItem.isCharged(stack)) {
                return 0.0f;
            }
            return (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / (float)CrossbowItem.getPullTime(stack, entity);
        });
        ModelPredicateProviderRegistry.register(ModItems.SCOPED_CROSSBOW, Identifier.ofVanilla("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItems.SCOPED_CROSSBOW, Identifier.ofVanilla("charged"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
        ModelPredicateProviderRegistry.register(ModItems.SCOPED_CROSSBOW, Identifier.ofVanilla("firework"), (stack, world, entity, seed) -> {
            ChargedProjectilesComponent chargedProjectilesComponent = stack.get(DataComponentTypes.CHARGED_PROJECTILES);
            return chargedProjectilesComponent != null && chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET) ? 1.0f : 0.0f;
        });

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex != 1) return 0xFFFFFFFF;
            if (!(stack.getItem() instanceof UnlockScrollItem)) return 0xFFFFFFFF;
            if (!stack.contains(ModComponents.UNLOCK_SPELL_COMPONENT)) return 0xFFFFFFFF;

            Spell.InstancedSpell inst = UnlockScrollCache.getCachedSpell(stack);
            if (inst == null || inst.spell() == null) return 0xFFFFFFFF;

            return inst.spell().getColor(inst.args());
        }, ModItems.UNLOCK_SCROLL);


        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getItem() instanceof PetCharmItem) {
                if (stack.contains(ModComponents.STORED_MOB_UUID)) {
                    try {
                        return 0xFF000000 | Integer.parseInt(Objects.requireNonNull(stack.get(ModComponents.STORED_MOB_UUID)).toString(), 0, 6, 16) & 0xFFF0F0F0;
                    } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ignored) {
                    }
                } else {
                    return 0xFF00AA2C;
                }
            }
            return 0xFFFFFFFF;
        }, ModItems.PET_CHARM);


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



        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (stack.getItem() instanceof BlockKeyItem keyItem) {

                if (tintIndex == 1 && stack.contains(ModComponents.KEY_UUID)) {
                    try {
                        return 0xFF000000 | Integer.parseInt(Objects.requireNonNull(stack.get(ModComponents.KEY_UUID)).getString(), 0, 6, 16) & 0xFFF0F0F0;
                    } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ignored) {}
                }
                if (tintIndex == 2) {
                    return 0x30FFFFFF;
                }
            }

            return 0xFFFFFFFF;
        }, ModItems.BLOCK_KEY);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (state.getBlock() instanceof GunpowderDustBlock gunpowder) {
                return gunpowder.getColor(state);
            }
            return -1;
        }, ModBlocks.GUNPOWDER_DUST);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> 0xFF777777, ModBlocks.GUNPOWDER_DUST);

        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
            if (state.getBlock() instanceof GunpowderBarrelBlock gunpowder && tintIndex == 1) {
                return gunpowder.getColor(state);
            }
            return -1;
        }, ModBlocks.GUNPOWDER_BARREL);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> tintIndex == 1 ? 0xFF505050 : -1, ModBlocks.GUNPOWDER_BARREL);

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
//        ModelLoadingPlugin.register(new BTCModelLoadingPlugin());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MELTING_ICE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BRAZIER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_FLAME, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEP_FLAME, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FORTRESS_FLAME, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_DOOR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ITEM_PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOB_DETECTOR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_BARREL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SPY_GLASS_BLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_WIRE, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BELLOW, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.EXPOSED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WEATHERED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WAXED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GUNPOWDER_DUST, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_DUNGEON_GRATE, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SALT_BLOCK, RenderLayer.getCutout());



        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUFF_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUFF_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUFF_BRICK_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUFF_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POLISHED_TUFF_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POLISHED_TUFF_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHISELED_TUFF_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHISELED_TUFF_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STONE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STONE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COBBLESTONE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COBBLESTONE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOSSY_COBBLESTONE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOSSY_COBBLESTONE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STONE_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STONE_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOSSY_STONE_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MOSSY_STONE_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_STONE_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_STONE_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COBBLED_DEEPSLATE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COBBLED_DEEPSLATE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POLISHED_DEEPSLATE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POLISHED_DEEPSLATE_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_TILES_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DEEPSLATE_TILES_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR, RenderLayer.getCutoutMipped());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHISELED_DEEPSLATE_PILASTER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CHISELED_DEEPSLATE_PILLAR, RenderLayer.getCutoutMipped());



        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POTION_PILLAR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWER_PILLAR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWER_PILASTER, RenderLayer.getCutoutMipped());



        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.ITEM_PEDESTAL_BLOCK_ENTITY, ItemPedestalBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.KEY_ACCEPTOR_ENTITY, KeyAcceptorBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.MOB_DETECTOR_BLOCK_ENTITY, MobDetectorBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.POTION_PILLAR_BLOCK_ENTITY, PotionPillarBlockRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.OBSIDIAN_CHEST_BLOCK_ENTITY, ObsidianChestRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WIRE_BLOCK_ENTITY, WireBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.BONFIRE_BLOCK_ENTITY, BonfireBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SPY_GLASS_BLOCK_ENTITY, SpyGlassBlockEntityRenderer::new);


        BlockEntityRendererFactories.register(ModBlockEntities.COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.EXPOSED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WEATHERED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.OXIDIZED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WAXED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WAXED_EXPOSED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WAXED_WEATHERED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WAXED_OXIDIZED_COPPER_FAN_BLOCK_ENTITY, FanBlockEntityRenderer::new);

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
        EntityRendererRegistry.register(ModEntities.SUPER_HAPPY_KILL_BALL, SuperHappyKillBallEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.KEY_GOLEM, KeyGolemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MINE, MineEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TRIAL_CUBE, TrialCubeEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ELDRITCH_LUMINARY, EldritchLuminaryModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WATER_BURST, WaterBlastEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.COPPER_GOLEM, CopperGolemModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TUFF_GOLEM, TuffGolemEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.EARTH_SPIKE, EarthSpikeModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.CREEPER_PILLAR, CreeperPillarModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WIND_TORNADO, WindTornadoEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SHKB, SuperHappyKillBallEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.KEY_GOLEM, KeyGolemModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MINE, MineEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TRIAL_CUBE, TrialCubeEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.FAN_BLADES_LAYER, FanBlockModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SPY_GLASS_BLOCK_LAYER, SpyGlassBlockModel::getTexturedModelData);

    }

    public static void openWrenchMenu(ItemStack stack) {

        //CONNECTIONS
        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> connTypes = new ArrayList<>();
        connTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire connection")));
        for (var type : WireBlock.ConnectionType.values()) {
            connTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("block.btc.wire.connection." + type.asString()),
                    (menu,triggerType) -> menu.sendCommand("btcwrench wire connection " + type.asString())));
        }
        connTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"),  (menu, type) -> menu.goBack()));

        //OPERATOR
        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> opTypes = new ArrayList<>();
        opTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire operator")));
        for (var op : WireBlock.Operator.values()) {
            opTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("block.btc.wire.operator." + op.asString()),
                    (menu,type) -> menu.sendCommand("btcwrench wire operator " + op.asString())));
        }
        opTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //DELAY
        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> delayTypes = new ArrayList<>();
        delayTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench wire delay")));
        for (int i = 0; i <= 7; i++) {
            final int val = i;
            delayTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench wire delay " + val)));
        }
        delayTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));


        //CONNECTION, OPERATOR, AND DELAY, NESTED TOGETHER
        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> wireOptions = List.of(
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.connections"), connTypes),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.operator"), opTypes),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.delay"), delayTypes),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> selectorOptions = List.of(
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.auto_mode"), (m, t) ->  m.sendCommand("btcwrench selector selector_auto")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_1_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos1")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.corner_2_set_mode"), (m, t) -> m.sendCommand("btcwrench selector selector_pos2")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.selector.clear"), (m, t) -> m.sendCommand("btcwrench selector selector_clear")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> fanDepthTypes = new ArrayList<>();
        fanDepthTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan depth")));
        for (int i = 1; i <= 16; i++) {
            final int val = i;
            fanDepthTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan depth " + val)));
        }
        fanDepthTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> fanBaseRadiusTypes = new ArrayList<>();
        fanBaseRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan base_radius")));
        fanBaseRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.literal("0.5"), (menu, type) -> menu.sendCommand("btcwrench fan base_radius 0.5")));
        for (int i = 0; i <= 8; i++) {
            final int val = i;
            fanBaseRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan base_radius " + val)));
        }
        fanBaseRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> fanFarRadiusTypes = new ArrayList<>();
        fanFarRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.cycle"), (menu, type) -> menu.sendCommand("btcwrench fan far_radius")));
        for (int i = 1; i <= 12; i++) {
            final int val = i;
            fanFarRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.literal(String.valueOf(val)), (menu, type) -> menu.sendCommand("btcwrench fan far_radius " + val)));
        }
        fanFarRadiusTypes.add(new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()));

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> fanOptions = List.of(
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.depth"), fanDepthTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.base_radius"), fanBaseRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.fan.far_radius"), fanFarRadiusTypes).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.mode"), (m, t) -> m.sendCommand("btcwrench fan mode")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.toggle"), (m, t) -> m.sendCommand("btcwrench fan toggle")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.fan.show_cone"), (m, t) -> m.sendCommand("btcwrench fan show_cone")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.back"), (menu, type) -> menu.goBack()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        List<io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue> mainCategories = List.of(
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.rotate"), (m, t) -> m.sendCommand("btcwrench rotate")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.mirror"), (m, t) -> m.sendCommand("btcwrench mirror")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.copy"), (m, t) -> m.sendCommand("btcwrench copy")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.type.paste"), (m, t) -> m.sendCommand("btcwrench paste")).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.wire"), wireOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.selector"), selectorOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue.nested(Text.translatable("item.btc.wrench.type.fan"), fanOptions).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD),
                new io.github.tobyrue.btc.client.radial_menus.RadialMenu.RadialValue(Text.translatable("item.btc.wrench.close"), (menu, type) -> menu.close()).withColor(0xFFD67B5B).enableHoverEffects(true).withHoverEffectsText(0xFFFFE16B, Formatting.BOLD)
        );

        MinecraftClient.getInstance().setScreen(new io.github.tobyrue.btc.client.radial_menus.RadialMenu(
                Text.translatable("item.btc.wrench.title.modes"),
                mainCategories,
                stack,
                BTCClient.radialMenuKeyBinding,
                0xFFD67B5B,
                true,
                false,
                0
        ));
    }
}

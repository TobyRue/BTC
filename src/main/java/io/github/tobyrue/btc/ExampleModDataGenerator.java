package io.github.tobyrue.btc;

import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.SpellScrollItem;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.*;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
//        pack.addProvider(ModModelProvider::new);
        pack.addProvider(CraftingRecipeProvider::new);
//        pack.addProvider(AdvancementsProvider::new);

        // Adding a provider example:
    }
//    public static class ModModelProvider extends FabricModelProvider {
//        public ModModelProvider(FabricDataOutput output) {
//            super(output);
//        }
//
//        @Override
//        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//
//        }
//
//        @Override
//        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//            for (SpellScrollItem spell : ModItems.SPELL_ITEMS.values()) {
//                if (!spell.spellType.isStartingSpell) {
//                    Models.HANDHELD.upload(
//                            ModelIds.getItemModelId(spell),
//                            TextureMap.layer0(BTC.identifierOf(String.format("item/%s_scroll", spell.spellType.getSpellType().toString()))),
//                            itemModelGenerator.writer
//                    );
//                }
//            }
//        }
//
//    }
    static class CraftingRecipeProvider extends FabricRecipeProvider {

        public CraftingRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            for (SpellScrollItem spell : ModItems.SPELL_ITEMS.values()) {
                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.CLUSTER_WIND_SHOT) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.WIND_CHARGE)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.BREEZE_ROD)
                            .criterion(FabricRecipeProvider.hasItem(Items.WIND_CHARGE),
                                    FabricRecipeProvider.conditionsFromItem(Items.WIND_CHARGE))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.DRAGON_FIREBALL) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', ModItems.DRAGON_ROD)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.FIRE_CHARGE)
                            .criterion(FabricRecipeProvider.hasItem(ModItems.DRAGON_ROD),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.DRAGON_ROD))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.FROST_WALKER) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.WATER_BUCKET)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.BLUE_ICE)
                            .criterion(FabricRecipeProvider.hasItem(Items.BLUE_ICE),
                                    FabricRecipeProvider.conditionsFromItem(Items.BLUE_ICE))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.EARTH_SPIKES) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.STONE)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.EMERALD)
                            .criterion(FabricRecipeProvider.hasItem(Items.EMERALD),
                                    FabricRecipeProvider.conditionsFromItem(Items.EMERALD))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.FIREBALL_STRONG) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.BLAZE_ROD)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.FIRE_CHARGE)
                            .criterion(FabricRecipeProvider.hasItem(Items.BLAZE_ROD),
                                    FabricRecipeProvider.conditionsFromItem(Items.BLAZE_ROD))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.TEMPESTS_CALL) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.PHANTOM_MEMBRANE)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.WIND_CHARGE)
                            .criterion(FabricRecipeProvider.hasItem(Items.PHANTOM_MEMBRANE),
                                    FabricRecipeProvider.conditionsFromItem(Items.PHANTOM_MEMBRANE))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }

                if (!spell.spellType.isStartingSpell && spell.spellType == SpellRegistryEnum.STORM_PUSH) {
                    ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, spell)
                            .pattern("aca")
                            .pattern("cbc")
                            .pattern("aca")
                            .input('a', Items.FEATHER)
                            .input('b', ModItems.EMPTY_SCROLL)
                            .input('c', Items.PHANTOM_MEMBRANE)
                            .criterion(FabricRecipeProvider.hasItem(Items.FEATHER),
                                    FabricRecipeProvider.conditionsFromItem(Items.FEATHER))
                            .criterion(FabricRecipeProvider.hasItem(ModItems.EMPTY_SCROLL),
                                    FabricRecipeProvider.conditionsFromItem(ModItems.EMPTY_SCROLL))
                            .offerTo(exporter);
                }
            }
        }
    }
//    static class AdvancementsProvider extends FabricAdvancementProvider {
//        public AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
//            super(output, registryLookup);
//        }
//
//        @Override
//        public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
//            AdvancementEntry rootAdvancement = Advancement.Builder.create()
//                    .display(
//                            Items.DIRT, // The display icon
//                            Text.literal("Your First Dirt Block"), // The title
//                            Text.literal("Now make a three by three"), // The description
//                            Identifier.of("textures/gui/advancements/backgrounds/adventure.png"), // Background image used
//                            AdvancementFrame.TASK, // Options: TASK, CHALLENGE, GOAL
//                            true, // Show toast top right
//                            true, // Announce to chat
//                            false // Hidden in the advancement tab
//                    )
//
//                    // The first string used in criterion is the name referenced by other advancements when they want to have 'requirements'
//                    .criterion("got_dirt", UsingItemCriterion.Conditions.items(Items.DIRT))
//                    .build(consumer, "your_mod_id_please_change_me" + "/root");
//        }
//    }

}

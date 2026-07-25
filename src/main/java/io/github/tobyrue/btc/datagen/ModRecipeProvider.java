package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {

        offerPillarSet(exporter, Blocks.TUFF, ModBlocks.TUFF_PILLAR, ModBlocks.TUFF_PILASTER);
        offerPillarSet(exporter, Blocks.TUFF_BRICKS, ModBlocks.TUFF_BRICKS_PILLAR, ModBlocks.TUFF_BRICK_PILASTER);
        offerPillarSet(exporter, Blocks.CHISELED_TUFF_BRICKS, ModBlocks.CHISELED_TUFF_BRICKS_PILLAR, ModBlocks.CHISELED_TUFF_BRICKS_PILASTER);
        offerPillarSet(exporter, Blocks.POLISHED_TUFF, ModBlocks.POLISHED_TUFF_PILLAR, ModBlocks.POLISHED_TUFF_PILASTER);

        offerPillarSet(exporter, Blocks.STONE, ModBlocks.STONE_PILLAR, ModBlocks.STONE_PILASTER);
        offerPillarSet(exporter, Blocks.COBBLESTONE, ModBlocks.COBBLESTONE_PILLAR, ModBlocks.COBBLESTONE_PILASTER);
        offerPillarSet(exporter, Blocks.MOSSY_COBBLESTONE, ModBlocks.MOSSY_COBBLESTONE_PILLAR, ModBlocks.MOSSY_COBBLESTONE_PILASTER);
        offerPillarSet(exporter, Blocks.STONE_BRICKS, ModBlocks.STONE_BRICKS_PILLAR, ModBlocks.STONE_BRICKS_PILASTER);
        offerPillarSet(exporter, Blocks.MOSSY_STONE_BRICKS, ModBlocks.MOSSY_STONE_BRICKS_PILLAR, ModBlocks.MOSSY_STONE_BRICKS_PILASTER);
        offerPillarSet(exporter, Blocks.CRACKED_STONE_BRICKS, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, ModBlocks.CRACKED_STONE_BRICKS_PILASTER);

        offerPillarSet(exporter, Blocks.DEEPSLATE, ModBlocks.DEEPSLATE_PILLAR, ModBlocks.DEEPSLATE_PILASTER);
        offerPillarSet(exporter, Blocks.COBBLED_DEEPSLATE, ModBlocks.COBBLED_DEEPSLATE_PILLAR, ModBlocks.COBBLED_DEEPSLATE_PILASTER);
        offerPillarSet(exporter, Blocks.POLISHED_DEEPSLATE, ModBlocks.POLISHED_DEEPSLATE_PILLAR, ModBlocks.POLISHED_DEEPSLATE_PILASTER);
        offerPillarSet(exporter, Blocks.DEEPSLATE_BRICKS, ModBlocks.DEEPSLATE_BRICKS_PILLAR, ModBlocks.DEEPSLATE_BRICKS_PILASTER);
        offerPillarSet(exporter, Blocks.CRACKED_DEEPSLATE_BRICKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER);
        offerPillarSet(exporter, Blocks.DEEPSLATE_TILES, ModBlocks.DEEPSLATE_TILES_PILLAR, ModBlocks.DEEPSLATE_TILES_PILASTER);
        offerPillarSet(exporter, Blocks.CRACKED_DEEPSLATE_TILES, ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR, ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER);
        offerPillarSet(exporter, Blocks.CHISELED_DEEPSLATE, ModBlocks.CHISELED_DEEPSLATE_PILLAR, ModBlocks.CHISELED_DEEPSLATE_PILASTER);

        offerStonecutting(exporter, Blocks.STONE, ModBlocks.STONE_PILLAR, ModBlocks.STONE_PILASTER);
        offerStonecutting(exporter, Blocks.STONE_BRICKS, ModBlocks.STONE_BRICKS_PILLAR, ModBlocks.STONE_BRICKS_PILASTER);
        offerStonecutting(exporter, Blocks.COBBLESTONE, ModBlocks.COBBLESTONE_PILLAR, ModBlocks.COBBLESTONE_PILASTER);
        offerStonecutting(exporter, Blocks.MOSSY_COBBLESTONE, ModBlocks.MOSSY_COBBLESTONE_PILLAR, ModBlocks.MOSSY_COBBLESTONE_PILASTER);
        offerStonecutting(exporter, Blocks.MOSSY_STONE_BRICKS, ModBlocks.MOSSY_STONE_BRICKS_PILLAR, ModBlocks.MOSSY_STONE_BRICKS_PILASTER);
        offerStonecutting(exporter, Blocks.CRACKED_STONE_BRICKS, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, ModBlocks.CRACKED_STONE_BRICKS_PILASTER);

        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.STONE_BRICKS_PILLAR, Blocks.STONE, 4);
        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.STONE_BRICKS_PILASTER, Blocks.STONE, 1);

        offerStonecutting(exporter, Blocks.TUFF, ModBlocks.TUFF_PILLAR, ModBlocks.TUFF_PILASTER);
        offerStonecutting(exporter, Blocks.TUFF_BRICKS, ModBlocks.TUFF_BRICKS_PILLAR, ModBlocks.TUFF_BRICK_PILASTER);
        offerStonecutting(exporter, Blocks.CHISELED_TUFF_BRICKS, ModBlocks.CHISELED_TUFF_BRICKS_PILLAR, ModBlocks.CHISELED_TUFF_BRICKS_PILASTER);
        offerStonecutting(exporter, Blocks.POLISHED_TUFF, ModBlocks.POLISHED_TUFF_PILLAR, ModBlocks.POLISHED_TUFF_PILASTER);
        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICKS_PILLAR, Blocks.TUFF, 4);
        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.TUFF_BRICK_PILASTER, Blocks.TUFF, 1);

        offerStonecutting(exporter, Blocks.DEEPSLATE, ModBlocks.DEEPSLATE_PILLAR, ModBlocks.DEEPSLATE_PILASTER);
        offerStonecutting(exporter, Blocks.COBBLED_DEEPSLATE, ModBlocks.COBBLED_DEEPSLATE_PILLAR, ModBlocks.COBBLED_DEEPSLATE_PILASTER);
        offerStonecutting(exporter, Blocks.POLISHED_DEEPSLATE, ModBlocks.POLISHED_DEEPSLATE_PILLAR, ModBlocks.POLISHED_DEEPSLATE_PILASTER);
        offerStonecutting(exporter, Blocks.DEEPSLATE_BRICKS, ModBlocks.DEEPSLATE_BRICKS_PILLAR, ModBlocks.DEEPSLATE_BRICKS_PILASTER);
        offerStonecutting(exporter, Blocks.CRACKED_DEEPSLATE_BRICKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER);
        offerStonecutting(exporter, Blocks.DEEPSLATE_TILES, ModBlocks.DEEPSLATE_TILES_PILLAR, ModBlocks.DEEPSLATE_TILES_PILASTER);
        offerStonecutting(exporter, Blocks.CRACKED_DEEPSLATE_TILES, ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR, ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER);
        offerStonecutting(exporter, Blocks.CHISELED_DEEPSLATE, ModBlocks.CHISELED_DEEPSLATE_PILLAR, ModBlocks.CHISELED_DEEPSLATE_PILASTER);

        offerSmelting(exporter, List.of(ModBlocks.STONE_BRICKS_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, 0.1f, 200, "cracked_stone_bricks_pillar");
        offerSmelting(exporter, List.of(ModBlocks.STONE_BRICKS_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_STONE_BRICKS_PILASTER, 0.1f, 200, "cracked_stone_bricks_pilaster");
        offerBlasting(exporter, List.of(ModBlocks.STONE_BRICKS_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, 0.1f, 100, "cracked_stone_bricks_pillar");
        offerBlasting(exporter, List.of(ModBlocks.STONE_BRICKS_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_STONE_BRICKS_PILASTER, 0.1f, 100, "cracked_stone_bricks_pilaster");

        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_BRICKS_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR, 0.1f, 200, "cracked_deepslate_bricks_pillar");
        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_BRICKS_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER, 0.1f, 200, "cracked_deepslate_bricks_pilaster");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_BRICKS_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR, 0.1f, 100, "cracked_deepslate_bricks_pillar");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_BRICKS_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER, 0.1f, 100, "cracked_deepslate_bricks_pilaster");

        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_TILES_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR, 0.1f, 200, "cracked_deepslate_tiles_pillar");
        offerSmelting(exporter, List.of(ModBlocks.DEEPSLATE_TILES_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER, 0.1f, 200, "cracked_deepslate_tiles_pilaster");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_TILES_PILLAR), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR, 0.1f, 100, "cracked_deepslate_tiles_pillar");
        offerBlasting(exporter, List.of(ModBlocks.DEEPSLATE_TILES_PILASTER), RecipeCategory.BUILDING_BLOCKS, ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER, 0.1f, 100, "cracked_deepslate_tiles_pilaster");

        offerMossyVariant(exporter, ModBlocks.COBBLESTONE_PILLAR, ModBlocks.MOSSY_COBBLESTONE_PILLAR);
        offerMossyVariant(exporter, ModBlocks.COBBLESTONE_PILASTER, ModBlocks.MOSSY_COBBLESTONE_PILASTER);

        offerMossyVariant(exporter, ModBlocks.STONE_BRICKS_PILLAR, ModBlocks.MOSSY_STONE_BRICKS_PILLAR);
        offerMossyVariant(exporter, ModBlocks.STONE_BRICKS_PILASTER, ModBlocks.MOSSY_STONE_BRICKS_PILASTER);
    }

    private void offerPillarSet(RecipeExporter exporter, ItemConvertible input, Block pillar, Block pilaster) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pillar, 8)
                .pattern("B")
                .pattern("B")
                .input('B', input)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pilaster, 6)
                .pattern(" B ")
                .pattern("BBB")
                .input('B', input)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }

    private void offerStonecutting(RecipeExporter exporter, ItemConvertible input, Block pillar, Block pilaster) {
        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, pillar, input, 4);
        createStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, pilaster, input, 1);
    }

    private void createStonecuttingRecipe(RecipeExporter exporter, RecipeCategory category, ItemConvertible output, ItemConvertible input, int count) {
        StonecuttingRecipeJsonBuilder.createStonecutting(Ingredient.ofItems(input), category, output, count)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.getItemPath(output) + "_from_" + RecipeProvider.getItemPath(input) + "_stonecutting");
    }

    private void offerMossyVariant(RecipeExporter exporter, Block base, Block mossyResult) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, mossyResult)
                .input(base)
                .input(Ingredient.ofItems(Blocks.VINE, Blocks.MOSS_BLOCK))
                .criterion(hasItem(base), conditionsFromItem(base))
                .criterion(hasItem(Blocks.VINE), conditionsFromItem(Blocks.VINE))
                .criterion(hasItem(Blocks.MOSS_BLOCK), conditionsFromItem(Blocks.MOSS_BLOCK))
                .offerTo(exporter, RecipeProvider.getItemPath(mossyResult) + "_from_mossing");
    }
}
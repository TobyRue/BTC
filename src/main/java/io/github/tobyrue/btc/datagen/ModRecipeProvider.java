package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.item.ModItems;
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
        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, ModBlocks.SALT_BLOCK, ModItems.SALT);
        offerShapelessRecipe(exporter, ModItems.SALT, ModBlocks.SALT_BLOCK, "salt", 4);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.CHARCOAL, 9)
                .input(ModBlocks.CHARCOAL_BLOCK)
                .criterion(hasItem(ModBlocks.CHARCOAL_BLOCK), conditionsFromItem(ModBlocks.CHARCOAL_BLOCK))
                .offerTo(exporter, RecipeProvider.getItemPath(Items.CHARCOAL) + "_from_" + RecipeProvider.getItemPath(ModBlocks.CHARCOAL_BLOCK));

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARCOAL_BLOCK, 1)
                .pattern("CCC")
                .pattern("CCC")
                .pattern("CCC")
                .input('C', Items.CHARCOAL)
                .criterion(hasItem(Items.CHARCOAL), conditionsFromItem(Items.CHARCOAL))
                .offerTo(exporter);
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
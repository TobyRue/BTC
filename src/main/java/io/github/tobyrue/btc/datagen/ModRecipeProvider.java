package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        generateChargedCopperRecipe(exporter, ModBlocks.CHARGED_COPPER);

        createWaxingRecipe(exporter, ModBlocks.CHARGED_COPPER, ModBlocks.WAXED_CHARGED_COPPER);
        createWaxingRecipe(exporter, ModBlocks.EXPOSED_CHARGED_COPPER, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER);
        createWaxingRecipe(exporter, ModBlocks.WEATHERED_CHARGED_COPPER, ModBlocks.WAXED_WEATHERED_CHARGED_COPPER);
        createWaxingRecipe(exporter, ModBlocks.OXIDIZED_CHARGED_COPPER, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER);
    }

    private void generateChargedCopperRecipe(RecipeExporter exporter, Block resultBlock) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, resultBlock)
                .pattern(" C ")
                .pattern("CRC")
                .pattern(" C ")
                .input('R', Items.REDSTONE_BLOCK)
                .input('C', Items.COPPER_INGOT)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .offerTo(exporter);
    }

    private void createWaxingRecipe(RecipeExporter exporter, Block unwaxed, Block waxed) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, waxed)
                .input(unwaxed)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(unwaxed), conditionsFromItem(unwaxed))
                .offerTo(exporter, RecipeProvider.getRecipeName(unwaxed) + "_to_waxed");
    }
}
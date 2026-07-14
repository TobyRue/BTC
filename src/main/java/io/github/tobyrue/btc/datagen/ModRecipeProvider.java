package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
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

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARGED_COPPER)
                .pattern("CCC")
                .pattern("CRC")
                .pattern("CCC")
                .input('C', Items.COPPER_INGOT)
                .input('R', Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT))
                .criterion(hasItem(Items.REDSTONE_BLOCK), conditionsFromItem(Items.REDSTONE_BLOCK))
                .offerTo(exporter, BTC.identifierOf("charged_copper_from_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CHARGED_COPPER)
                .input(Items.COPPER_BLOCK)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.COPPER_BLOCK), conditionsFromItem(Items.COPPER_BLOCK))
                .offerTo(exporter, BTC.identifierOf("charged_copper_from_block"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.EXPOSED_CHARGED_COPPER)
                .input(Items.EXPOSED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.EXPOSED_COPPER), conditionsFromItem(Items.EXPOSED_COPPER))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WEATHERED_CHARGED_COPPER)
                .input(Items.WEATHERED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.WEATHERED_COPPER), conditionsFromItem(Items.WEATHERED_COPPER))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.OXIDIZED_CHARGED_COPPER)
                .input(Items.OXIDIZED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.OXIDIZED_COPPER), conditionsFromItem(Items.OXIDIZED_COPPER))
                .offerTo(exporter);


        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_CHARGED_COPPER)
                .input(Items.WAXED_COPPER_BLOCK)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.WAXED_COPPER_BLOCK), conditionsFromItem(Items.WAXED_COPPER_BLOCK))
                .offerTo(exporter, BTC.identifierOf("waxed_charged_copper_from_redstone"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER)
                .input(Items.WAXED_EXPOSED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.WAXED_EXPOSED_COPPER), conditionsFromItem(Items.WAXED_EXPOSED_COPPER))
                .offerTo(exporter, BTC.identifierOf("waxed_exposed_charged_copper_from_redstone"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_WEATHERED_CHARGED_COPPER)
                .input(Items.WAXED_WEATHERED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.WAXED_WEATHERED_COPPER), conditionsFromItem(Items.WAXED_WEATHERED_COPPER))
                .offerTo(exporter, BTC.identifierOf("waxed_weathered_charged_copper_from_redstone"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER)
                .input(Items.WAXED_OXIDIZED_COPPER)
                .input(Items.REDSTONE_BLOCK)
                .criterion(hasItem(Items.WAXED_OXIDIZED_COPPER), conditionsFromItem(Items.WAXED_OXIDIZED_COPPER))
                .offerTo(exporter, BTC.identifierOf("waxed_oxidized_charged_copper_from_redstone"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_CHARGED_COPPER)
                .input(ModBlocks.CHARGED_COPPER)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER)
                .input(ModBlocks.EXPOSED_CHARGED_COPPER)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_WEATHERED_CHARGED_COPPER)
                .input(ModBlocks.WEATHERED_CHARGED_COPPER)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB))
                .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER)
                .input(ModBlocks.OXIDIZED_CHARGED_COPPER)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(Items.HONEYCOMB), conditionsFromItem(Items.HONEYCOMB))
                .offerTo(exporter);


    }
}
package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
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


        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.GUNPOWDER_BARREL)
                .input(Items.GUNPOWDER)
                .input(Items.BARREL)
                .criterion(hasItem(Items.GUNPOWDER), conditionsFromItem(Items.GUNPOWDER))
                .criterion(hasItem(Items.BARREL), conditionsFromItem(Items.BARREL))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.BELLOW)
                .pattern("PWP")
                .pattern("LDL")
                .pattern("PRP")
                .input('P', net.minecraft.recipe.Ingredient.fromTag(net.minecraft.registry.tag.ItemTags.PLANKS))
                .input('R', Items.REDSTONE)
                .input('L', Items.LEATHER)
                .input('D', Items.DISPENSER)
                .input('W', Items.WIND_CHARGE)
                .criterion("has_planks", conditionsFromTag(net.minecraft.registry.tag.ItemTags.PLANKS))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, ModBlocks.POLISHED_TUFF_PRESSURE_PLATE)
                .pattern("TT")
                .input('T', Items.POLISHED_TUFF)
                .criterion(hasItem(Items.POLISHED_TUFF), conditionsFromItem(Items.POLISHED_TUFF))
                .offerTo(exporter);

        offerCopperButtonRecipe(exporter, ModBlocks.UNOXIDIZED_COPPER_BUTTON, Items.COPPER_INGOT);

        offerCopperFanRecipe(exporter, ModBlocks.COPPER_TRIAL_FAN, Items.COPPER_INGOT, Items.TUFF_BRICKS);

        offerWaxingRecipe(exporter, ModBlocks.COPPER_TRIAL_FAN, ModBlocks.WAXED_COPPER_TRIAL_FAN);
        offerWaxingRecipe(exporter, ModBlocks.EXPOSED_COPPER_TRIAL_FAN, ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN);
        offerWaxingRecipe(exporter, ModBlocks.WEATHERED_COPPER_TRIAL_FAN, ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN);
        offerWaxingRecipe(exporter, ModBlocks.OXIDIZED_COPPER_TRIAL_FAN, ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN);

        offerWaxingRecipe(exporter, ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
        offerWaxingRecipe(exporter, ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
        offerWaxingRecipe(exporter, ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
        offerWaxingRecipe(exporter, ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);


        offerPillarAndPilasterRecipes(exporter, ModBlocks.STONE_PILLAR, ModBlocks.STONE_PILASTER, Items.STONE);
        offerPillarAndPilasterRecipes(exporter, ModBlocks.STONE_BRICKS_PILLAR, ModBlocks.STONE_BRICKS_PILASTER, Items.STONE_BRICKS);
        offerPillarAndPilasterRecipes(exporter, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, ModBlocks.CRACKED_STONE_BRICKS_PILASTER, Items.CRACKED_STONE_BRICKS);

        offerPillarAndPilasterRecipes(exporter, ModBlocks.TUFF_PILLAR, ModBlocks.TUFF_PILASTER, Items.TUFF);
        offerPillarAndPilasterRecipes(exporter, ModBlocks.TUFF_BRICKS_PILLAR, ModBlocks.TUFF_BRICK_PILASTER, Items.TUFF_BRICKS);
        offerPillarAndPilasterRecipes(exporter, ModBlocks.POLISHED_TUFF_PILLAR, ModBlocks.POLISHED_TUFF_PILASTER, Items.POLISHED_TUFF);
        offerPillarAndPilasterRecipes(exporter, ModBlocks.CHISELED_TUFF_BRICKS_PILLAR, ModBlocks.CHISELED_TUFF_BRICKS_PILASTER, Items.CHISELED_TUFF_BRICKS);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SCOPED_CROSSBOW)
                .input(Items.CROSSBOW)
                .input(ModItems.SCOPED_CROSSBOW)
                .criterion(hasItem(ModItems.SCOPED_CROSSBOW), conditionsFromItem(ModItems.SCOPED_CROSSBOW))
                .offerTo(exporter);


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.ELDRITCH_ARMOR_TRIM, 2)
                .pattern("DTD")
                .pattern("DSD")
                .pattern("DDD")
                .input('T', ModItems.ELDRITCH_ARMOR_TRIM)
                .input('S', Items.COPPER_BLOCK)
                .input('D', Items.DIAMOND)
                .criterion(hasItem(ModItems.ELDRITCH_ARMOR_TRIM), conditionsFromItem(ModItems.ELDRITCH_ARMOR_TRIM))
                .offerTo(exporter, getRecipeName(ModItems.ELDRITCH_ARMOR_TRIM) + "_duplication");

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SUN_ARMOR_TRIM, 2)
                .pattern("DTD")
                .pattern("DSD")
                .pattern("DDD")
                .input('T', ModItems.SUN_ARMOR_TRIM)
                .input('S', Items.COPPER_BLOCK)
                .input('D', Items.DIAMOND)
                .criterion(hasItem(ModItems.SUN_ARMOR_TRIM), conditionsFromItem(ModItems.SUN_ARMOR_TRIM))
                .offerTo(exporter, getRecipeName(ModItems.SUN_ARMOR_TRIM) + "_duplication");
    }


    private void offerWaxingRecipe(RecipeExporter exporter, Block input, Block output) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, output)
                .input(input)
                .input(Items.HONEYCOMB)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, getRecipeName(output) + "_from_waxing");
    }

    private void offerCopperButtonRecipe(RecipeExporter exporter, Block button, Item copperMaterial) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, button)
                .input(copperMaterial)
                .criterion(hasItem(copperMaterial), conditionsFromItem(copperMaterial))
                .offerTo(exporter);
    }

    private void offerCopperFanRecipe(RecipeExporter exporter, Block fanBlock, Item copperMaterial, Item tuffMaterial) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, fanBlock)
                .pattern("TIT")
                .pattern("IDI")
                .pattern("TIT")
                .input('I', copperMaterial)
                .input('T', tuffMaterial)
                .input('D', Items.DISPENSER)
                .criterion(hasItem(copperMaterial), conditionsFromItem(copperMaterial))
                .offerTo(exporter);
    }


    private void offerPillarAndPilasterRecipes(RecipeExporter exporter, Block pillar, Block pilaster, Item baseItem) {
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, pillar, baseItem);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, pilaster, baseItem);

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pillar, 6)
                .pattern("B")
                .pattern("B")
                .pattern("B")
                .input('B', baseItem)
                .criterion(hasItem(baseItem), conditionsFromItem(baseItem))
                .offerTo(exporter, getRecipeName(pillar) + "_from_crafting");

        ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, pilaster, 4)
                .pattern("   ")
                .pattern(" B ")
                .pattern("BBB")
                .input('B', baseItem)
                .criterion(hasItem(baseItem), conditionsFromItem(baseItem))
                .offerTo(exporter, getRecipeName(pilaster) + "_from_crafting");
    }
}
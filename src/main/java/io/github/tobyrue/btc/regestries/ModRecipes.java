package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipes {
    public static final RecipeType<ScrollTableRecipe> SCROLL_TABLE_RECIPE_TYPE = RecipeType.register(
            "scroll_crafting"
    );

    public static final RecipeSerializer<ScrollTableRecipe> SCROLL_TABLE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            BTC.identifierOf("scroll_crafting"),
            new ScrollTableRecipe.Serializer()
    );

    public static void registerRecipes() {
    }
}
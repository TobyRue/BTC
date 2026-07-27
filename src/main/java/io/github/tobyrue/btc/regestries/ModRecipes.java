package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeType<ScrollTableRecipe> SCROLL_TABLE_RECIPE_TYPE = register("scroll_crafting");

    public static final RecipeSerializer<ScrollTableRecipe> SCROLL_TABLE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            BTC.identifierOf("scroll_crafting"),
            new ScrollTableRecipe.Serializer()
    );

    private static <T extends Recipe<?>> RecipeType<T> register(final String id) {
        return Registry.register(
                Registries.RECIPE_TYPE,
                BTC.identifierOf(id),
                new RecipeType<T>() {
                    @Override
                    public String toString() {
                        return BTC.identifierOf(id).toString();
                    }
                }
        );
    }
    public static void registerRecipes() {
    }
}
package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import io.github.tobyrue.btc.recipes.SpellUpgradeRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    // In ModRecipes.java
    public static final RecipeType<Recipe<ScrollTableRecipeInput>> SCROLL_TABLE_RECIPE_TYPE =
            Registry.register(
                    Registries.RECIPE_TYPE,
                    BTC.identifierOf("scroll_table"),
                    new RecipeType<Recipe<ScrollTableRecipeInput>>() {
                        @Override
                        public String toString() {
                            return "scroll_table";
                        }
                    }
            );

    public static final RecipeSerializer<ScrollTableRecipe> SCROLL_TABLE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            BTC.identifierOf("scroll_crafting"),
            new ScrollTableRecipe.Serializer()
    );

    public static final RecipeSerializer<SpellUpgradeRecipe> SPELL_UPGRADE_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    BTC.identifierOf("scroll_upgrade"),
                    new SpellUpgradeRecipe.Serializer()
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
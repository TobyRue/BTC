package io.github.tobyrue.btc.client.screen.recipe_book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ScrollTableRecipeBookWidget extends RecipeBookWidget implements RecipeDisplayListener {

    @Override
    public void showGhostRecipe(RecipeEntry<?> recipe, List<Slot> slots) {
        this.ghostSlots.reset();
        this.ghostSlots.setRecipe(recipe);

        if (this.client != null && this.client.world != null) {
            ItemStack resultStack = recipe.value().getResult(this.client.world.getRegistryManager());
            Slot resultSlot = slots.get(9);
            this.ghostSlots.addSlot(Ingredient.ofStacks(resultStack), resultSlot.x, resultSlot.y);
        }

        DefaultedList<Ingredient> ingredients = recipe.value().getIngredients();
        for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
            Ingredient ingredient = ingredients.get(i);
            System.out.println("Num: " + i + " Ingred: " + (ingredient.getMatchingStacks().length > 0 ? ingredient.getMatchingStacks()[0].getName().getString() : "Empty"));
            if (!ingredient.isEmpty()) {
                Slot slot = slots.get(i);
                this.ghostSlots.addSlot(ingredient, slot.x, slot.y);
            }
        }
    }

    @Override
    public void onRecipesDisplayed(List<RecipeEntry<?>> recipes) {
        if (!recipes.isEmpty() && this.craftingScreenHandler != null) {
            RecipeEntry<?> selectedRecipe = recipes.get(0);
            this.showGhostRecipe(selectedRecipe, this.craftingScreenHandler.slots);
        }
    }
}
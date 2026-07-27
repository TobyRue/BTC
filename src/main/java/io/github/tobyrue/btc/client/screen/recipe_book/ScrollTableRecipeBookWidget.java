package io.github.tobyrue.btc.client.screen.recipe_book;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ScrollTableRecipeBookWidget extends RecipeBookWidget {

    @Override
    public void showGhostRecipe(RecipeEntry<?> recipe, List<Slot> slots) {
        this.ghostSlots.reset();
        this.ghostSlots.setRecipe(recipe);

        ItemStack resultStack = recipe.value().getResult(this.client.world.getRegistryManager());
        Slot outputSlot = slots.get(44);
        this.ghostSlots.addSlot(Ingredient.ofStacks(resultStack), outputSlot.x, outputSlot.y);

        DefaultedList<Ingredient> ingredients = recipe.value().getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            if (i >= 8) break;

            Ingredient ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                Slot inputSlot = slots.get(36 + i);
                this.ghostSlots.addSlot(ingredient, inputSlot.x, inputSlot.y);
            }
        }
    }
}
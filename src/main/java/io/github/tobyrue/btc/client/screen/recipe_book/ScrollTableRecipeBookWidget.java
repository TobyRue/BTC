package io.github.tobyrue.btc.client.screen.recipe_book;

import com.mojang.logging.LogUtils;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeDisplayListener;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.slf4j.Logger;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ScrollTableRecipeBookWidget extends RecipeBookWidget implements RecipeDisplayListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void showGhostRecipe(RecipeEntry<?> recipe, List<Slot> slots) {
        LOGGER.info("[ScrollTableRecipeBookWidget] showGhostRecipe executing for recipe: {}", recipe.id());

        if (!(recipe.value() instanceof ScrollTableRecipe scrollRecipe)) {
            LOGGER.warn("[ScrollTableRecipeBookWidget] Not a ScrollTableRecipe instance!");
            return;
        }

        this.ghostSlots.reset();
        this.ghostSlots.setRecipe(recipe);

        if (this.client != null && this.client.world != null) {
            ItemStack resultStack = recipe.value().getResult(this.client.world.getRegistryManager());
            Slot resultSlot = slots.get(44); // Crafting output slot index
            LOGGER.info("[ScrollTableRecipeBookWidget] Output Ghost Slot set to {} at Slot 44 (X: {}, Y: {})",
                    resultStack.getItem(), resultSlot.x, resultSlot.y);
            this.ghostSlots.addSlot(Ingredient.ofStacks(resultStack), resultSlot.x, resultSlot.y);
        }

        DefaultedList<Ingredient> ingredients = scrollRecipe.getIngredients();
        for (int i = 0; i < Math.min(ingredients.size(), 8); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                Slot slot = slots.get(36 + i); // Circle input slots (36..43)
                LOGGER.info("[ScrollTableRecipeBookWidget] Ghost Ingredient {} bound to Slot {}", i, 36 + i);
                this.ghostSlots.addSlot(ingredient, slot.x, slot.y);
            }
        }
    }

    @Override
    public void onRecipesDisplayed(List<RecipeEntry<?>> recipes) {
        LOGGER.info("[ScrollTableRecipeBookWidget] onRecipesDisplayed triggered with {} recipes", recipes.size());

        if (!recipes.isEmpty() && this.craftingScreenHandler != null) {
            RecipeEntry<?> selectedRecipe = recipes.get(0);
            LOGGER.info("[ScrollTableRecipeBookWidget] Displaying ghost recipe for: {}", selectedRecipe.id());
            this.showGhostRecipe(selectedRecipe, this.craftingScreenHandler.slots);
        }
    }
}
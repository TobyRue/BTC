package io.github.tobyrue.btc.client.screen.recipe_book;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.recipe.input.RecipeInput;
import java.util.ArrayList;
import java.util.List;

public class ScrollTableRecipeInput implements RecipeInput {
    private final ItemStack[] inputs;

    public ScrollTableRecipeInput(ItemStack[] inputs) {
        this.inputs = inputs;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot >= 0 && slot < inputs.length ? inputs[slot] : ItemStack.EMPTY;
    }

    @Override
    public int getSize() {
        return inputs.length;
    }

    public CraftingRecipeInput asCraftingInput() {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(i < inputs.length ? inputs[i] : ItemStack.EMPTY);
        }
        return CraftingRecipeInput.create(3, 3, stacks);
    }
}
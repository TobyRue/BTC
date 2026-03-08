package io.github.tobyrue.btc.recipes;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.item.BlockKeyItem;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.Map;

public class KeyDuplicateRecipe extends ShapedRecipe {
    public KeyDuplicateRecipe(CraftingRecipeCategory category) {
        // Hardcoded pattern: Gold (G), Diamond (D), Key (K)
        // G D G
        // G K G
        // G G G
        super("", category, RawShapedRecipe.create(
                Map.of(
                        'G', Ingredient.ofItems(Items.GOLD_INGOT),
                        'D', Ingredient.ofItems(Items.DIAMOND),
                        'K', Ingredient.ofItems(ModItems.BLOCK_KEY)
                ),
                "DGD",
                        "GKG",
                        "DGD"
        ), new ItemStack(ModItems.BLOCK_KEY));
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        // Check if the basic shape matches first
        if (!super.matches(input, world)) {
            return false;
        }
        // Ensure the key in the middle actually has data to copy
        ItemStack key = findKey(input);
        return !key.isEmpty() && key.contains(BTC.KEY_UUID);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack sourceKey = findKey(input);
        if (sourceKey.isEmpty()) return ItemStack.EMPTY;

        ItemStack result = sourceKey.copyWithCount(2);

        result.set(BTC.KEY_UUID, sourceKey.get(BTC.KEY_UUID));

        return result;
    }

    private static ItemStack findKey(CraftingRecipeInput input) {
        for (int i = 0; i < input.getSize(); ++i) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.getItem() instanceof BlockKeyItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BTC.KEY_DUPLICATE_SERIALIZER;
    }
}
package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.List;

public class UnlockScrollManager {

    public static final List<ItemStack> UNLOCK_SCROLLS = new ArrayList<>();

    public static void init() {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (!success) return;

            UNLOCK_SCROLLS.clear();

            RecipeManager recipeManager = server.getRecipeManager();
            RegistryWrapper.WrapperLookup registries = server.getRegistryManager();

            for (RecipeEntry<?> recipe : recipeManager.values()) {
                ItemStack result = recipe.value().getResult(registries);

                if (result.isOf(ModItems.UNLOCK_SCROLL)) {
                    UNLOCK_SCROLLS.add(result.copy());
                }
            }
        });
    }
}
package io.github.tobyrue.btc.mixin;

import net.minecraft.recipe.book.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeBookCategory.class)
public enum RecipeBookCategoryMixin {
    BTC_SCROLL_TABLE
}
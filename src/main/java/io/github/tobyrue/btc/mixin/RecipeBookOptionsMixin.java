package io.github.tobyrue.btc.mixin;

import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.book.RecipeBookOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RecipeBookOptions.class)
public class RecipeBookOptionsMixin {

    @Shadow
    @Final
    private Map<RecipeBookCategory, RecipeBookOptions.CategoryOption> categoryOptions;

    @Inject(method = "<init>(Ljava/util/Map;)V", at = @At("TAIL"))
    private void onInit(Map<RecipeBookCategory, RecipeBookOptions.CategoryOption> categoryOptions, CallbackInfo ci) {
        this.categoryOptions.putIfAbsent(
                RecipeBookCategory.BTC_SCROLL_TABLE,
                new RecipeBookOptions.CategoryOption(false, false)
        );
    }
}
package io.github.tobyrue.btc.mixin;

import com.google.common.collect.ImmutableList;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeBookGroup.class)
public class RecipeBookGroupGroupingsMixin {

    @Inject(method = "getGroups", at = @At("HEAD"), cancellable = true)
    private static void onGetGroups(RecipeBookCategory category, CallbackInfoReturnable<List<RecipeBookGroup>> cir) {
        if (category == RecipeBookCategory.BTC_SCROLL_TABLE) {
            cir.setReturnValue(ImmutableList.of(
                    RecipeBookGroup.BTC_SCROLL_TABLE_SEARCH
            ));
        }
    }
    @Inject(method = "getIcons", at = @At("HEAD"), cancellable = true)
    private void onGetIcons(CallbackInfoReturnable<List<ItemStack>> cir) {
        RecipeBookGroup self = (RecipeBookGroup) (Object) this;

        if (self == RecipeBookGroup.BTC_SCROLL_TABLE_SEARCH) {
            cir.setReturnValue(List.of(new ItemStack(Items.COMPASS)));
        }
    }
}
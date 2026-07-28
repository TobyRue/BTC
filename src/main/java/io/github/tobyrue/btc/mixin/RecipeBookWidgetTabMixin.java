package io.github.tobyrue.btc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeBookWidget.class)
public class RecipeBookWidgetTabMixin {

    @WrapOperation(
            method = "refreshTabButtons",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/recipebook/RecipeBookGroup;CRAFTING_SEARCH:Lnet/minecraft/client/recipebook/RecipeBookGroup;"
            )
    )
    private RecipeBookGroup btc$craftingSearch(Operation<RecipeBookGroup> original) {
        return original.call();
    }
}
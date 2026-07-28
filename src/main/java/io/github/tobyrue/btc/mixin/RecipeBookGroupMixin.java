package io.github.tobyrue.btc.mixin;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeBookGroup.class)
public enum RecipeBookGroupMixin {
    BTC_SCROLL_TABLE_SEARCH(new ItemStack[]{new ItemStack(Items.COMPASS)});

    @Shadow
    RecipeBookGroupMixin(ItemStack... entries) {
        throw new AssertionError();
    }
}
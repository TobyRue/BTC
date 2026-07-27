package io.github.tobyrue.btc.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.regestries.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ScrollTableRecipe implements Recipe<ScrollTableRecipeInput> {
    private final RawShapedRecipe rawPattern;
    private final ItemStack result;

    public ScrollTableRecipe(RawShapedRecipe rawPattern, ItemStack result) {
        this.rawPattern = rawPattern;
        this.result = result;
    }

    public RawShapedRecipe getRawPattern() {
        return this.rawPattern;
    }

    @Override
    public boolean matches(ScrollTableRecipeInput input, World world) {
        return this.rawPattern.matches(input.asCraftingInput());
    }

    @Override
    public ItemStack craft(ScrollTableRecipeInput input, RegistryWrapper.WrapperLookup registries) {
        return this.result.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return this.result;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return this.rawPattern.getIngredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCROLL_TABLE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SCROLL_TABLE_RECIPE_TYPE;
    }


    public static class Serializer implements RecipeSerializer<ScrollTableRecipe> {
        public static final MapCodec<ScrollTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RawShapedRecipe.CODEC.forGetter(ScrollTableRecipe::getRawPattern),
                        ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
                ).apply(instance, ScrollTableRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, ScrollTableRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                (buf, recipe) -> {
                    RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.rawPattern);
                    ItemStack.PACKET_CODEC.encode(buf, recipe.result);
                },
                buf -> new ScrollTableRecipe(
                        RawShapedRecipe.PACKET_CODEC.decode(buf),
                        ItemStack.PACKET_CODEC.decode(buf)
                )
        );

        @Override
        public MapCodec<ScrollTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ScrollTableRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
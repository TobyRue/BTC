package io.github.tobyrue.btc.recipes;

import com.mojang.logging.LogUtils;
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
import org.slf4j.Logger;

public class ScrollTableRecipe implements Recipe<ScrollTableRecipeInput> {
    private static final Logger LOGGER = LogUtils.getLogger();

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
        boolean match = this.rawPattern.matches(input.asCraftingInput());
        LOGGER.info("[ScrollTableRecipe] Matches check for {}: {}", this.result.getItem(), match);
        return match;
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
        DefaultedList<Ingredient> patternIngredients = this.rawPattern.getIngredients();
        DefaultedList<Ingredient> circleIngredients = DefaultedList.ofSize(8, Ingredient.EMPTY);

        int[] gridToCircleMap = {0, 1, 2, 3, -1, 4, 5, 6, 7};

        for (int i = 0; i < patternIngredients.size() && i < 9; i++) {
            int targetSlot = gridToCircleMap[i];
            if (targetSlot != -1) {
                circleIngredients.set(targetSlot, patternIngredients.get(i));
            }
        }
        return circleIngredients;
    }

    @Override
    public boolean isEmpty() {
        return this.rawPattern.getIngredients().isEmpty();
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
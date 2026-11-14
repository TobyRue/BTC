package io.github.tobyrue.btc.recipes;

import com.mojang.serialization.Codec;
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
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class ScrollTableRecipe implements Recipe<ScrollTableRecipeInput> {

    private final RawShapedRecipe rawPattern;
    private final ItemStack result;
    private final ExperienceRequirement experience;

    public ScrollTableRecipe(RawShapedRecipe rawPattern, ItemStack result, ExperienceRequirement experience) {
        this.rawPattern = rawPattern;
        this.result = result;
        this.experience = experience;
    }

    public ExperienceRequirement getExperience() {
        return experience;
    }

    public RawShapedRecipe getRawPattern() {
        return this.rawPattern;
    }

    @Override
    public boolean matches(ScrollTableRecipeInput input, World world) {
        boolean match = this. rawPattern.matches(input.asCraftingInput());
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
        DefaultedList<Ingredient> circleIngredients = DefaultedList.ofSize(9, Ingredient.EMPTY);

        for (int i = 0; i < patternIngredients.size() && i < 9; i++) {
            circleIngredients.set(i, patternIngredients.get(i));
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

    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.BTC_SCROLL_TABLE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SCROLL_TABLE_RECIPE_TYPE;
    }

    public static class Serializer implements RecipeSerializer<ScrollTableRecipe> {
        public static final MapCodec<ScrollTableRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RawShapedRecipe.CODEC.forGetter(ScrollTableRecipe::getRawPattern),
                        ItemStack.VALIDATED_UNCOUNTED_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                        ExperienceRequirement.CODEC.fieldOf("experience").forGetter(ScrollTableRecipe::getExperience)
                ).apply(instance, ScrollTableRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, ScrollTableRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                (buf, recipe) -> {
                    RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.rawPattern);
                    ItemStack.PACKET_CODEC.encode(buf, recipe.result);
                    ExperienceRequirement.PACKET_CODEC.encode(buf, recipe.experience);
                },
                buf -> new ScrollTableRecipe(
                        RawShapedRecipe.PACKET_CODEC.decode(buf),
                        ItemStack.PACKET_CODEC.decode(buf),
                        ExperienceRequirement.PACKET_CODEC.decode(buf)
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


    public enum ExperienceType implements StringIdentifiable {
        LEVELS("levels"),
        POINTS("points");

        private final String name;

        ExperienceType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
        public static final Codec<ExperienceType> CODEC = StringIdentifiable.createCodec(ExperienceType::values);
        public static final PacketCodec<RegistryByteBuf, ExperienceType> PACKET_CODEC = PacketCodec.of(
                (type, buf) -> buf.writeEnumConstant(type),
                buf -> buf.readEnumConstant(ExperienceType.class)
        );

    }
    public record ExperienceRequirement(ExperienceType type, int amount) {
        public static final Codec<ExperienceRequirement> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ExperienceType.CODEC.fieldOf("type").forGetter(ExperienceRequirement::type),
                        Codec.INT.fieldOf("amount").forGetter(ExperienceRequirement::amount)
                ).apply(instance, ExperienceRequirement::new)
        );

        public static final PacketCodec<RegistryByteBuf, ExperienceRequirement> PACKET_CODEC = PacketCodec.ofStatic(
                (buf, req) -> {
                    ExperienceType.PACKET_CODEC.encode(buf, req.type());
                    buf.writeInt(req.amount());
                },
                buf -> new ExperienceRequirement(
                        ExperienceType.PACKET_CODEC.decode(buf),
                        buf.readInt()
                )
        );
    }
}
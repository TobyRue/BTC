package io.github.tobyrue.btc.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.BTC;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Unit;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class UnbreakableSmithingRecipe implements SmithingRecipe {

    final Ingredient template;
    final Ingredient base;
    final Ingredient addition;

    public UnbreakableSmithingRecipe(Ingredient template, Ingredient base, Ingredient addition) {
        this.template = template;
        this.base = base;
        this.addition = addition;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return template.test(input.template())
                && base.test(input.base())
                && addition.test(input.addition());
    }

    @Override
    public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack stack = input.base().copy();

        if (stack.contains(DataComponentTypes.UNBREAKABLE)) {
            return ItemStack.EMPTY;
        }

        stack.set(DataComponentTypes.UNBREAKABLE, new UnbreakableComponent(true));
        return stack;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return base.test(stack)
                && stack.isDamageable()
                && !stack.contains(DataComponentTypes.UNBREAKABLE);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return addition.test(stack);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BTC.UNBREAKABLE_SMITHING;
    }

    public boolean isEmpty() {
        return Stream.of(template, base, addition).anyMatch(Ingredient::isEmpty);
    }

    public static class Serializer implements RecipeSerializer<UnbreakableSmithingRecipe> {

        private static final MapCodec<UnbreakableSmithingRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("template").forGetter(r -> r.template),
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("base").forGetter(r -> r.base),
                        Ingredient.ALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(r -> r.addition)
                ).apply(instance, UnbreakableSmithingRecipe::new));

        public static final PacketCodec<RegistryByteBuf, UnbreakableSmithingRecipe> PACKET_CODEC =
                PacketCodec.ofStatic(Serializer::write, Serializer::read);

        @Override
        public MapCodec<UnbreakableSmithingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, UnbreakableSmithingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static UnbreakableSmithingRecipe read(RegistryByteBuf buf) {
            Ingredient template = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient base = Ingredient.PACKET_CODEC.decode(buf);
            Ingredient addition = Ingredient.PACKET_CODEC.decode(buf);

            return new UnbreakableSmithingRecipe(template, base, addition);
        }

        private static void write(RegistryByteBuf buf, UnbreakableSmithingRecipe recipe) {
            Ingredient.PACKET_CODEC.encode(buf, recipe.template);
            Ingredient.PACKET_CODEC.encode(buf, recipe.base);
            Ingredient.PACKET_CODEC.encode(buf, recipe.addition);
        }
    }
}
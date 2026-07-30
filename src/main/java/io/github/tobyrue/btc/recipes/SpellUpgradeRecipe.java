package io.github.tobyrue.btc.recipes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.component.UnlockSpellComponent;
import io.github.tobyrue.btc.item.UnlockScrollItem;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRecipes;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.btc.util.UnlockScrollCache;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpellUpgradeRecipe implements Recipe<ScrollTableRecipeInput> {

    private final RawShapedRecipe rawPattern;
    private final Identifier upgradeId;

    public SpellUpgradeRecipe(RawShapedRecipe rawPattern, Identifier upgradeId) {
        this.rawPattern = rawPattern;
        this.upgradeId = upgradeId;
    }

    public RawShapedRecipe getRawPattern() {
        return this.rawPattern;
    }

    public Identifier getUpgradeId() {
        return this.upgradeId;
    }

    @Override
    public boolean matches(ScrollTableRecipeInput input, World world) {

        ItemStack scrollStack = input.getStackInSlot(4);
        if (scrollStack.isEmpty() || !(scrollStack.getItem() instanceof UnlockScrollItem)) {
            return false;
        }

        DefaultedList<Ingredient> ingredients = this.rawPattern.getIngredients();

        for (int i = 0; i < 9; i++) {
            if (i == 4) continue;

            Ingredient required = i < ingredients.size() ? ingredients.get(i) : Ingredient.EMPTY;
            ItemStack actualSlotStack = input.getStackInSlot(i);

            if (!required.test(actualSlotStack)) {
                return false;
            }
        }

        UnlockSpellComponent component = scrollStack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (component == null) {
            return false;
        }

        Spell spell = ModRegistries.SPELL.get(component.id());
        if (spell == null) {
            return false;
        }

        if (!(spell instanceof UpgradableSpell upgradableSpell)) {
            return false;
        }

        GrabBag args = GrabBag.fromNBT(component.argsAsNbt());
        HashMap<Identifier, Pair<String, ?>> availableUpgrades = upgradableSpell.getUpgradeOptions(args);


        return availableUpgrades.containsKey(this.upgradeId);
    }

    @Override
    public ItemStack craft(ScrollTableRecipeInput input, RegistryWrapper.WrapperLookup registries) {

        ItemStack scrollStack = input.getStackInSlot(4).copy();
        if (scrollStack.isEmpty() || !(scrollStack.getItem() instanceof UnlockScrollItem)) {
            return ItemStack.EMPTY;
        }

        UnlockSpellComponent component = scrollStack.get(ModComponents.UNLOCK_SPELL_COMPONENT);
        if (component == null) {
            return ItemStack.EMPTY;
        }

        Spell spell = ModRegistries.SPELL.get(component.id());
        if (spell instanceof UpgradableSpell upgradableSpell) {
            GrabBag args = GrabBag.fromNBT(component.argsAsNbt());
            HashMap<Identifier, Pair<String, ?>> availableUpgrades = upgradableSpell.getUpgradeOptions(args);
            Pair<String, ?> upgrade = availableUpgrades.get(this.upgradeId);

            if (upgrade != null) {
                GrabBag updatedArgs = applyUpgrade(args, upgrade.getLeft(), upgrade.getRight());
                NbtCompound updatedNbt = GrabBag.toNBT(updatedArgs);

                UnlockSpellComponent newComponent = new UnlockSpellComponent(
                        component.advancement(),
                        component.textureInt(),
                        component.id(),
                        updatedNbt.asString()
                );

                scrollStack.set(ModComponents.UNLOCK_SPELL_COMPONENT, newComponent);
                UnlockScrollCache.invalidate(scrollStack);

                return scrollStack;
            }
        }

        return ItemStack.EMPTY;
    }

    private GrabBag applyUpgrade(GrabBag current, String key, Object newValue) {
        Map<String, Object> map = new HashMap<>();
        for (String k : current.getKeys()) {
            Class<?> type = current.getType(k);
            if (type == Integer.class) map.put(k, current.getInt(k));
            else if (type == Float.class) map.put(k, current.getFloat(k));
            else if (type == Double.class) map.put(k, current.getDouble(k));
            else if (type == Boolean.class) map.put(k, current.getBoolean(k));
            else if (type == String.class) map.put(k, current.getString(k));
            else if (type == Long.class) map.put(k, current.getLong(k));
            else if (type == Short.class) map.put(k, current.getShort(k));
            else if (type == Byte.class) map.put(k, current.getByte(k));
        }

        map.put(key, newValue);
        return GrabBag.fromMap(map);
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registries) {
        return ItemStack.EMPTY;
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
        return ModRecipes.SPELL_UPGRADE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SCROLL_TABLE_RECIPE_TYPE;
    }

    public RecipeBookCategory getCategory() {
        return RecipeBookCategory.BTC_SCROLL_TABLE;
    }

    public static class Serializer implements RecipeSerializer<SpellUpgradeRecipe> {
        public static final MapCodec<SpellUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        RawShapedRecipe.CODEC.forGetter(SpellUpgradeRecipe::getRawPattern),
                        Identifier.CODEC.fieldOf("upgrade").forGetter(SpellUpgradeRecipe::getUpgradeId)
                ).apply(instance, SpellUpgradeRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, SpellUpgradeRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                (buf, recipe) -> {
                    RawShapedRecipe.PACKET_CODEC.encode(buf, recipe.rawPattern);
                    Identifier.PACKET_CODEC.encode(buf, recipe.upgradeId);
                },
                buf -> new SpellUpgradeRecipe(
                        RawShapedRecipe.PACKET_CODEC.decode(buf),
                        Identifier.PACKET_CODEC.decode(buf)
                )
        );

        @Override
        public MapCodec<SpellUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, SpellUpgradeRecipe> packetCodec() {
            return PACKET_CODEC;
        }
    }
}
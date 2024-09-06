package io.github.tobyrue.btc;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotions {

    public static final Potion ANTI_PLACE =
            Registry.register(
                    Registries.POTION,
                    Identifier.of("btc", "anti_place"),
                    new Potion(
                            new StatusEffectInstance(
                                    Registries.STATUS_EFFECT.getEntry(BTC.ANTI_PLACE),
                                    3600,
                                    0)));

    public static void initialize() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.SLOWNESS,
                    // Ingredient
                    Items.ENDER_EYE,
                    // Output potion.
                    Registries.POTION.getEntry(ANTI_PLACE)
            );
        });
    }
}

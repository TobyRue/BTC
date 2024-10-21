package io.github.tobyrue.btc;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
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
    public static final Potion DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of("btc", "dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES),
                                    2400,
                                    0)));
    public static final Potion LONG_DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of("btc", "long_dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES),
                                    4800,
                                    0)));

    public static final Potion STRONG_DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of("btc", "strong_dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES),
                                    2400,
                                    1)));
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
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.TURTLE_MASTER,
                    // Ingredient
                    ModItems.DRAGON_ROD,
                    // Output potion.
                    Registries.POTION.getEntry(DRAGON_SCALES)
            );
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Registries.POTION.getEntry(DRAGON_SCALES),
                    // Ingredient
                    Items.REDSTONE,
                    // Output potion.
                    Registries.POTION.getEntry(LONG_DRAGON_SCALES)
            );
        });
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Registries.POTION.getEntry(DRAGON_SCALES),
                    // Ingredient
                    Items.GLOWSTONE_DUST,
                    // Output potion.
                    Registries.POTION.getEntry(STRONG_DRAGON_SCALES)
            );
        });
    }
}

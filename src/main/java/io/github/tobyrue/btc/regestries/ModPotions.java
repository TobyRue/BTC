package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModPotions {
    public static final Potion BUILDER_BLUNDER =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(BTC.MOD_ID, "builder_blunder"),
                    new Potion(
                            new StatusEffectInstance(
                                    ModStatusEffects.BUILDER_BLUNDER,
                                    3600,
                                    0)));
    public static final Potion DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(BTC.MOD_ID, "dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    ModStatusEffects.DRAGON_SCALES,
                                    2400,
                                    0)));
    public static final Potion LONG_DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(BTC.MOD_ID, "long_dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    ModStatusEffects.DRAGON_SCALES,
                                    4800,
                                    0)));
    public static final Potion STRONG_DRAGON_SCALES =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(BTC.MOD_ID, "strong_dragon_scales"),
                    new Potion(
                            new StatusEffectInstance(
                                    ModStatusEffects.DRAGON_SCALES,
                                    2400,
                                    1)));
    public static final Potion FROST_WALKER =
            Registry.register(
                    Registries.POTION,
                    Identifier.of(BTC.MOD_ID, "frost_walker"),
                    new Potion(
                            new StatusEffectInstance(
                                    ModStatusEffects.FROST_WALKER,
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
                    Registries.POTION.getEntry(BUILDER_BLUNDER)
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
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(
                    // Input potion.
                    Potions.WATER,
                    // Ingredient
                    Items.BLUE_ICE,
                    // Output potion.
                    Registries.POTION.getEntry(FROST_WALKER)
            );
        });
    }
}

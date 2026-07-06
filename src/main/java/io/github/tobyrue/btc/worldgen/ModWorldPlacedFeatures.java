package io.github.tobyrue.btc.worldgen;

import io.github.tobyrue.btc.BTC;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModWorldPlacedFeatures {
    public static final RegistryKey<PlacedFeature> SALT_PLACED_KEY = registerKey("ore_salt");

    public static List<PlacementModifier> make(PlacementModifier rarityModifier, PlacementModifier biomeFilter, PlacementModifier surfaceFilter) {
        return List.of(
                rarityModifier,
                biomeFilter,
                SquarePlacementModifier.of(),
                surfaceFilter
        );
    }

    public static void configure(Registerable<PlacedFeature> context) {
        RegistryEntryLookup<ConfiguredFeature<?, ?>> configuredFeatures = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);

        register(
                context,
                SALT_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SALT_CONFIG_KEY),
                make(
                        RarityFilterPlacementModifier.of(8),
                        BiomePlacementModifier.of(),
                        HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG)
                )
        );
    }

    public static RegistryKey<PlacedFeature> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(BTC.MOD_ID, name));
    }

    private static void register(Registerable<PlacedFeature> context, RegistryKey<PlacedFeature> key, RegistryEntry<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
package io.github.tobyrue.btc.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.gen.GenerationStep;

public class OreMaker {
    public static void generateOres() {
        BiomeModifications.addFeature(
                BiomeSelectors.all(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                ModWorldPlacedFeatures.SALT_PLACED_KEY
        );
    }
}

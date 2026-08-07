package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.worldgen.feature.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<SaltPatchFeatureConfig> SALT_PATCH = new SaltPatchFeature(SaltPatchFeatureConfig.CODEC);
    public static final Feature<SaltSpringFeatureConfig> SALT_SPRING = new SaltSpringFeature(SaltSpringFeatureConfig.CODEC);
    public static final Feature<CaveWallCrystalFeatureConfig> CAVE_WALL_CRYSTAL = new CaveWallCrystalFeature(CaveWallCrystalFeatureConfig.CODEC);

    public static void registerFeatures() {
        Registry.register(Registries.FEATURE, BTC.identifierOf("salt_patch"), SALT_PATCH);
        Registry.register(Registries.FEATURE, BTC.identifierOf("salt_spring"), SALT_SPRING);
        Registry.register(Registries.FEATURE, BTC.identifierOf("cave_wall_crystal"), CAVE_WALL_CRYSTAL);
    }
}
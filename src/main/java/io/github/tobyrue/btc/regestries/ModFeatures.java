package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.worldgen.feature.SaltPatchFeature;
import io.github.tobyrue.btc.worldgen.feature.SaltPatchFeatureConfig;
import io.github.tobyrue.btc.worldgen.feature.SaltSpringFeature;
import io.github.tobyrue.btc.worldgen.feature.SaltSpringFeatureConfig;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {
    public static final Feature<SaltPatchFeatureConfig> SALT_PATCH = new SaltPatchFeature(SaltPatchFeatureConfig.CODEC);
    public static final Feature<SaltSpringFeatureConfig> SALT_SPRING = new SaltSpringFeature(SaltSpringFeatureConfig.CODEC);

    public static void registerFeatures() {
        Registry.register(Registries.FEATURE, BTC.identifierOf("salt_patch"), SALT_PATCH);
        Registry.register(Registries.FEATURE, BTC.identifierOf("salt_spring"), SALT_SPRING);
    }
}
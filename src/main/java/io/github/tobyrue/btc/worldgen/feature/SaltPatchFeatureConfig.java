package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

public record SaltPatchFeatureConfig(
        BlockState tuffState,
        BlockState calciteState,
        BlockState saltyCalciteState,
        BlockState saltState,
        int radiusX,
        int radiusY,
        int radiusZ,
        float saltyCalciteThickness
) implements FeatureConfig {

    public static final Codec<SaltPatchFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("tuff_state").forGetter(SaltPatchFeatureConfig::tuffState),
                    BlockState.CODEC.fieldOf("calcite_state").forGetter(SaltPatchFeatureConfig::calciteState),
                    BlockState.CODEC.fieldOf("salty_calcite_state").forGetter(SaltPatchFeatureConfig::saltyCalciteState),
                    BlockState.CODEC.fieldOf("salt_state").forGetter(SaltPatchFeatureConfig::saltState),
                    Codec.INT.fieldOf("radius_x").orElse(12).forGetter(SaltPatchFeatureConfig::radiusX),
                    Codec.INT.fieldOf("radius_y").orElse(6).forGetter(SaltPatchFeatureConfig::radiusY),
                    Codec.INT.fieldOf("radius_z").orElse(16).forGetter(SaltPatchFeatureConfig::radiusZ),
                    Codec.FLOAT.fieldOf("salty_calcite_thickness").orElse(1.5f).forGetter(SaltPatchFeatureConfig::saltyCalciteThickness)
            ).apply(instance, SaltPatchFeatureConfig::new)
    );
}
package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record SaltSpringFeatureConfig(
        BlockState fluidState,
        BlockState rimState,
        List<BlockState> crystalStates,
        int radius
) implements FeatureConfig {

    public static final Codec<SaltSpringFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("fluid_state").forGetter(SaltSpringFeatureConfig::fluidState),
                    BlockState.CODEC.fieldOf("rim_state").forGetter(SaltSpringFeatureConfig::rimState),
                    BlockState.CODEC.listOf().fieldOf("crystal_states").forGetter(SaltSpringFeatureConfig::crystalStates),
                    Codec.INT.fieldOf("radius").orElse(4).forGetter(SaltSpringFeatureConfig::radius)
            ).apply(instance, SaltSpringFeatureConfig::new)
    );
}
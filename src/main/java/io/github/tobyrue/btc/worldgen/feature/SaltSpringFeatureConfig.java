package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record SaltSpringFeatureConfig(
        BlockState fluidState,
        List<WeightedBlockState> rimStates,
        List<WeightedBlockState> crystalStates,
        int radius
) implements FeatureConfig {

    public record WeightedBlockState(BlockState state, int weight) {
        public static final Codec<WeightedBlockState> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockState.CODEC.fieldOf("state").forGetter(WeightedBlockState::state),
                        Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedBlockState::weight)
                ).apply(instance, WeightedBlockState::new)
        );
    }

    public static final Codec<SaltSpringFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    BlockState.CODEC.fieldOf("fluid_state").forGetter(SaltSpringFeatureConfig::fluidState),
                    WeightedBlockState.CODEC.listOf().fieldOf("rim_states").forGetter(SaltSpringFeatureConfig::rimStates),
                    WeightedBlockState.CODEC.listOf().fieldOf("crystal_states").forGetter(SaltSpringFeatureConfig::crystalStates),
                    Codec.INT.fieldOf("radius").orElse(4).forGetter(SaltSpringFeatureConfig::radius)
            ).apply(instance, SaltSpringFeatureConfig::new)
    );

    /**
     * Helper to pick a weighted random BlockState from a list.
     */
    public static BlockState getRandomState(List<WeightedBlockState> weightedList, Random random, BlockState fallback) {
        if (weightedList.isEmpty()) return fallback;

        int totalWeight = 0;
        for (WeightedBlockState entry : weightedList) {
            totalWeight += Math.max(1, entry.weight());
        }

        if (totalWeight <= 0) return fallback;

        int roll = random.nextInt(totalWeight);
        int currentWeight = 0;

        for (WeightedBlockState entry : weightedList) {
            currentWeight += Math.max(1, entry.weight());
            if (roll < currentWeight) {
                return entry.state();
            }
        }

        return weightedList.get(0).state();
    }
}
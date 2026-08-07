package io.github.tobyrue.btc.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record CaveWallCrystalFeatureConfig(
        List<WeightedBlockState> crystalStates,
        float litChance,
        int placementAttempts
) implements FeatureConfig {

    public record WeightedBlockState(BlockState state, int weight) {
        public static final Codec<WeightedBlockState> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockState.CODEC.fieldOf("state").forGetter(WeightedBlockState::state),
                        Codec.INT.optionalFieldOf("weight", 1).forGetter(WeightedBlockState::weight)
                ).apply(instance, WeightedBlockState::new)
        );
    }

    public static final Codec<CaveWallCrystalFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    WeightedBlockState.CODEC.listOf().fieldOf("crystal_states").forGetter(CaveWallCrystalFeatureConfig::crystalStates),
                    Codec.FLOAT.fieldOf("lit_chance").orElse(0.15f).forGetter(CaveWallCrystalFeatureConfig::litChance),
                    Codec.INT.fieldOf("placement_attempts").orElse(64).forGetter(CaveWallCrystalFeatureConfig::placementAttempts)
            ).apply(instance, CaveWallCrystalFeatureConfig::new)
    );

    public static BlockState getRandomState(List<WeightedBlockState> weightedList, Random random) {
        if (weightedList.isEmpty()) return null;

        int totalWeight = 0;
        for (WeightedBlockState entry : weightedList) {
            totalWeight += Math.max(1, entry.weight());
        }

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
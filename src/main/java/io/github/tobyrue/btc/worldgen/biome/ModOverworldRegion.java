package io.github.tobyrue.btc.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;
import terrablender.api.VanillaParameterOverlayBuilder;

import java.lang.reflect.Parameter;
import java.util.function.Consumer;

public class ModOverworldRegion extends Region {
    public ModOverworldRegion(Identifier name, int weight) {
        super(name, RegionType.OVERWORLD, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<MultiNoiseUtil.NoiseHypercube, RegistryKey<Biome>>> mapper) {
        VanillaParameterOverlayBuilder builder = new VanillaParameterOverlayBuilder();

        new ParameterUtils.ParameterPointListBuilder()
                .temperature(ParameterUtils.Temperature.WARM, ParameterUtils.Temperature.HOT)
                .humidity(ParameterUtils.Humidity.ARID, ParameterUtils.Humidity.DRY)
                .continentalness(
                        ParameterUtils.Continentalness.NEAR_INLAND,
                        ParameterUtils.Continentalness.MID_INLAND,
                        ParameterUtils.Continentalness.FAR_INLAND
                )
                .erosion(
                        ParameterUtils.Erosion.EROSION_3,
                        ParameterUtils.Erosion.EROSION_4,
                        ParameterUtils.Erosion.EROSION_5
                )
                .depth(MultiNoiseUtil.ParameterRange.of(0.6f, 0.95f))
                .weirdness(
                        ParameterUtils.Weirdness.MID_SLICE_VARIANT_DESCENDING,
                        ParameterUtils.Weirdness.HIGH_SLICE_VARIANT_DESCENDING
                )
                .build().forEach(point ->
                        builder.add(point, Biomes.SALT_CAVE)
                );

        builder.build().forEach(mapper);
    }
}
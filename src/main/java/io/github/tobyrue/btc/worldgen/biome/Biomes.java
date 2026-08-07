package io.github.tobyrue.btc.worldgen.biome;

import io.github.tobyrue.btc.BTC;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;

public class Biomes {
    public static final RegistryKey<Biome> SALT_CAVE = register("salt_caves");

    private static RegistryKey<Biome> register(String name)
    {
        return RegistryKey.of(RegistryKeys.BIOME, BTC.identifierOf(name));
    }
}
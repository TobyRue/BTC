package io.github.tobyrue.btc;


import io.github.tobyrue.btc.datagen.*;
import io.github.tobyrue.btc.worldgen.ModConfiguredFeatures;
import io.github.tobyrue.btc.worldgen.ModWorldPlacedFeatures;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class BTCDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
//        pack.addProvider(ModRecipeProvider::new);
//        pack.addProvider(ModBlockLootTableProvider::new);
//        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModModelProvider::new);
//        pack.addProvider(ModWorldGenProvider::new);
    }

//    @Override
//    public void buildRegistry(RegistryBuilder registryBuilder) {
//        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModConfiguredFeatures::configure);
//        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModWorldPlacedFeatures::configure);
//        registryBuilder.addRegistry(RegistryKeys.TRIM_PATTERN, ModTrimPatterns::bootstrap);
//    }
}

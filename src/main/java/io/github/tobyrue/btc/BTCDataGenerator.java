package io.github.tobyrue.btc;


import io.github.tobyrue.btc.datagen.ModBlockLootTableProvider;
import io.github.tobyrue.btc.datagen.ModBlockTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class BTCDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
    }

//    @Override
//    public void buildRegistry(RegistryBuilder registryBuilder) {
//        registryBuilder.addRegistry(RegistryKeys.TRIM_PATTERN, ModTrimPatterns::bootstrap);
//    }
}

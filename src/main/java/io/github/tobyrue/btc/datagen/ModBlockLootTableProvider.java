package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {

    public ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.CHARGED_COPPER);
        addDrop(ModBlocks.EXPOSED_CHARGED_COPPER);
        addDrop(ModBlocks.WEATHERED_CHARGED_COPPER);
        addDrop(ModBlocks.OXIDIZED_CHARGED_COPPER);
        addDrop(ModBlocks.WAXED_CHARGED_COPPER);
        addDrop(ModBlocks.WAXED_EXPOSED_CHARGED_COPPER);
        addDrop(ModBlocks.WAXED_WEATHERED_CHARGED_COPPER);
        addDrop(ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER);
    }
}
package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {

    public ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.CHARGED_COPPER, ModBlocks.EXPOSED_CHARGED_COPPER,
                ModBlocks.WEATHERED_CHARGED_COPPER, ModBlocks.OXIDIZED_CHARGED_COPPER,
                ModBlocks.WAXED_CHARGED_COPPER, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER,
                ModBlocks.WAXED_WEATHERED_CHARGED_COPPER, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER);
    }

    public void addDrop(Block... blocks) {
        for (var block : blocks) {
            addDrop(block);
        }
    }
}
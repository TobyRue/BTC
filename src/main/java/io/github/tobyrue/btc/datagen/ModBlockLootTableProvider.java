package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModBlockLootTableProvider extends FabricBlockLootTableProvider {

    public ModBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(
                ModBlocks.TUFF_PILASTER,
                ModBlocks.TUFF_PILLAR,
                ModBlocks.TUFF_BRICK_PILASTER,
                ModBlocks.TUFF_BRICKS_PILLAR,
                ModBlocks.CHISELED_TUFF_BRICKS_PILASTER,
                ModBlocks.CHISELED_TUFF_BRICKS_PILLAR,
                ModBlocks.POLISHED_TUFF_PILASTER,
                ModBlocks.POLISHED_TUFF_PILLAR,
                ModBlocks.STONE_PILASTER,
                ModBlocks.STONE_PILLAR,
                ModBlocks.COBBLESTONE_PILASTER,
                ModBlocks.COBBLESTONE_PILLAR,
                ModBlocks.MOSSY_COBBLESTONE_PILASTER,
                ModBlocks.MOSSY_COBBLESTONE_PILLAR,
                ModBlocks.STONE_BRICKS_PILASTER,
                ModBlocks.STONE_BRICKS_PILLAR,
                ModBlocks.MOSSY_STONE_BRICKS_PILASTER,
                ModBlocks.MOSSY_STONE_BRICKS_PILLAR,
                ModBlocks.CRACKED_STONE_BRICKS_PILASTER,
                ModBlocks.CRACKED_STONE_BRICKS_PILLAR,
                ModBlocks.DEEPSLATE_PILASTER,
                ModBlocks.DEEPSLATE_PILLAR,
                ModBlocks.COBBLED_DEEPSLATE_PILASTER,
                ModBlocks.COBBLED_DEEPSLATE_PILLAR,
                ModBlocks.POLISHED_DEEPSLATE_PILASTER,
                ModBlocks.POLISHED_DEEPSLATE_PILLAR,
                ModBlocks.DEEPSLATE_BRICKS_PILASTER,
                ModBlocks.DEEPSLATE_BRICKS_PILLAR,
                ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILASTER,
                ModBlocks.CRACKED_DEEPSLATE_BRICKS_PILLAR,
                ModBlocks.DEEPSLATE_TILES_PILASTER,
                ModBlocks.DEEPSLATE_TILES_PILLAR,
                ModBlocks.CRACKED_DEEPSLATE_TILES_PILASTER,
                ModBlocks.CRACKED_DEEPSLATE_TILES_PILLAR,
                ModBlocks.CHISELED_DEEPSLATE_PILASTER,
                ModBlocks.CHISELED_DEEPSLATE_PILLAR
        );
    }

    public void addDrop(Block... blocks) {
        for (var block : blocks) {
            addDrop(block);
        }
    }
}
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
        addDrop(ModBlocks.GUNPOWDER_BARREL);
        addDrop(ModBlocks.KILL_BALL_RECEPTOR);
        addDrop(ModBlocks.BELLOW);
        addDrop(ModBlocks.BRAZIER);
        addDrop(ModBlocks.OMINOUS_BEACON);
        addDrop(ModBlocks.MOB_DETECTOR);
        addDrop(ModBlocks.DUNGEON_PRESSURE_PLATE);
        addDrop(ModBlocks.POLISHED_TUFF_PRESSURE_PLATE);
        addDrop(ModBlocks.DUNGEON_BUTTON);
        addDrop(ModBlocks.PEDESTAL);
        addDrop(ModBlocks.KEY_DISPENSER_BLOCK);
        addDrop(ModBlocks.KEY_ACCEPTOR);
        addDrop(ModBlocks.DUNGEON_DOOR);
        addDrop(ModBlocks.DUNGEON_FLAME);
        addDrop(ModBlocks.DEEP_FLAME);
        addDrop(ModBlocks.FORTRESS_FLAME);

        addDrop(ModBlocks.UNOXIDIZED_COPPER_BUTTON);
        addDrop(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
        addDrop(ModBlocks.EXPOSED_COPPER_BUTTON);
        addDrop(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
        addDrop(ModBlocks.WEATHERED_COPPER_BUTTON);
        addDrop(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
        addDrop(ModBlocks.OXIDIZED_COPPER_BUTTON);
        addDrop(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);

        addDrop(ModBlocks.COPPER_TRIAL_FAN);
        addDrop(ModBlocks.EXPOSED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.WEATHERED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.WAXED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN);
        addDrop(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN);

        addDrop(ModBlocks.CHISELED_TUFF_BRICKS_PILLAR);
        addDrop(ModBlocks.TUFF_PILLAR);
        addDrop(ModBlocks.STONE_PILLAR);
        addDrop(ModBlocks.CRACKED_STONE_BRICKS_PILLAR);
        addDrop(ModBlocks.STONE_BRICKS_PILLAR);
        addDrop(ModBlocks.POLISHED_TUFF_PILLAR);
        addDrop(ModBlocks.TUFF_BRICKS_PILLAR);
        addDrop(ModBlocks.STONE_PILASTER);
        addDrop(ModBlocks.STONE_BRICKS_PILASTER);
        addDrop(ModBlocks.CRACKED_STONE_BRICKS_PILASTER);
        addDrop(ModBlocks.TUFF_BRICK_PILASTER);
        addDrop(ModBlocks.CHISELED_TUFF_BRICKS_PILASTER);
        addDrop(ModBlocks.POLISHED_TUFF_PILASTER);
        addDrop(ModBlocks.TUFF_PILASTER);
    }
}
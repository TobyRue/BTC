package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.KILL_BALL_RECEPTOR)
                .add(ModBlocks.COPPER_TRIAL_FAN)
                .add(ModBlocks.EXPOSED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WEATHERED_COPPER_TRIAL_FAN)
                .add(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN)
                .add(ModBlocks.CHISELED_TUFF_BRICKS_PILLAR)
                .add(ModBlocks.TUFF_PILLAR)
                .add(ModBlocks.STONE_PILLAR)
                .add(ModBlocks.CRACKED_STONE_BRICKS_PILLAR)
                .add(ModBlocks.STONE_BRICKS_PILLAR)
                .add(ModBlocks.POLISHED_TUFF_PILLAR)
                .add(ModBlocks.TUFF_BRICKS_PILLAR)
                .add(ModBlocks.STONE_PILASTER)
                .add(ModBlocks.STONE_BRICKS_PILASTER)
                .add(ModBlocks.CRACKED_STONE_BRICKS_PILASTER)
                .add(ModBlocks.TUFF_BRICK_PILASTER)
                .add(ModBlocks.CHISELED_TUFF_BRICKS_PILASTER)
                .add(ModBlocks.POLISHED_TUFF_PILASTER)
                .add(ModBlocks.TUFF_PILASTER)
                .add(ModBlocks.BRAZIER)
                .add(ModBlocks.OMINOUS_BEACON)
                .add(ModBlocks.MOB_DETECTOR)
                .add(ModBlocks.DUNGEON_PRESSURE_PLATE)
                .add(ModBlocks.POLISHED_TUFF_PRESSURE_PLATE)
                .add(ModBlocks.PEDESTAL)
                .add(ModBlocks.KEY_DISPENSER_BLOCK)
                .add(ModBlocks.KEY_ACCEPTOR)
                .add(ModBlocks.DUNGEON_FLAME)
                .add(ModBlocks.DEEP_FLAME)
                .add(ModBlocks.FORTRESS_FLAME)
                .add(ModBlocks.DUNGEON_DOOR);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.GUNPOWDER_BARREL)
                .add(ModBlocks.BELLOW)
                .add(ModBlocks.DUNGEON_DOOR);

        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.KILL_BALL_RECEPTOR)
                .add(ModBlocks.COPPER_TRIAL_FAN)
                .add(ModBlocks.EXPOSED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WEATHERED_COPPER_TRIAL_FAN)
                .add(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN)
                .add(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN)
                .add(ModBlocks.BRAZIER)
                .add(ModBlocks.OMINOUS_BEACON)
                .add(ModBlocks.MOB_DETECTOR)
                .add(ModBlocks.DUNGEON_DOOR)
                .add(ModBlocks.DUNGEON_FLAME)
                .add(ModBlocks.DEEP_FLAME)
                .add(ModBlocks.FORTRESS_FLAME);
    }
}
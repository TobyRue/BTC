package io.github.tobyrue.btc.datagen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.data.family.BlockFamily;
import net.minecraft.item.Item;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        BlockStateModelGenerator.BlockTexturePool texturePool =
//                blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.REINFORCED_DUNGEON_TILES);
//
//        texturePool.stairs(ModBlocks.REINFORCED_DUNGEON_TILE_STAIRS);
//        texturePool.slab(ModBlocks.REINFORCED_DUNGEON_TILE_SLAB);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.REINFORCED_DUNGEON_GRATE);
    }
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @Override
    public String getName() {
        return "ModModelProvider";
    }
}
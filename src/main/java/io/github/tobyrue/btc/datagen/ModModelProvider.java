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
        this.registerCopperBlocks(blockStateModelGenerator, ModBlocks.CHARGED_COPPER, ModBlocks.WAXED_CHARGED_COPPER);
        this.registerCopperBlocks(blockStateModelGenerator, ModBlocks.EXPOSED_CHARGED_COPPER, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER);
        this.registerCopperBlocks(blockStateModelGenerator, ModBlocks.WEATHERED_CHARGED_COPPER, ModBlocks.WAXED_WEATHERED_CHARGED_COPPER);
        this.registerCopperBlocks(blockStateModelGenerator, ModBlocks.OXIDIZED_CHARGED_COPPER, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER);
    }

    public final void registerCopperBlocks(BlockStateModelGenerator blockStateModelGenerator, Block unwaxedCopperBlock, Block waxedCopperBlock) {
        Identifier identifier = Models.CUBE_ALL.upload(unwaxedCopperBlock, TextureMap.all(unwaxedCopperBlock), blockStateModelGenerator.modelCollector);

        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(unwaxedCopperBlock, identifier)
        );

        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(waxedCopperBlock, identifier)
        );

//        blockStateModelGenerator.registerParentedItemModel(unwaxedCopperBlock, identifier);

        blockStateModelGenerator.registerParentedItemModel(waxedCopperBlock, identifier);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }

    @Override
    public String getName() {
        return "ModModelProvider";
    }
}
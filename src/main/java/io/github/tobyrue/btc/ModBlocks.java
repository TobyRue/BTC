package io.github.tobyrue.btc;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block register(Block block, String name, boolean shouldRegisterItem) {
    // Register the block and its item.
        Identifier id = Identifier.of(BTC.MOD_ID, name);

    // Sometimes, you may not want to register an item for the block.
    // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
    if (shouldRegisterItem) {
        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, id, blockItem);
    }

    return Registry.register(Registries.BLOCK, id, block);
}
    public static final OminousBeaconBlock OMINOUS_BEACON = (OminousBeaconBlock) register(
            new OminousBeaconBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).luminance((state) -> {
                return 14;
            })),
            "ominous_beacon",
            true
    );
    public static final PedestalBlock PEDESTAL = (PedestalBlock) register(
            new PedestalBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 12;
            }).strength(-1.0F, 3600000.0F)),
            "pedestal",
            true
    );

    public static void initialize() {
    }

}

package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.HoneycombItem;

public class ModWaxings {
    public static void initialize() {
        //WAXING
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);

        //UN-WAXING
        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON, ModBlocks.UNOXIDIZED_COPPER_BUTTON);
        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON);
        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON);
        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON);

        //OXIDIZING AND DE-OXIDIZING
        Oxidizable.OXIDATION_LEVEL_INCREASES.get().put(ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON);
        Oxidizable.OXIDATION_LEVEL_INCREASES.get().put(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON);
        Oxidizable.OXIDATION_LEVEL_INCREASES.get().put(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON);
    }
}

package io.github.tobyrue.btc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroups;

public class BTC implements ModInitializer {
    static String MOD_ID = "btc";
    @Override
    public void onInitialize() {
        System.out.println("hello world");
        ModBlocks.initialize();
        ModItems.initialize();
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.RUBY_TRIAL_KEY));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.OMINOUS_BEACON));
    }
}

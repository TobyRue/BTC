package io.github.tobyrue.btc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.RailBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.item.ItemGroups;

public class BTC implements ModInitializer {
    public static String MOD_ID = "btc";
    @Override
    public void onInitialize() {

        System.out.println("hello world");
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntities.initialize();

        //INGREDIENTS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.RUBY_TRIAL_KEY));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.STAFF));

        //FUNCTIONAL
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.OMINOUS_BEACON));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.PEDESTAL));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.ANTI_MINE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_FIRE));

        //COMBAT
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WIND_STAFF));

        //REDSTONE
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_DOOR));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_PRESSURE_PLATE));
    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", java.util.Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
}

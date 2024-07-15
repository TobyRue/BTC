package io.github.tobyrue.btc;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class BTC implements ModInitializer {
    static String MOD_ID = "btc";
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

        //COMBAT
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.STAFF));

    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", java.util.Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
}

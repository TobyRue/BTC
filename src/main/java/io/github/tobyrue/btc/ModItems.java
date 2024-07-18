package io.github.tobyrue.btc;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(BTC.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }
    public static final Item RUBY_TRIAL_KEY = register(
            new Item(new Item.Settings()),
            "ruby_trial_key"
    );
    public static final Item STAFF = register(
            new Item(new Item.Settings().maxCount(1)),
            "staff"
    );
    public static final Item WIND_STAFF = register(
        new WindStaffItem(new Item.Settings().maxCount(1)),
            "wind_staff"
    );


    public static void initialize() {
    }
}
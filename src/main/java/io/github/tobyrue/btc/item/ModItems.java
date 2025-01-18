package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.*;
import io.github.tobyrue.btc.entity.ModEntities;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static<T extends Item> T register(T item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(BTC.MOD_ID, id);

        // Register the item.
        T registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }
    public static final Item RUBY_TRIAL_KEY = register(
            new Item(new Item.Settings()),
            "ruby_trial_key"
    );
    public static final Item IRON_WRENCH = register(new IronWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.COMMON)),
        "iron_wrench"
    );
    public static final Item GOLD_WRENCH = register(new GoldWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)),
            "gold_wrench"
    );
    public static final Item DRAGON_ROD = register(new Item(new Item.Settings().rarity(Rarity.RARE)),
            "dragon_rod"
    );
    public static final StaffItem STAFF = register(
            new StaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "staff"
    );
    public static final StaffItem WIND_STAFF = register(
        new WindStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "wind_staff"
    );
    public static final StaffItem FIRE_STAFF = register(
            new FireStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "fire_staff"
    );
    public static final StaffItem DRAGON_STAFF = register(
            new DragonStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "dragon_staff"
    );
    public static final Item ELDRITCH_LUMINARY_SPAWN_EGG = register(
            new SpawnEggItem(ModEntities.ELDRITCH_LUMINARY, 0x37BBDD, 0xCC9104, new Item.Settings()),
            "eldritch_luminary_spawn_egg"
    );
    public static final Item WATER_BLAST = register(
            new WaterBlastItem(new Item.Settings().maxCount(64).rarity(Rarity.COMMON)),
            "water_blast"
    );
    public static void initialize() {
    }
}
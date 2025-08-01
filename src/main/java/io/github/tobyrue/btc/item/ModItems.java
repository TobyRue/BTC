package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.*;
import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
//    public static final Map<SpellRegistryEnum, SpellScrollItem> SPELL_ITEMS = new HashMap<>();

    public static<T extends Item> T register(T item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(BTC.MOD_ID, id);

        // Register the item.
        T registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

//    static {
//        for (SpellRegistryEnum spell : SpellRegistryEnum.values()) {
//            if (!spell.hasNoScroll) {
//                SpellScrollItem item = new SpellScrollItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).maxCount(1), spell);
//                SPELL_ITEMS.put(spell, register(item, spell.toString() + "_scroll"));
//            }
//        }
//    }

    public static final Item EMPTY_SCROLL = register(
            new Item(new Item.Settings()),
            "empty_scroll"
    );
    public static final Item TEST = register(
            new ScreenTestItem(new Item.Settings()),
            "test_screen"
    );
    public static final Item SPELLSTONE = register(
            new SpellstoneItem(new Item.Settings().maxCount(1 )),
            "spellstone"
    );
    public static final Item RUBY_TRIAL_KEY = register(
            new Item(new Item.Settings()),
            "ruby_trial_key"
    );
    public static final Item COPPER_WRENCH = register(new WrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.COMMON)),
            "copper_wrench"
    );
    public static final Item IRON_WRENCH = register(new IronWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.COMMON)),
        "iron_wrench"
    );
    public static final Item GOLD_WRENCH = register(new GoldWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)),
            "gold_wrench"
    );
    public static final Item CREATIVE_WRENCH = register(new CreativeWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)),
            "creative_wrench"
    );
    public static final Item DRAGON_ROD = register(new Item(new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)),
            "dragon_rod"
    );
    public static final Item ENCHANTED_PAPER = register(new Item(new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)),
            "enchanted_paper"
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
    public static final StaffItem WATER_STAFF = register(
            new WaterStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "water_staff"
    );
    public static final StaffItem EARTH_STAFF = register(
            new EarthStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "earth_staff"
    );
    public static final Item ELDRITCH_LUMINARY_SPAWN_EGG = register(
            new SpawnEggItem(ModEntities.ELDRITCH_LUMINARY, 0x37BBDD, 0xCC9104, new Item.Settings()),
            "eldritch_luminary_spawn_egg"
    );
    public static final Item COPPER_GOLEM_SPAWN_EGG = register(
            new SpawnEggItem(ModEntities.COPPER_GOLEM, 0xC87456, 0x8A422B, new Item.Settings()),
            "copper_golem_spawn_egg"
    );
    public static final Item TUFF_GOLEM_SPAWN_EGG = register(
            new SpawnEggItem(ModEntities.TUFF_GOLEM, 0x636C6D, 0xC6881B, new Item.Settings()),
            "tuff_golem_spawn_egg"
    );
    public static final Item WATER_BLAST = register(
            new WaterBlastItem(new Item.Settings().maxCount(64).rarity(Rarity.UNCOMMON)),
            "water_blast"
    );
    public static final Item SPELL_BOOK = register(
            new SpellBookItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "spell_book_item"
    );

    public static void initialize() {
    }
}
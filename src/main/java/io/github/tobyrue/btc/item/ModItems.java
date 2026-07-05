package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.*;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModSounds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(BTC.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

    public static final Item COOKED_MEAT_CLUB = register(
            new Item(new Item.Settings().maxCount(1).food(ModComponents.COOKED_MEAT_CLUB).attributeModifiers(AxeItem.createAttributeModifiers(ToolMaterials.STONE, 7.0F, -3.2F))),
            "cooked_meat_club"
    );

    public static final Item RAW_MEAT_CLUB = register(
            new Item(new Item.Settings().maxCount(1).food(ModComponents.RAW_MEAT_CLUB).attributeModifiers(AxeItem.createAttributeModifiers(ToolMaterials.WOOD, 6.0F, -3.2F))),
            "raw_meat_club"
    );

    public static final Item TRIAL_JERKY = register(
            new Item(new Item.Settings().food(ModComponents.TRIAL_JERKY)),
            "trial_jerky"
    );

    public static final Item AMETHYST_LENS = register(
            new AmethystLensItem(new Item.Settings().maxCount(1)),
            "amethyst_lens"
    );

    public static final Item ELDRITCH_ARMOR_TRIM = register(
            SmithingTemplateItem.of(BTC.identifierOf("eldritch"), FeatureFlags.VANILLA),
            "eldritch_armor_trim_smithing_template"
    );
    public static final Item SUN_ARMOR_TRIM = register(
            SmithingTemplateItem.of(BTC.identifierOf("sun"), FeatureFlags.VANILLA),
            "sun_armor_trim_smithing_template"
    );
    public static final Item UNBREAKABLE_UPGRADE_TEMPLATE = register(
            new Item(new Item.Settings().maxCount(1)),
            "unbreakable_upgrade_template"
    );
    public static final Item PET_TOTEM = register(
            new PetTotemItem(new Item.Settings().maxCount(1)),
            "pet_totem"
    );
    public static final Item BLOCK_KEY = register(
            new BlockKeyItem(new Item.Settings().maxCount(1)),
            "block_key"
    );

    public static final Item SELECTOR = register(
            new SelectorItem(new Item.Settings().maxCount(1)),
            "selector"
    );

    public static final Item UNLOCK_SCROLL = register(
            new UnlockScrollItem(),
            "unlock_scroll"
    );

    public static final Item EMPTY_SCROLL = register(
            new Item(new Item.Settings()),
            "empty_scroll"
    );
//    public static final Item TEST = register(
//            new ScreenTestItem(new Item.Settings()),
//            "test_screen"
//    );
    public static final Item SCOPED_CROSSBOW = register(
            new ScopedCrossbow(new Item.Settings().maxCount(1)),
            "scoped_crossbow"
    );
    public static final Item SPELLSTONE = register(
            new SpellstoneItem(new Item.Settings().maxCount(1 )),
            "spellstone"
    );
    public static final Item RUBY_TRIAL_KEY = register(
            new Item(new Item.Settings()),
            "ruby_trial_key"
    );
    public static final Item COPPER_WRENCH = register(new WrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.COMMON).component(ModComponents.WRENCH_TYPE, WrenchType.ROTATE).component(ModComponents.WRENCH_SUBTYPE, WrenchType.WireSubtype.CONNECTION).component(ModComponents.WRENCH_CONNECTION, "null").component(ModComponents.WRENCH_OPERATOR, "null").component(ModComponents.WRENCH_DELAY, -1)),
            "copper_wrench"
    );
//    public static final Item IRON_WRENCH = register(new IronWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.COMMON)),
//        "iron_wrench"
//    );
//    public static final Item GOLD_WRENCH = register(new GoldWrenchItem(new Item.Settings().maxCount(1).rarity(Rarity.RARE)),
//            "gold_wrench"
//    );

    public static final Item DRAGON_ROD = register(new Item(new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)),
            "dragon_rod"
    );
    public static final Item ENCHANTED_PAPER = register(new Item(new Item.Settings().rarity(Rarity.RARE).component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)),
            "enchanted_paper"
    );
    public static final Item STAFF = register(
            new StaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "staff"
    );
    public static final Item WIND_STAFF = register(
        new io.github.tobyrue.btc.item.staffs.WindStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "wind_staff"
    );
    public static final Item FIRE_STAFF = register(
            new io.github.tobyrue.btc.item.staffs.FireStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "fire_staff"
    );
    public static final Item DRAGON_STAFF = register(
            new io.github.tobyrue.btc.item.staffs.DragonStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "dragon_staff"
    );
    public static final Item WATER_STAFF = register(
            new io.github.tobyrue.btc.item.staffs.WaterStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "water_staff"
    );
    public static final Item EARTH_STAFF = register(
            new io.github.tobyrue.btc.item.staffs.EarthStaffItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
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
            new WaterBlastItem(new Item.Settings().rarity(Rarity.UNCOMMON)),
            "water_blast"
    );
    public static final Item SPELL_BOOK = register(
            new SpellBookItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)),
            "spell_book_item"
    );

    public static final Item CRYSTAL_FOREST_MUSIC_DISC = register(
            new Item(new Item.Settings().maxCount(1).rarity(Rarity.RARE).jukeboxPlayable(ModSounds.CRYSTAL_FOREST_KEY)),
            "crystal_forest_music_disc"
    );


    public static void initialize() {

    }
}
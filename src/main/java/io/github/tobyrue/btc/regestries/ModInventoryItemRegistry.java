package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class ModInventoryItemRegistry {
    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.OMINOUS_TRIAL_KEY, ModItems.RUBY_TRIAL_KEY);
            content.addAfter(ModItems.RUBY_TRIAL_KEY, ModItems.STAFF);
            content.addAfter(ModItems.STAFF, ModItems.DRAGON_ROD);
            content.addAfter(Items.PAPER, ModItems.ENCHANTED_PAPER);
            content.addAfter(ModItems.ENCHANTED_PAPER, ModItems.EMPTY_SCROLL);
            content.addAfter(ModItems.EMPTY_SCROLL, ModItems.UNLOCK_SCROLL);

//            for (SpellScrollItem spell : ModItems.SPELL_ITEMS.values()) {
//                if (!spell.spellType.hasNoScroll) {
//                    content.addAfter(ModItems.ENCHANTED_PAPER, spell);
//                }
//            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(content -> {
            content.addAfter(Items.LIGHT, ModItems.TEST);
            content.addAfter(ModItems.TEST, ModItems.SPELLSTONE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.NETHERITE_HOE, ModItems.COPPER_WRENCH);
            content.addAfter(Items.MUSIC_DISC_PIGSTEP, ModItems.CRYSTAL_FOREST_MUSIC_DISC);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Blocks.BEACON, ModBlocks.OMINOUS_BEACON);
            content.addAfter(Blocks.ENCHANTING_TABLE, ModBlocks.PEDESTAL);
            content.addAfter(ModBlocks.PEDESTAL, ModBlocks.KEY_DISPENSER_BLOCK);
            content.addAfter(ModBlocks.KEY_DISPENSER_BLOCK, ModBlocks.ANTIER);
            content.addAfter(ModBlocks.ANTIER, ModBlocks.DUNGEON_DOOR);
            content.addAfter(ModBlocks.DUNGEON_DOOR, ModBlocks.FIRE_DISPENSER);
            content.addAfter(ModBlocks.FIRE_DISPENSER, ModBlocks.DUNGEON_FIRE);
            content.addAfter(ModBlocks.FIRE_DISPENSER, ModBlocks.DUNGEON_WIRE_LEGACY);
            content.addAfter(ModBlocks.DUNGEON_WIRE_LEGACY, ModBlocks.COPPER_WIRE_LEGACY);
            content.addAfter(ModBlocks.COPPER_WIRE_LEGACY, ModItems.IRON_WRENCH);
            content.addAfter(ModItems.IRON_WRENCH, ModItems.GOLD_WRENCH);
            content.addAfter(Blocks.VAULT, ModBlocks.MELTING_ICE);

        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.MACE, ModItems.STAFF);
            content.addAfter(ModItems.STAFF, ModItems.WIND_STAFF);
            content.addAfter(ModItems.WIND_STAFF, ModItems.FIRE_STAFF);
            content.addAfter(ModItems.FIRE_STAFF, ModItems.EARTH_STAFF);
            content.addAfter(ModItems.EARTH_STAFF, ModItems.WATER_STAFF);
            content.addAfter(ModItems.WATER_STAFF, ModItems.DRAGON_STAFF);
            content.addAfter(ModItems.DRAGON_STAFF, ModItems.SPELL_BOOK);
            content.addAfter(Items.WIND_CHARGE, ModItems.WATER_BLAST);
            content.addAfter(Items.CROSSBOW, ModItems.SCOPED_CROSSBOW);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.addAfter(Blocks.REDSTONE_LAMP, ModBlocks.DUNGEON_WIRE_LEGACY);
            content.addAfter(ModBlocks.DUNGEON_WIRE_LEGACY, ModBlocks.COPPER_WIRE_LEGACY);
            content.addAfter(ModBlocks.COPPER_WIRE_LEGACY, ModBlocks.KEY_DISPENSER_BLOCK);
            content.addAfter(ModBlocks.KEY_DISPENSER_BLOCK, ModBlocks.DUNGEON_DOOR);
            content.addAfter(ModBlocks.DUNGEON_DOOR, ModBlocks.FIRE_DISPENSER);
            content.addAfter(Blocks.STONE_PRESSURE_PLATE, ModBlocks.DUNGEON_PRESSURE_PLATE);
            content.addAfter(Blocks.REDSTONE_LAMP, ModBlocks.DUNGEON_WIRE_LEGACY);
            content.addAfter(Blocks.STONE_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.addAfter(Items.EVOKER_SPAWN_EGG, ModItems.ELDRITCH_LUMINARY_SPAWN_EGG);
            content.addAfter(Items.IRON_GOLEM_SPAWN_EGG, ModItems.COPPER_GOLEM_SPAWN_EGG);
            content.addAfter(ModItems.COPPER_GOLEM_SPAWN_EGG, ModItems.TUFF_GOLEM_SPAWN_EGG);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.addAfter(Blocks.WAXED_OXIDIZED_COPPER_BULB, ModBlocks.UNOXIDIZED_COPPER_BUTTON);
            content.addAfter(ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON);
            content.addAfter(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
            content.addAfter(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON);
            content.addAfter(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
            content.addAfter(Blocks.TUFF_BRICK_STAIRS, ModBlocks.TUFF_BRICK_PILASTER);
            content.addAfter(Blocks.TUFF_STAIRS, ModBlocks.TUFF_PILASTER);
            content.addAfter(Blocks.POLISHED_TUFF_STAIRS, ModBlocks.POLISHED_TUFF_PILASTER);

        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(content -> {
            content.addAfter(Items.DEBUG_STICK, ModItems.CREATIVE_WRENCH);
        });
    }
}

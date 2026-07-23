package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.UnlockScrollManager;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

public class ModInventoryItemRegistry {

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.OMINOUS_TRIAL_KEY, ModItems.RUBY_TRIAL_KEY, ModItems.STAFF, ModItems.DRAGON_ROD);
            content.addAfter(Items.PAPER, ModItems.ENCHANTED_PAPER, ModItems.EMPTY_SCROLL, ModItems.UNLOCK_SCROLL);
            content.addAfter(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ModItems.UNBREAKABLE_UPGRADE_TEMPLATE);
            content.addAfter(Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, ModItems.ELDRITCH_ARMOR_TRIM, ModItems.SUN_ARMOR_TRIM);
            content.addAll(UnlockScrollManager.UNLOCK_SCROLLS);
            content.addAfter(Items.SUGAR, ModItems.SALT);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(content -> {
            content.addAfter(Items.TUFF, ModBlocks.SALT_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(content -> {
            content.addAfter(Items.LIGHT, ModItems.SPELLSTONE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.addAfter(Items.DRIED_KELP, ModItems.RAW_MEAT_CLUB, ModItems.COOKED_MEAT_CLUB);
            content.addAfter(Items.ROTTEN_FLESH, ModItems.TRIAL_JERKY);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.MUSIC_DISC_PIGSTEP, ModItems.CRYSTAL_FOREST_MUSIC_DISC);
            content.addAfter(Items.WARPED_FUNGUS_ON_A_STICK, ModItems.SELECTOR);
            content.addAfter(Items.LEAD, ModItems.BLOCK_KEY, ModItems.AMETHYST_LENS);

            content.addAfter(ModItems.BLOCK_KEY, ModItems.COPPER_WRENCH);

            content.addAfter(Items.SPYGLASS, ModBlocks.SPY_GLASS_BLOCK);
            content.addAfter(Items.MILK_BUCKET, ModItems.TOXIC_SLUDGE_BUCKET);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Blocks.BEACON, ModBlocks.OMINOUS_BEACON);
            content.addAfter(Blocks.ENCHANTING_TABLE, ModBlocks.DUNGEON_WIRE,
                    ModBlocks.PEDESTAL, ModBlocks.ITEM_PEDESTAL,
                    ModBlocks.KEY_ACCEPTOR, ModBlocks.POTION_PILLAR,
                    ModBlocks.DUNGEON_DOOR, ModBlocks.DUNGEON_FLAME,
                    ModBlocks.DEEP_FLAME, ModBlocks.FORTRESS_FLAME,
                    ModBlocks.BRAZIER, ModBlocks.SPY_GLASS_BLOCK);
            content.addAfter(Blocks.VAULT, ModBlocks.MOB_DETECTOR, ModBlocks.MELTING_ICE);
            content.addAfter(Blocks.DECORATED_POT, ModBlocks.FANCY_RED_POT, ModBlocks.FANCY_GREEN_POT, ModBlocks.FANCY_BLUE_POT);

            content.addAfter(Blocks.JUKEBOX, ModBlocks.TRIAL_CORE, ModBlocks.BONFIRE, ModBlocks.BELLOW, ModBlocks.GUNPOWDER_BARREL, ModBlocks.OBSIDIAN_CHEST);
            content.addAfter(Blocks.MAGMA_BLOCK, ModBlocks.SALT_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(content -> {
            content.addAfter(Items.MACE, ModItems.STAFF, ModItems.WIND_STAFF, ModItems.FIRE_STAFF, ModItems.EARTH_STAFF, ModItems.WATER_STAFF, ModItems.DRAGON_STAFF, ModItems.SPELL_BOOK);
            content.addAfter(Items.WIND_CHARGE, ModItems.WATER_BLAST, ModItems.SEA_MINE);
            content.addAfter(Items.CROSSBOW, ModItems.SCOPED_CROSSBOW);
            content.addAfter(Items.TOTEM_OF_UNDYING, ModItems.PET_CHARM);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
            content.addAfter(Items.REDSTONE_ORE, ModBlocks.ITEM_PEDESTAL,
                    ModBlocks.DUNGEON_DOOR, ModBlocks.DUNGEON_WIRE,
                    ModBlocks.POWER_PILASTER, ModBlocks.POWER_PILLAR,
                    ModBlocks.DUNGEON_BUTTON, ModBlocks.DUNGEON_PRESSURE_PLATE,
                    ModBlocks.WAXED_COPPER_TRIAL_FAN, ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN,
                    ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN, ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN);
            content.addAfter(Blocks.STONE_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON,
                    ModBlocks.WAXED_EXPOSED_COPPER_BUTTON,
                    ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
            content.addAfter(Blocks.DECORATED_POT, ModBlocks.FANCY_RED_POT, ModBlocks.FANCY_GREEN_POT, ModBlocks.FANCY_BLUE_POT);

            content.addAfter(Blocks.TARGET, ModBlocks.KILL_BALL_RECEPTOR, ModBlocks.POLISHED_TUFF_PRESSURE_PLATE);
            content.addAfter(Blocks.OBSERVER, ModBlocks.REDSTONE_BRIDGE);
            content.addAfter(ModBlocks.DUNGEON_PRESSURE_PLATE, ModBlocks.COPPER_TRIAL_FAN, ModBlocks.EXPOSED_COPPER_TRIAL_FAN, ModBlocks.WEATHERED_COPPER_TRIAL_FAN, ModBlocks.OXIDIZED_COPPER_TRIAL_FAN);

            content.addAfter(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON,
                    ModBlocks.WAXED_CHARGED_COPPER,
                    ModBlocks.WAXED_EXPOSED_CHARGED_COPPER,
                    ModBlocks.WAXED_WEATHERED_CHARGED_COPPER,
                    ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER,
                    ModBlocks.CHARGED_REINFORCED_DUNGEON_BLOCK);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.addAfter(Items.EVOKER_SPAWN_EGG, ModItems.ELDRITCH_LUMINARY_SPAWN_EGG);
            content.addAfter(Items.IRON_GOLEM_SPAWN_EGG, ModItems.COPPER_GOLEM_SPAWN_EGG);
            content.addAfter(ModItems.COPPER_GOLEM_SPAWN_EGG, ModItems.TUFF_GOLEM_SPAWN_EGG);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
            content.addAfter(Blocks.WAXED_OXIDIZED_COPPER_BULB, ModBlocks.UNOXIDIZED_COPPER_BUTTON,
                    ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON,
                    ModBlocks.WAXED_EXPOSED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON,
                    ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON,
                    ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON,

                    ModBlocks.CHARGED_COPPER, ModBlocks.WAXED_CHARGED_COPPER,
                    ModBlocks.EXPOSED_CHARGED_COPPER, ModBlocks.WAXED_EXPOSED_CHARGED_COPPER,
                    ModBlocks.WEATHERED_CHARGED_COPPER, ModBlocks.WAXED_WEATHERED_CHARGED_COPPER,
                    ModBlocks.OXIDIZED_CHARGED_COPPER, ModBlocks.WAXED_OXIDIZED_CHARGED_COPPER,
                    ModBlocks.CHARGED_REINFORCED_DUNGEON_BLOCK);

            content.addAfter(Blocks.STONE, ModBlocks.STONE_PILLAR, ModBlocks.STONE_PILASTER);
            content.addAfter(Blocks.STONE_BRICKS, ModBlocks.STONE_BRICKS_PILLAR, ModBlocks.STONE_BRICKS_PILASTER);
            content.addAfter(Blocks.CRACKED_STONE_BRICKS, ModBlocks.CRACKED_STONE_BRICKS_PILLAR, ModBlocks.CRACKED_STONE_BRICKS_PILASTER);
            content.addAfter(Blocks.TUFF_BRICKS, ModBlocks.UNSTABLE_TUFF_BRICKS, ModBlocks.TUFF_PILLAR, ModBlocks.TUFF_PILASTER);
            content.addAfter(Blocks.CHISELED_TUFF_BRICKS, ModBlocks.CHISELED_TUFF_BRICKS_PILLAR, ModBlocks.CHISELED_TUFF_BRICKS_PILASTER);
            content.addAfter(Blocks.POLISHED_TUFF, ModBlocks.POLISHED_TUFF_PILLAR, ModBlocks.POLISHED_TUFF_PILASTER);
            content.addAfter(Blocks.TUFF_BRICKS, ModBlocks.TUFF_BRICKS_PILLAR, ModBlocks.TUFF_BRICK_PILASTER);

            content.addAfter(Blocks.AMETHYST_BLOCK, ModBlocks.REINFORCED_DUNGEON_BLOCK,
                    ModBlocks.REINFORCED_DUNGEON_GRATE, ModBlocks.REINFORCED_DUNGEON_TILES,
                    ModBlocks.REINFORCED_DUNGEON_TILE_STAIRS, ModBlocks.REINFORCED_DUNGEON_TILE_SLAB);


        });
    }
}
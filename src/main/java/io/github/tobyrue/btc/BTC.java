package io.github.tobyrue.btc;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import io.github.tobyrue.btc.enums.WrenchType;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.regestries.*;
import io.github.tobyrue.btc.status_effects.MinerMishapEffect;
import io.github.tobyrue.btc.status_effects.BuilderBlunderEffect;
import io.github.tobyrue.btc.status_effects.DragonScalesEffect;
import io.github.tobyrue.btc.status_effects.DrowningEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.GustParticle;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.structure.Structure;

import java.util.Arrays;

public class BTC implements ModInitializer {

    public static String MOD_ID = "btc";
    public static final StatusEffect BUILDER_BLUNDER;
    public static final StatusEffect MINER_MISHAP;
    public static final StatusEffect DRAGON_SCALES;
    public static final StatusEffect DROWNING;
    public static final TagKey<Block> WRENCH_BLACKLIST = TagKey.of(RegistryKeys.BLOCK,  Identifier.of(MOD_ID, "wrench_blacklist"));
    public static final TagKey<Item> WRENCHES = TagKey.of(RegistryKeys.ITEM,  Identifier.of(MOD_ID, "wrenches"));
    public static final ComponentType<Direction> WRENCH_DIRECTION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            identifierOf("wrench_direction"),
            ComponentType.<Direction>builder()
                    .codec(Direction.CODEC)
                    .build()
    );
    public static final ComponentType<WrenchType> WRENCH_TYPE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            identifierOf("wrench_type"),
            ComponentType.<WrenchType>builder()
                    .codec(WrenchType.CODEC)
                    .build()
    );
    //To add another map for a structure make a new tag like below and also add a new json file with the path in the tag below under the path: data/btc/tags/worldgen/structure. Look at better_trial_chambers_maps for the format change the structure in it to the name of the structure.
    public static final TagKey<Structure> BETTER_TRIAL_CHAMBERS_TAG = TagKey.of(RegistryKeys.STRUCTURE, Identifier.of(MOD_ID, "better_trial_chambers_maps"));

    static {
        BUILDER_BLUNDER = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "builder_blunder"), new BuilderBlunderEffect());
        MINER_MISHAP = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "miner_mishap"), new MinerMishapEffect());
        DRAGON_SCALES = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "dragon_scales"), new DragonScalesEffect());
        DROWNING = Registry.register(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "drowning"), new DrowningEffect());
    }
    public static final SimpleParticleType WATER_BLAST = FabricParticleTypes.simple();

    // Register our custom particle type in the mod initializer.
    @Override
    public void onInitialize() {

        //Adds a trade in another class, BetterTrialChambersMapTrade
        TradeOfferHelper.registerWanderingTraderOffers( 1, factories -> {
            factories.add(new BetterTrialChambersMapTrade());
        });

        ModMapDecorationTypes.register();
        ModBlocks.initialize();
        ModItems.initialize();
        ModBlockEntities.initialize();
        ModPotions.initialize();
        ModSounds.initialize();
        ModCopperBlocks.registerCopperBlocks();

        FabricDefaultAttributeRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryEntity.createEldritchLuminaryAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemEntity.createCopperGolemAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemEntity.createTuffGolemAttributes());

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (player.hasStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.BUILDER_BLUNDER)) && !player.isCreative()) {
                ItemStack stack = player.getStackInHand(hand);
                if (stack.getItem() instanceof BlockItem) {
                    return ActionResult.FAIL; // Block placement is prevented
                }
            }
            return ActionResult.PASS; // Other interactions (like opening chests, using tools) are allowed
        });



//        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.UNOXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON);
//        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.EXPOSED_COPPER_BUTTON, ModBlocks.WAXED_EXPOSED_COPPER_BUTTON);
//        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.WEATHERED_COPPER_BUTTON, ModBlocks.WAXED_WEATHERED_COPPER_BUTTON);
//        HoneycombItem.UNWAXED_TO_WAXED_BLOCKS.get().put(ModBlocks.OXIDIZED_COPPER_BUTTON, ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON);
//
//        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON, ModBlocks.UNOXIDIZED_COPPER_BUTTON);
//        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON, ModBlocks.EXPOSED_COPPER_BUTTON);
//        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON, ModBlocks.WEATHERED_COPPER_BUTTON);
//        HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON, ModBlocks.OXIDIZED_COPPER_BUTTON);

        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "water_blast"), WATER_BLAST);
        ParticleFactoryRegistry.getInstance().register(BTC.WATER_BLAST, GustParticle.Factory::new);


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(content -> {
            content.addAfter(Items.OMINOUS_TRIAL_KEY, ModItems.RUBY_TRIAL_KEY);
            content.addAfter(ModItems.RUBY_TRIAL_KEY, ModItems.STAFF);
            content.addAfter(ModItems.STAFF, ModItems.DRAGON_ROD);
            content.addAfter(Items.PAPER, ModItems.ENCHANTED_PAPER);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(content -> {
            content.addAfter(Items.NETHERITE_HOE, ModItems.COPPER_WRENCH);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(content -> {
            content.addAfter(Blocks.BEACON, ModBlocks.OMINOUS_BEACON);
            content.addAfter(Blocks.ENCHANTING_TABLE, ModBlocks.PEDESTAL);
            content.addAfter(ModBlocks.PEDESTAL, ModBlocks.KEY_DISPENSER_BLOCK);
            content.addAfter(ModBlocks.KEY_DISPENSER_BLOCK, ModBlocks.ANTIER);
            content.addAfter(ModBlocks.ANTIER, ModBlocks.DUNGEON_DOOR);
            content.addAfter(ModBlocks.DUNGEON_DOOR, ModBlocks.FIRE_DISPENSER);
            content.addAfter(ModBlocks.FIRE_DISPENSER, ModBlocks.DUNGEON_WIRE_LEGACY);
            content.addAfter(ModBlocks.DUNGEON_WIRE_LEGACY, ModBlocks.COPPER_WIRE_LEGACY);
            content.addAfter(ModBlocks.COPPER_WIRE_LEGACY, ModItems.IRON_WRENCH);
            content.addAfter(ModItems.IRON_WRENCH, ModItems.GOLD_WRENCH);
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
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register(content -> {
            content.addAfter(Items.DEBUG_STICK, ModItems.CREATIVE_WRENCH);
        });
        //INGREDIENTS
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.RUBY_TRIAL_KEY));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModItems.DRAGON_ROD));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));
//
//        //FUNCTIONAL
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.OMINOUS_BEACON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.PEDESTAL));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.ANTIER));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_FIRE));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModItems.IRON_WRENCH));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((itemGroup) -> itemGroup.add(ModItems.GOLD_WRENCH));
//
//        //COMBAT
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.STAFF));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WIND_STAFF));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.FIRE_STAFF));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.DRAGON_STAFF));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.SPELL_BOOK));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register((itemGroup) -> itemGroup.add(ModItems.WATER_BLAST));
//
//        //REDSTONE
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_WIRE));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.COPPER_WIRE));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.FIRE_DISPENSER));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_DOOR));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.DUNGEON_PRESSURE_PLATE));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.KEY_DISPENSER_BLOCK));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModItems.IRON_WRENCH));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModItems.GOLD_WRENCH));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.UNOXIDIZED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_UNOXIDIZED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.EXPOSED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WEATHERED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.OXIDIZED_COPPER_BUTTON));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register((itemGroup) -> itemGroup.add(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON));
//
//        //SPAWN EGGS
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.ELDRITCH_LUMINARY_SPAWN_EGG));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.COPPER_GOLEM_SPAWN_EGG));
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register((itemGroup) -> itemGroup.add(ModItems.TUFF_GOLEM_SPAWN_EGG));
//
//        //BUILDING BLOCKS
//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((itemGroup) -> itemGroup.add(ModBlocks.CHISELED_COPPER_BRICKS));
    }
    public static void println(Object... args) {
        System.out.println(String.join(" ", Arrays.stream(args).map(Object::toString).toArray(String[]::new)));
    }
    public static Identifier identifierOf(String id) {
        return Identifier.of(BTC.MOD_ID, id);
    }
}

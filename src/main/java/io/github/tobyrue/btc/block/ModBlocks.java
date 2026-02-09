package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.wires.WireBlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        // Register the block and its item.
        Identifier id = Identifier.of(BTC.MOD_ID, name);

        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }
    public static final PillarBlock CHISELED_TUFF_BRICKS_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF_BRICKS)),
            "chiseled_tuff_bricks_pillar",
            true
    );
    public static final PillarBlock TUFF_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)),
            "tuff_pillar",
            true
    );
    public static final PillarBlock STONE_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STONE)),
            "stone_pillar",
            true
    );
    public static final PillarBlock CRACKED_STONE_BRICKS_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS)),
            "cracked_stone_bricks_pillar",
            true
    );
    public static final PillarBlock STONE_BRICKS_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS)),
            "stone_bricks_pillar",
            true
    );
    public static final PillarBlock POLISHED_TUFF_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)),
            "polished_tuff_pillar",
            true
    );
    public static final PillarBlock TUFF_BRICKS_PILLAR = (PillarBlock) register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)),
            "tuff_bricks_pillar",
            true
    );
    public static final PilasterBlock STONE_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.STONE)),
            "stone_pilaster",
            true
    );
    public static final PilasterBlock STONE_BRICKS_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS)),
            "stone_bricks_pilaster",
            true
    );
    public static final PilasterBlock CRACKED_STONE_BRICKS_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS)),
            "cracked_stone_bricks_pilaster",
            true
    );
    public static final PilasterBlock TUFF_BRICK_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)),
            "tuff_brick_pilaster",
            true
    );
    public static final PilasterBlock CHISELED_TUFF_BRICKS_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF_BRICKS)),
            "chiseled_tuff_bricks_pilaster",
            true
    );
    public static final PilasterBlock POLISHED_TUFF_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)),
            "polished_tuff_pilaster",
            true
    );
    public static final PilasterBlock TUFF_PILASTER = (PilasterBlock) register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.TUFF)),
            "tuff_pilaster",
            true
    );

    public static final Block MELTING_ICE = register(
            new MeltingIceBlock(AbstractBlock.Settings.create().mapColor(MapColor.WATER_BLUE).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS).instrument(NoteBlockInstrument.SNARE).nonOpaque()),
            "melting_ice",
            true
    );
    public static final Block CHISELED_COPPER_BRICKS = register(
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).mapColor(MapColor.TERRACOTTA_ORANGE).requiresTool().strength(1.5F, 6.0F).instrument(NoteBlockInstrument.SNARE)),
            "chiseled_copper_bricks",
            true
    );
    public static final Block DUNGEON_WIRE = register(
            new WireBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.TUFF_BRICKS), true),
            "dungeon_wire",
            true
    );
    public static final BrazierBlock BRAZIER = (BrazierBlock) register(
            new BrazierBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).requiresTool().sounds(BlockSoundGroup.POLISHED_DEEPSLATE).luminance((state) -> {
                return 15;
            }).strength(3.5F, 3600000.0F)),
            "brazier",
            true

            );
    public static final OminousBeaconBlock OMINOUS_BEACON = (OminousBeaconBlock) register(
            new OminousBeaconBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).requiresTool().luminance((state) -> {
                return 14;
            }).strength(3.5F, 3600000.0F)),
            "ominous_beacon",
            true
    );
    public static final MobDetectorBlock MOB_DETECTOR = (MobDetectorBlock) register(
            new MobDetectorBlock(AbstractBlock.Settings.create().nonOpaque()),
            "mob_detector",
            true

    );
    public static final DungeonPressurePlateBlock DUNGEON_PRESSURE_PLATE = (DungeonPressurePlateBlock) register(
            new DungeonPressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.create().requiresTool().strength(2.0F, 3600000.0F).sounds(BlockSoundGroup.COPPER)),
            "dungeon_pressure_plate",
            true
    );
    public static final PressurePlateBlock POLISHED_TUFF_PRESSURE_PLATE = (PressurePlateBlock) register(
            new PressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)),
            "polished_tuff_pressure_plate",
            true
    );
    public static final CopperButtonBlock UNOXIDIZED_COPPER_BUTTON = (CopperButtonBlock) register(
            new CopperButtonBlock(BlockSetType.STONE, 10, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.UNAFFECTED),
            "copper_button_unoxidized",
            true
    );
    public static final ButtonBlock WAXED_UNOXIDIZED_COPPER_BUTTON = (ButtonBlock) register(
            new ButtonBlock(BlockSetType.STONE, 10, AbstractBlock.Settings.copy(UNOXIDIZED_COPPER_BUTTON)),
            "waxed_copper_button_unoxidized",
            true
    );
    public static final CopperButtonBlock EXPOSED_COPPER_BUTTON = (CopperButtonBlock) register(
            new CopperButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.EXPOSED),
            "copper_button_exposed",
            true
    );
    public static final ButtonBlock WAXED_EXPOSED_COPPER_BUTTON = (ButtonBlock) register(
            new ButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.copy(EXPOSED_COPPER_BUTTON)),
            "waxed_copper_button_exposed",
            true
    );
    public static final CopperButtonBlock WEATHERED_COPPER_BUTTON = (CopperButtonBlock) register(
            new CopperButtonBlock(BlockSetType.STONE, 30, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.WEATHERED),
            "copper_button_weathered",
            true
    );
    public static final ButtonBlock WAXED_WEATHERED_COPPER_BUTTON = (ButtonBlock) register(
            new ButtonBlock(BlockSetType.STONE, 30, AbstractBlock.Settings.copy(WEATHERED_COPPER_BUTTON)),
            "waxed_copper_button_weathered",
            true
    );
    public static final CopperButtonBlock OXIDIZED_COPPER_BUTTON = (CopperButtonBlock) register(
            new CopperButtonBlock(BlockSetType.STONE, 40, AbstractBlock.Settings.create().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.OXIDIZED),
            "copper_button_oxidized",
            true
    );

    public static final ButtonBlock WAXED_OXIDIZED_COPPER_BUTTON = (ButtonBlock) register(
            new ButtonBlock(BlockSetType.STONE, 40, AbstractBlock.Settings.copy(OXIDIZED_COPPER_BUTTON)),
            "waxed_copper_button_oxidized",
            true
    );

    public static final PotionPillar POTION_PILLAR = (PotionPillar) register(
            new PotionPillar(AbstractBlock.Settings.create().nonOpaque().strength(-1.0F, 3600000.0F)),
            "potion_pillar",
            true
    );

    public static final PedestalBlock PEDESTAL = (PedestalBlock) register(
            new PedestalBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 12;
            }).strength(-1.0F, 3600000.0F)),
            "pedestal",
            true
    );

    public static final KeyDispenserBlock KEY_DISPENSER_BLOCK = (KeyDispenserBlock) register(
            new KeyDispenserBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 12;
            }).strength(-1.0F, 3600000.0F)),
            "key_dispenser",
            true
    );
    public static final KeyAcceptorBlock KEY_ACCEPTOR = (KeyAcceptorBlock) register(
            new KeyAcceptorBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 12;
            }).strength(-1.0F, 3600000.0F)),
            "key_acceptor",
            true
    );
//    public static final DungeonWireBlock DUNGEON_WIRE_LEGACY = (DungeonWireBlock) register(
//            new DungeonWireBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F).luminance(
//                    DungeonWireBlock::getLuminance)
//            ),
//
//            "dungeon_wire",
//            true
//    );
//    public static final CopperWireBlock COPPER_WIRE_LEGACY = (CopperWireBlock) register(
//            new CopperWireBlock(AbstractBlock.Settings.create().requiresTool().strength(8F, 3600000.0F).luminance(
//                    CopperWireBlock::getLuminance)
//            ),
//            "copper_wire",
//            true
//    );
    //TODO :/ dungeon flambeaux
    public static final DungeonFlameBlock DUNGEON_FLAME = (DungeonFlameBlock) register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().requiresTool().strength(4.5F, 3600000.0F).luminance(
                    DungeonFlameBlock::getLuminance).emissiveLighting((state,blockView,pos) -> DungeonFlameBlock.getLuminance(state) > 0), null, ParticleTypes.FLAME
    ),
            "dungeon_flame",
            true
    );
    //TODO :/ deep flambeaux
    public static final DungeonFlameBlock DEEP_FLAME = (DungeonFlameBlock) register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().requiresTool().strength(4.5F, 3600000.0F).luminance(
                    DungeonFlameBlock::getLuminance).emissiveLighting((state,blockView,pos) -> DungeonFlameBlock.getLuminance(state) > 0), null, ParticleTypes.SOUL_FIRE_FLAME
            ),
            "deep_flame",
            true
    );
    //TODO :/ fortress flambeaux
    public static final DungeonFlameBlock FORTRESS_FLAME = (DungeonFlameBlock) register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().requiresTool().strength(4.5F, 3600000.0F).luminance(state -> 15).emissiveLighting((state,blockView,pos) -> true), ParticleTypes.FLAME, ParticleTypes.SOUL_FIRE_FLAME
            ),
            "fortress_flame",
            true
    );
    public static final DungeonDoorBlock DUNGEON_DOOR = (DungeonDoorBlock) register(
            new DungeonDoorBlock(AbstractBlock.Settings.create().requiresTool().strength(6.5F, .0F)),

            "dungeon_door",
            true
    );



    public static void initialize() {
        Registries.BLOCK.addAlias(BTC.identifierOf("dungeon_wire_v2"), BTC.identifierOf("dungeon_wire"));
        Registries.ITEM.addAlias(BTC.identifierOf("dungeon_wire_v2"), BTC.identifierOf("dungeon_wire"));
        Registries.BLOCK.addAlias(BTC.identifierOf("presence_node"), BTC.identifierOf("mob_detector"));
        Registries.ITEM.addAlias(BTC.identifierOf("presence_node"), BTC.identifierOf("mob_detector"));
        Registries.BLOCK.addAlias(BTC.identifierOf("antier"), BTC.identifierOf("potion_pillar"));
        Registries.ITEM.addAlias(BTC.identifierOf("antier"), BTC.identifierOf("potion_pillar"));
        Registries.BLOCK.addAlias(BTC.identifierOf("tuff_column"), BTC.identifierOf("tuff_pillar"));
        Registries.ITEM.addAlias(BTC.identifierOf("tuff_column"), BTC.identifierOf("tuff_pillar"));
        Registries.BLOCK.addAlias(BTC.identifierOf("stone_column"), BTC.identifierOf("stone_pillar"));
        Registries.ITEM.addAlias(BTC.identifierOf("stone_column"), BTC.identifierOf("stone_pillar"));
        Registries.BLOCK.addAlias(BTC.identifierOf("cracked_stone_column"), BTC.identifierOf("cracked_stone_pillar"));
        Registries.ITEM.addAlias(BTC.identifierOf("cracked_stone_column"), BTC.identifierOf("cracked_stone_pillar"));
        Registries.BLOCK.addAlias(BTC.identifierOf("dungeon_fire"), BTC.identifierOf("brazier"));
        Registries.ITEM.addAlias(BTC.identifierOf("dungeon_fire"), BTC.identifierOf("brazier"));
        Registries.BLOCK.addAlias(BTC.identifierOf("fire_dispenser"), BTC.identifierOf("dungeon_flame"));
        Registries.ITEM.addAlias(BTC.identifierOf("fire_dispenser"), BTC.identifierOf("dungeon_flame"));
        Registries.BLOCK.addAlias(BTC.identifierOf("cracked_stone_pillar"), BTC.identifierOf("cracked_stone_bricks_pillar"));
        Registries.ITEM.addAlias(BTC.identifierOf("cracked_stone_pillar"), BTC.identifierOf("cracked_stone_bricks_pillar"));
    }
}

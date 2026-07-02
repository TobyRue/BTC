package io.github.tobyrue.btc.block;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.wires.WireBlock;
import io.github.tobyrue.btc.wires.circuit.FPGABlock;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(BTC.MOD_ID, name);
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static final Block REINFORCED_DUNGEON_TILES = register(
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.METAL).instrument(NoteBlockInstrument.BASEDRUM)),
            "reinforced_dungeon_tiles",
            true
    );

    public static final Block REINFORCED_DUNGEON_BLOCK = register(
            new Block(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.METAL).instrument(NoteBlockInstrument.BASEDRUM)),
            "reinforced_dungeon_block",
            true
    );

    public static final Block KILL_BALL_RECEPTOR = register(
            new KillBallReceptorBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.SNARE)),
            "kill_ball_receptor",
            true
    );

    public static final Block MOB_DETECTOR = register(
            new MobDetectorBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.STONE).nonOpaque()),
            "mob_detector",
            true
    );

    public static final Block DUNGEON_DOOR = register(
            new DungeonDoorBlock(AbstractBlock.Settings.create().mapColor(MapColor.IRON_GRAY).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.NETHERITE)),
            "dungeon_door",
            true
    );

    public static final Block OMINOUS_BEACON = register(
            new OminousBeaconBlock(AbstractBlock.Settings.create().mapColor(MapColor.LAPIS_BLUE).nonOpaque().requiresTool().strength(6.5F, 6.0F).sounds(BlockSoundGroup.VAULT).luminance((state) -> 14)),
            "ominous_beacon",
            true
    );

    public static final Block GUNPOWDER_BARREL = register(
            new GunpowderBarrelBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).strength(2.0F, 3.0F).sounds(BlockSoundGroup.WOOD).instrument(NoteBlockInstrument.BASS)),
            "gunpowder_barrel",
            true
    );

    public static final Block BELLOW = register(
            new BellowBlock(AbstractBlock.Settings.create().mapColor(MapColor.SPRUCE_BROWN).requiresTool().strength(2.5F, 3.0F).sounds(BlockSoundGroup.WOOD).instrument(NoteBlockInstrument.BASS)),
            "bellow",
            true
    );

    public static final Block GUNPOWDER_DUST = register(
            new GunpowderDustBlock(AbstractBlock.Settings.create().mapColor(MapColor.CLEAR).breakInstantly().noCollision().pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.SAND)),
            "gunpowder_dust",
            false
    );

    public static final Block COPPER_TRIAL_FAN = register(
            new CopperFanBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).requiresTool().strength(3.5F, 6.0F).sounds(BlockSoundGroup.COPPER).instrument(NoteBlockInstrument.IRON_XYLOPHONE), Oxidizable.OxidationLevel.UNAFFECTED),
            "copper_fan",
            true
    );

    public static final Block EXPOSED_COPPER_TRIAL_FAN = register(
            new ExposedFanBlock(AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).requiresTool().strength(3.5F, 6.0F).sounds(BlockSoundGroup.COPPER).instrument(NoteBlockInstrument.IRON_XYLOPHONE), Oxidizable.OxidationLevel.EXPOSED),
            "exposed_copper_fan",
            true
    );

    public static final Block WEATHERED_COPPER_TRIAL_FAN = register(
            new WeatheredFanBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).requiresTool().strength(3.5F, 6.0F).sounds(BlockSoundGroup.COPPER).instrument(NoteBlockInstrument.IRON_XYLOPHONE), Oxidizable.OxidationLevel.WEATHERED),
            "weathered_copper_fan",
            true
    );

    public static final Block OXIDIZED_COPPER_TRIAL_FAN = register(
            new OxidizedFanBlock(AbstractBlock.Settings.create().mapColor(MapColor.TEAL).requiresTool().strength(3.5F, 6.0F).sounds(BlockSoundGroup.COPPER).instrument(NoteBlockInstrument.IRON_XYLOPHONE), Oxidizable.OxidationLevel.OXIDIZED),
            "oxidized_copper_fan",
            true
    );

    public static final Block WAXED_COPPER_TRIAL_FAN = register(
            new WaxedCopperFanBlock(AbstractBlock.Settings.copy(ModBlocks.COPPER_TRIAL_FAN)),
            "waxed_copper_fan",
            true
    );

    public static final Block WAXED_EXPOSED_COPPER_TRIAL_FAN = register(
            new WaxedExposedCopperFanBlock(AbstractBlock.Settings.copy(ModBlocks.EXPOSED_COPPER_TRIAL_FAN)),
            "waxed_exposed_copper_fan",
            true
    );

    public static final Block WAXED_WEATHERED_COPPER_TRIAL_FAN = register(
            new WaxedWeatheredCopperFanBlock(AbstractBlock.Settings.copy(ModBlocks.WEATHERED_COPPER_TRIAL_FAN)),
            "waxed_weathered_copper_fan",
            true
    );

    public static final Block WAXED_OXIDIZED_COPPER_TRIAL_FAN = register(
            new WaxedOxidizedCopperFanBlock(AbstractBlock.Settings.copy(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN)),
            "waxed_oxidized_copper_fan",
            true
    );

    public static final Block TRIAL_CORE = register(
            new TrialCoreBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.CHIME)),
            "trial_core",
            true
    );

    public static final Block BONFIRE = register(
            new BonfireBlock(AbstractBlock.Settings.create().mapColor(MapColor.OAK_TAN).strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.WOOD).instrument(NoteBlockInstrument.BASS)),
            "bonfire",
            true
    );

    public static final Block POWER_PILASTER = register(
            new PowerPilasterBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_RED).strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)),
            "power_pilaster",
            true
    );

    public static final Block POWER_PILLAR = register(
            new PowerPillarBlock(AbstractBlock.Settings.create().mapColor(MapColor.DARK_RED).strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)),
            "power_pillar",
            true
    );

    public static final Block DUNGEON_WIRE = register(
            new WireBlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)),
            "dungeon_wire",
            true
    );

    public static final Block FPGA_BLOCK = register(
            new FPGABlock(AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)),
            "fpga_wire",
            true
    );

    public static final Block OBSIDIAN_CHEST = register(
            new ObsidianChestBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLACK).strength(-1.0F, 3600000.0F).sounds(BlockSoundGroup.STONE).instrument(NoteBlockInstrument.BASEDRUM)),
            "obsidian_chest",
            true
    );

    public static final Block CHISELED_TUFF_BRICKS_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF_BRICKS)),
            "chiseled_tuff_bricks_pillar",
            true
    );

    public static final Block TUFF_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.TUFF)),
            "tuff_pillar",
            true
    );

    public static final Block STONE_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STONE)),
            "stone_pillar",
            true
    );

    public static final Block CRACKED_STONE_BRICKS_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS)),
            "cracked_stone_bricks_pillar",
            true
    );

    public static final Block STONE_BRICKS_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS)),
            "stone_bricks_pillar",
            true
    );

    public static final Block POLISHED_TUFF_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)),
            "polished_tuff_pillar",
            true
    );

    public static final Block TUFF_BRICKS_PILLAR = register(
            new PillarBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)),
            "tuff_bricks_pillar",
            true
    );

    public static final Block STONE_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.STONE)),
            "stone_pilaster",
            true
    );

    public static final Block STONE_BRICKS_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.STONE_BRICKS)),
            "stone_bricks_pilaster",
            true
    );

    public static final Block CRACKED_STONE_BRICKS_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.CRACKED_STONE_BRICKS)),
            "cracked_stone_bricks_pilaster",
            true
    );

    public static final Block TUFF_BRICK_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.TUFF_BRICKS)),
            "tuff_brick_pilaster",
            true
    );

    public static final Block CHISELED_TUFF_BRICKS_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.CHISELED_TUFF_BRICKS)),
            "chiseled_tuff_bricks_pilaster",
            true
    );

    public static final Block POLISHED_TUFF_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.POLISHED_TUFF)),
            "polished_tuff_pilaster",
            true
    );

    public static final Block TUFF_PILASTER = register(
            new PilasterBlock(AbstractBlock.Settings.copy(Blocks.TUFF)),
            "tuff_pilaster",
            true
    );

    public static final Block SPY_GLASS_BLOCK = register(
            new SpyGlassBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_FENCE).nonOpaque()),
            "spy_glass_block",
            true
    );

    public static final Block MELTING_ICE = register(
            new MeltingIceBlock(AbstractBlock.Settings.create().mapColor(MapColor.WATER_BLUE).slipperiness(0.98F).strength(0.5F).sounds(BlockSoundGroup.GLASS).instrument(NoteBlockInstrument.SNARE).nonOpaque()),
            "melting_ice",
            true
    );

    public static final Block BRAZIER = register(
            new BrazierBlock(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).nonOpaque().requiresTool().strength(3.5F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE).luminance((state) -> 15)),
            "brazier",
            true
    );

    public static final Block DUNGEON_PRESSURE_PLATE = register(
            new DungeonPressurePlateBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.STONE)),
            "dungeon_pressure_plate",
            true
    );

    public static final Block POLISHED_TUFF_PRESSURE_PLATE = register(
            new PressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).requiresTool().strength(2.0F, 6.0F).sounds(BlockSoundGroup.POLISHED_TUFF)),
            "polished_tuff_pressure_plate",
            true
    );

    public static final Block DUNGEON_BUTTON = register(
            new DungeonButtonBlock(BlockSetType.STONE, 40, AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.TUFF_BRICKS)),
            "dungeon_button",
            true
    );

    public static final Block UNOXIDIZED_COPPER_BUTTON = register(
            new CopperButtonBlock(BlockSetType.STONE, 10, AbstractBlock.Settings.create().mapColor(MapColor.ORANGE).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.UNAFFECTED),
            "copper_button_unoxidized",
            true
    );

    public static final Block WAXED_UNOXIDIZED_COPPER_BUTTON = register(
            new ButtonBlock(BlockSetType.STONE, 10, AbstractBlock.Settings.copy(UNOXIDIZED_COPPER_BUTTON)),
            "waxed_copper_button_unoxidized",
            true
    );

    public static final Block EXPOSED_COPPER_BUTTON = register(
            new CopperButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.create().mapColor(MapColor.TERRACOTTA_LIGHT_GRAY).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.EXPOSED),
            "copper_button_exposed",
            true
    );

    public static final Block WAXED_EXPOSED_COPPER_BUTTON = register(
            new ButtonBlock(BlockSetType.STONE, 20, AbstractBlock.Settings.copy(EXPOSED_COPPER_BUTTON)),
            "waxed_copper_button_exposed",
            true
    );

    public static final Block WEATHERED_COPPER_BUTTON = register(
            new CopperButtonBlock(BlockSetType.STONE, 30, AbstractBlock.Settings.create().mapColor(MapColor.DARK_AQUA).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.WEATHERED),
            "copper_button_weathered",
            true
    );

    public static final Block WAXED_WEATHERED_COPPER_BUTTON = register(
            new ButtonBlock(BlockSetType.STONE, 30, AbstractBlock.Settings.copy(WEATHERED_COPPER_BUTTON)),
            "waxed_copper_button_weathered",
            true
    );

    public static final Block OXIDIZED_COPPER_BUTTON = register(
            new CopperButtonBlock(BlockSetType.STONE, 40, AbstractBlock.Settings.create().mapColor(MapColor.TEAL).noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.COPPER), Oxidizable.OxidationLevel.OXIDIZED),
            "copper_button_oxidized",
            true
    );

    public static final Block WAXED_OXIDIZED_COPPER_BUTTON = register(
            new ButtonBlock(BlockSetType.STONE, 40, AbstractBlock.Settings.copy(OXIDIZED_COPPER_BUTTON)),
            "waxed_copper_button_oxidized",
            true
    );

    public static final Block POTION_PILLAR = register(
            new PotionPillar(AbstractBlock.Settings.create().mapColor(MapColor.DIAMOND_BLUE).nonOpaque().strength(1000000.0F, 3600000.0F).sounds(BlockSoundGroup.GLASS)),
            "potion_pillar",
            true
    );

    public static final Block PEDESTAL = register(
            new PedestalBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).nonOpaque().strength(4.0F, 6.0F).sounds(BlockSoundGroup.STONE).luminance((state) -> 12)),
            "pedestal",
            true
    );

    public static final Block KEY_DISPENSER_BLOCK = register(
            new KeyDispenserBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).nonOpaque().strength(4.0F, 6.0F).sounds(BlockSoundGroup.STONE).luminance((state) -> 12)),
            "key_dispenser",
            true
    );

    public static final Block KEY_ACCEPTOR = register(
            new KeyAcceptorBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).nonOpaque().strength(4.0F, 6.0F).sounds(BlockSoundGroup.STONE).luminance((state) -> 12)),
            "key_acceptor",
            true
    );
    public static final Block FANCY_RED_POT = register(
            new FancyPotBlock.RedFancyPot(AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.DECORATED_POT)),
            "fancy_red_pot",
            true
    );
    public static final Block FANCY_GREEN_POT = register(
            new FancyPotBlock.GreenFancyPot(AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.DECORATED_POT)),
            "fancy_green_pot",
            true
    );
    public static final Block FANCY_BLUE_POT = register(
            new FancyPotBlock.BlueFancyPot(AbstractBlock.Settings.create().breakInstantly().sounds(BlockSoundGroup.DECORATED_POT)),
            "fancy_blue_pot",
            true
    );

    // --- MINEABLE HAZARDS & DUNGEON GATES ---
    public static final Block DUNGEON_FLAME = register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(4.5F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE).emissiveLighting((state, blockView, pos) -> DungeonFlameBlock.getLuminance(state) > 0).luminance(DungeonFlameBlock::getLuminance), new DungeonFlameBlock.Config() {
                @Override public float getDamage(boolean powered) { return powered ? 1.0f : 0.0f; }
                @Override public ParticleEffect getParticle(boolean powered) { return powered ? ParticleTypes.FLAME : null; }
                @Override public boolean isLit(boolean powered) { return powered; }
            }),
            "dungeon_flame",
            true
    );

    public static final Block DEEP_FLAME = register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(4.5F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE).emissiveLighting((state, blockView, pos) -> DungeonFlameBlock.getLuminance(state) > 0).luminance(DungeonFlameBlock::getLuminance), new DungeonFlameBlock.Config() {
                @Override public float getDamage(boolean powered) { return powered ? 2.0f : 0.0f; }
                @Override public ParticleEffect getParticle(boolean powered) { return powered ? ParticleTypes.SOUL_FIRE_FLAME : null; }
                @Override public boolean isLit(boolean powered) { return powered; }
            }),
            "deep_flame",
            true
    );

    public static final Block FORTRESS_FLAME = register(
            new DungeonFlameBlock(AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).requiresTool().strength(4.5F, 6.0F).sounds(BlockSoundGroup.POLISHED_DEEPSLATE).emissiveLighting((state, blockView, pos) -> true).luminance(state -> 15), new DungeonFlameBlock.Config() {
                @Override public float getDamage(boolean powered) { return powered ? 2.0f : 1.0f; }
                @Override public ParticleEffect getParticle(boolean powered) { return powered ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME; }
                @Override public boolean isLit(boolean powered) { return true; }
            }),
            "fortress_flame",
            true
    );


    public static void initialize() {
        Registries.BLOCK.addAlias(BTC.identifierOf("dungeon_wire_v2"), BTC.identifierOf("dungeon_wire"));
        Registries.ITEM.addAlias(BTC.identifierOf("dungeon_wire_v2"), BTC.identifierOf("dungeon_wire"));
        Registries.BLOCK.addAlias(BTC.identifierOf("test_wire"), BTC.identifierOf("dungeon_wire"));
        Registries.ITEM.addAlias(BTC.identifierOf("test_wire"), BTC.identifierOf("dungeon_wire"));
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
        Registries.BLOCK.addAlias(BTC.identifierOf("fan"), BTC.identifierOf("waxed_copper_fan"));
        Registries.ITEM.addAlias(BTC.identifierOf("fan"), BTC.identifierOf("waxed_copper_fan"));
    }
}
package io.github.tobyrue.btc;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
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

    public static final Block CHISELED_COPPER_BRICKS = register(
            new Block(AbstractBlock.Settings.create().sounds(BlockSoundGroup.COPPER).mapColor(MapColor.TERRACOTTA_ORANGE).requiresTool().strength(1.5F, 6.0F).instrument(NoteBlockInstrument.SNARE)),
            "chiseled_copper_bricks",
            true
    );
    public static final DungeonFireBlock DUNGEON_FIRE = (DungeonFireBlock) register(
            new DungeonFireBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).requiresTool().luminance((state) -> {
                return 15;
            }).strength(3.5F, 3600000.0F)),
            "dungeon_fire",
            true

            );
    public static final OminousBeaconBlock OMINOUS_BEACON = (OminousBeaconBlock) register(
            new OminousBeaconBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).requiresTool().luminance((state) -> {
                return 14;
            }).strength(3.5F, 3600000.0F)),
            "ominous_beacon",
            true
    );

    public static final PressurePlateBlock DUNGEON_PRESSURE_PLATE = (PressurePlateBlock) register(
            new PressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.create().requiresTool().strength(2.0F, 3600000.0F)),
            "dungeon_pressure_plate",
            true
    );
    public static final AntierBlock ANTIER = (AntierBlock) register(
            new AntierBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 7;
            }).strength(-1.0F, 3600000.0F)),
            "antier",
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
    public static final DungeonWireBlock DUNGEON_WIRE = (DungeonWireBlock) register(
            new DungeonWireBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F).luminance(
                    DungeonWireBlock::getLuminance)
            ),

            "dungeon_wire",
            true
    );
    public static final CopperWireBlock COPPER_WIRE = (CopperWireBlock) register(
            new CopperWireBlock(AbstractBlock.Settings.create().requiresTool().strength(8F, 3600000.0F).luminance(
                    CopperWireBlock::getLuminance)
            ),
            "copper_wire",
            true
    );
    public static final FireDispenserBlock FIRE_DISPENSER = (FireDispenserBlock) register(
            new FireDispenserBlock(AbstractBlock.Settings.create().requiresTool().strength(4.5F, 3600000.0F).luminance(
                    FireDispenserBlock::getLuminance)
    ),
            "fire_dispenser",
            true
    );
    public static final DungeonDoorBlock DUNGEON_DOOR = (DungeonDoorBlock) register(
            new DungeonDoorBlock(AbstractBlock.Settings.create().requiresTool().strength(6.5F, 3600000.0F)),

            "dungeon_door",
            true
    );


    public static void initialize() {
    }

}

package io.github.tobyrue.btc;

import net.minecraft.block.*;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
    public static final DungeonFireBlock DUNGEON_FIRE = (DungeonFireBlock) register(
            new DungeonFireBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).luminance((state) -> {
                return 15;
            })),
            "dungeon_fire",
            true

            );

    public static final OminousBeaconBlock OMINOUS_BEACON = (OminousBeaconBlock) register(
            new OminousBeaconBlock(AbstractBlock.Settings.create().nonOpaque().sounds(BlockSoundGroup.VAULT).luminance((state) -> {
                return 14;
            })),
            "ominous_beacon",
            true
    );
    //STONE_PRESSURE_PLATE = register((String)"stone_pressure_plate", new PressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.create().mapColor(MapColor.STONE_GRAY).solid().instrument(NoteBlockInstrument.BASEDRUM).requiresTool().noCollision().strength(0.5F).pistonBehavior(PistonBehavior.DESTROY)));

    public static final PressurePlateBlock DUNGEON_PRESSURE_PLATE = (PressurePlateBlock) register(
            new PressurePlateBlock(BlockSetType.STONE, AbstractBlock.Settings.create().strength(-1.0F, 3600000.0F)),
            "dungeon_pressure_plate",
            true
    );

    public static final PedestalBlock PEDESTAL = (PedestalBlock) register(
            new PedestalBlock(AbstractBlock.Settings.create().nonOpaque().luminance((state) -> {
                return 12;
            }).strength(-1.0F, 3600000.0F)),
            "pedestal",
            true
    );
    public static final DungeonWireBlock DUNGEON_WIRE = (DungeonWireBlock) register(
            new DungeonWireBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F)),

            "dungeon_wire",
            true
    );
    public static final DungeonDoorBlock DUNGEON_DOOR = (DungeonDoorBlock) register(
            new DungeonDoorBlock(AbstractBlock.Settings.create().strength(1000000.0F, 3600000.0F)),

            "dungeon_door",
            true
    );


    public static void initialize() {
    }

}

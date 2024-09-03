package io.github.tobyrue.btc;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "staff_pedestal"), BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build());
    public static BlockEntityType<OminousBeaconBlockEntity> OMINOUS_BEACON_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "ominous_beacon"), BlockEntityType.Builder.create(OminousBeaconBlockEntity::new, ModBlocks.OMINOUS_BEACON).build());
    public static BlockEntityType<AntierBlockEntity> ANTIER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "antier"), BlockEntityType.Builder.create(AntierBlockEntity::new, ModBlocks.ANTIER).build());
    public static void initialize() {
    }
}



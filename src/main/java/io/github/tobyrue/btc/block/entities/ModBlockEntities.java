package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "staff_pedestal"), BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build());
    public static BlockEntityType<OminousBeaconBlockEntity> OMINOUS_BEACON_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "ominous_beacon"), BlockEntityType.Builder.create(OminousBeaconBlockEntity::new, ModBlocks.OMINOUS_BEACON).build());
    public static BlockEntityType<AntierBlockEntity> ANTIER_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "antier"), BlockEntityType.Builder.create(AntierBlockEntity::new, ModBlocks.ANTIER).build());
    public static BlockEntityType<KeyDispenserBlockEntity> KEY_DISPENSER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "key_dispenser"), BlockEntityType.Builder.create(KeyDispenserBlockEntity::new, ModBlocks.KEY_DISPENSER_BLOCK).build());
    public static BlockEntityType<FireDispenserBlockEntity> FIRE_DISPENSER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "fire_dispenser"), BlockEntityType.Builder.create(FireDispenserBlockEntity::new, ModBlocks.FIRE_DISPENSER).build());
    public static void initialize() {
    }
}



package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "staff_pedestal"), BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build());
    public static BlockEntityType<OminousBeaconBlockEntity> OMINOUS_BEACON_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "ominous_beacon"), BlockEntityType.Builder.create(OminousBeaconBlockEntity::new, ModBlocks.OMINOUS_BEACON).build());
    public static BlockEntityType<PotionPillarBlockEntity> POTION_PILLAR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "potion_pillar"), BlockEntityType.Builder.create(PotionPillarBlockEntity::new, ModBlocks.POTION_PILLAR).build());
    public static BlockEntityType<KeyDispenserBlockEntity> KEY_DISPENSER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "key_dispenser"), BlockEntityType.Builder.create(KeyDispenserBlockEntity::new, ModBlocks.KEY_DISPENSER_BLOCK).build());
    public static BlockEntityType<KeyAcceptorBlockEntity> KEY_ACCEPTOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "key_acceptor"), BlockEntityType.Builder.create(KeyAcceptorBlockEntity::new, ModBlocks.KEY_ACCEPTOR).build());
    public static BlockEntityType<FireDispenserBlockEntity> FIRE_DISPENSER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "fire_dispenser"), BlockEntityType.Builder.create(FireDispenserBlockEntity::new, ModBlocks.FIRE_DISPENSER).build());
    public static BlockEntityType<MobDetectorBlockEntity> MOB_DETECTOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("btc", "mob_detector"), BlockEntityType.Builder.create(MobDetectorBlockEntity::new, ModBlocks.MOB_DETECTOR).build());
    public static void initialize() {
    }
}



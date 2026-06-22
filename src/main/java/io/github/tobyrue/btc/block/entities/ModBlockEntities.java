package io.github.tobyrue.btc.block.entities;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.wires.WireBlockEntity;
import io.github.tobyrue.btc.wires.circuit.FPGABlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {

    public static BlockEntityType<PedestalBlockEntity> PEDESTAL_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("staff_pedestal"), BlockEntityType.Builder.create(PedestalBlockEntity::new, ModBlocks.PEDESTAL).build());
    public static BlockEntityType<OminousBeaconBlockEntity> OMINOUS_BEACON_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("ominous_beacon"), BlockEntityType.Builder.create(OminousBeaconBlockEntity::new, ModBlocks.OMINOUS_BEACON).build());
    public static BlockEntityType<PotionPillarBlockEntity> POTION_PILLAR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("potion_pillar"), BlockEntityType.Builder.create(PotionPillarBlockEntity::new, ModBlocks.POTION_PILLAR).build());
    public static BlockEntityType<KeyDispenserBlockEntity> KEY_DISPENSER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("key_dispenser"), BlockEntityType.Builder.create(KeyDispenserBlockEntity::new, ModBlocks.KEY_DISPENSER_BLOCK).build());
    public static BlockEntityType<KeyAcceptorBlockEntity> KEY_ACCEPTOR_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("key_acceptor"), BlockEntityType.Builder.create(KeyAcceptorBlockEntity::new, ModBlocks.KEY_ACCEPTOR).build());
    public static BlockEntityType<MobDetectorBlockEntity> MOB_DETECTOR_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("mob_detector"), BlockEntityType.Builder.create(MobDetectorBlockEntity::new, ModBlocks.MOB_DETECTOR).build());
    public static BlockEntityType<ObsidianChestBlockEntity> OBSIDIAN_CHEST_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("obsidian_chest"), BlockEntityType.Builder.create(ObsidianChestBlockEntity::new, ModBlocks.OBSIDIAN_CHEST).build());
    public static BlockEntityType<WireBlockEntity> WIRE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("dungeoun_wire_block"), BlockEntityType.Builder.create(WireBlockEntity::new, ModBlocks.DUNGEON_WIRE).build());
    public static BlockEntityType<FPGABlockEntity> DUNGEON_WIRE_CIRCUIT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("dungeon_wire_circuit_block"), BlockEntityType.Builder.create(FPGABlockEntity::new, ModBlocks.FPGA_BLOCK).build());
    public static BlockEntityType<BonfireBlockEntity> BONFIRE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("bonfire"), BlockEntityType.Builder.create(BonfireBlockEntity::new, ModBlocks.BONFIRE).build());
    public static BlockEntityType<TrialCoreBlockEntity> TRIAL_CORE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("trial_core"), BlockEntityType.Builder.create(TrialCoreBlockEntity::new, ModBlocks.TRIAL_CORE).build());
    public static BlockEntityType<FanBlockEntity> COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> EXPOSED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("exposed_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.EXPOSED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> WEATHERED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("weathered_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.WEATHERED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> OXIDIZED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("oxidized_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.OXIDIZED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> WAXED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("waxed_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.WAXED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> WAXED_EXPOSED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("waxed_exposed_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> WAXED_WEATHERED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("waxed_weathered_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FanBlockEntity> WAXED_OXIDIZED_COPPER_FAN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("waxed_oxidized_copper_fan"), BlockEntityType.Builder.create(FanBlockEntity::new, ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN).build());
    public static BlockEntityType<FancyPotBlockEntity> FANCY_RED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("fancy_red_pot"), BlockEntityType.Builder.create(FancyPotBlockEntity::new, ModBlocks.FANCY_RED_POT).build());
    public static BlockEntityType<FancyPotBlockEntity> FANCY_GREEN_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("fancy_green_pot"), BlockEntityType.Builder.create(FancyPotBlockEntity::new, ModBlocks.FANCY_GREEN_POT).build());
    public static BlockEntityType<FancyPotBlockEntity> FANCY_BLUE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, BTC.identifierOf("fancy_blue_pot"), BlockEntityType.Builder.create(FancyPotBlockEntity::new, ModBlocks.FANCY_BLUE_POT).build());
    public static void initialize() {
    }
}



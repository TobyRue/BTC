package io.github.tobyrue.btc.entity;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<EldritchLuminaryEntity> ELDRITCH_LUMINARY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "eldritch_luminary"),
            EntityType.Builder.create(EldritchLuminaryEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.75f, 1.9f).eyeHeight(1.6f).build());

    public static final EntityType<WaterBlastEntity> WATER_BLAST = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "water_blast"),
            EntityType.Builder.<WaterBlastEntity>create(WaterBlastEntity::new, SpawnGroup.MISC)
                    .dimensions(0.5f, 0.5f).build());

    public static final EntityType<CopperGolemEntity> COPPER_GOLEM = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "copper_golem"),
            EntityType.Builder.create(CopperGolemEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.8f, 1.4f).eyeHeight(0.75f).build());

    public static final EntityType<TuffGolemEntity> TUFF_GOLEM = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "tuff_golem"),
            EntityType.Builder.create(TuffGolemEntity::new, SpawnGroup.CREATURE)
                    .dimensions(0.8f, 1.1f).eyeHeight(0.75f).build());

    public static final EntityType<EarthSpikeEntity> EARTH_SPIKE = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "earth_spike"),
            EntityType.Builder.<EarthSpikeEntity>create(EarthSpikeEntity::new, SpawnGroup.MISC)
                    .dimensions(0.8f, 1.18f).eyeHeight(0.75f).build());
}
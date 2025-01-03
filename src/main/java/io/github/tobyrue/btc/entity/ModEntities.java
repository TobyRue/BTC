package io.github.tobyrue.btc.entity;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<EldritchLuminaryEntity> ELDRITCH_LUMINARY = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "eldritch_luminary"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EldritchLuminaryEntity::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 1.9f).withEyeHeight(1.7f)).build());
}

package io.github.tobyrue.btc.entity;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminariesEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<EldritchLuminariesEntity> ELDRITCH_LUMINARIES = Registry.register(Registries.ENTITY_TYPE,
            Identifier.of(BTC.MOD_ID, "eldritch_luminaries"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, EldritchLuminariesEntity::new).dimensions(EntityDimensions.fixed(1f, 1f)).build());
}

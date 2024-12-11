package io.github.tobyrue.btc;

import net.minecraft.entity.damage.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {

    public static RegistryKey<DamageType> BEACON_BURN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("btc", "beacon_burn"));
    public static RegistryKey<DamageType> WIND_BURST = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("btc", "wind_burst"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}

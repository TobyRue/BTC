package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import net.minecraft.block.MapColor;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class ModMapDecorationTypes {
    public static RegistryKey<MapDecorationType> BETTER_TRIAL_CHAMBERS_KEY = RegistryKey.of(
            Registries.MAP_DECORATION_TYPE.getKey(),
            Identifier.of(BTC.MOD_ID, "better_trial_chambers")
    );

    public static MapDecorationType BETTER_TRIAL_CHAMBERS;

    public static void register() {
        BETTER_TRIAL_CHAMBERS = Registry.register(
                Registries.MAP_DECORATION_TYPE,
                BETTER_TRIAL_CHAMBERS_KEY.getValue(),
                new MapDecorationType(
                        Identifier.of(BTC.MOD_ID, "better_trial_chambers"),
                        true, // show on item frame
                        10892044, // map color
                        true, // explorationMapElement
                        false // trackCount
                )
        );
    }
}

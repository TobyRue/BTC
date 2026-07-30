package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.ScrollTableScreenHandler;
import io.github.tobyrue.btc.client.screen.SpellScreenInventoryHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public class ModScreens {
    public static final ScreenHandlerType<ScrollTableScreenHandler> SCROLL_TABLE_SCREEN_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    BTC.identifierOf("scroll_table"),
                    new ExtendedScreenHandlerType<>(
                            (syncId, playerInventory, buf) -> new ScrollTableScreenHandler(syncId, playerInventory),
                            BlockPos.PACKET_CODEC
                    )
            );
    public static final ScreenHandlerType<SpellScreenInventoryHandler> SPELL_SCREEN_INVENTORY_HANDLER =
            Registry.register(
                    Registries.SCREEN_HANDLER,
                    BTC.identifierOf("spell_inventory"),
                    new ScreenHandlerType<>(SpellScreenInventoryHandler::new, FeatureFlags.VANILLA_FEATURES)
            );
    public static void register() {
    }
}

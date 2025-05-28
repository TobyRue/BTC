package io.github.tobyrue.btc.regestries;

import io.github.tobyrue.btc.BTC;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

import java.util.Optional;

public class BetterTrialChambersMapTrade implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
        // Log to confirm method entry

        if (!(entity.getWorld() instanceof ServerWorld serverWorld)) {
            return null;
        }

        BlockPos pos = serverWorld.locateStructure(BTC.BETTER_TRIAL_CHAMBERS_TAG, entity.getBlockPos(), 10000, false);

        System.out.println(pos);
        if (pos == null) {
            return null;
        }

        ItemStack mapStack = FilledMapItem.createMap(serverWorld, pos.getX(), pos.getZ(), (byte)2, true, true);
        MapState mapState = FilledMapItem.getMapState(mapStack, serverWorld);
        mapStack.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Eldritch Trial Chamber Map"));

        // Get the decoration type for "trial_chamber"
        RegistryEntry<MapDecorationType> markerType;
        try {
            markerType = Registries.MAP_DECORATION_TYPE.getEntry(ModMapDecorationTypes.BETTER_TRIAL_CHAMBERS);
        } catch (Exception e) {
            markerType = Registries.MAP_DECORATION_TYPE.getEntry(MapDecorationTypes.TARGET_X.value());
        }

        // Add the decoration at the structure's location
        mapState.addDecorationsNbt(
                mapStack, 
                pos,
                "better_trial_chambers",
                markerType
        );

        // Log map creation

        String mapId = String.valueOf(serverWorld.increaseAndGetMapId());
        serverWorld.getPersistentStateManager().set(mapId, mapState);

        // Log decoration placement

        TradedItem emeralds = new TradedItem(Items.EMERALD, 32);
        TradedItem compass = new TradedItem(Items.COMPASS, 1);

        // Log trade details

        return new TradeOffer(emeralds, Optional.of(compass), mapStack, 2, 5, 0.05F);
    }
}

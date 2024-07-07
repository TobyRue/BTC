package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.ModBlocks;
import io.github.tobyrue.btc.OminousBeaconBlock;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;


public class BTCClient implements ClientModInitializer {

   @Override
   public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.STAFF_ALTAR, RenderLayer.getCutoutMipped());
       }
}

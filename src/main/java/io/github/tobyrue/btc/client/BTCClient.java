package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModBlockEntities;
import io.github.tobyrue.btc.ModBlocks;
import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.resource.ResourceManager;

public class BTCClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_FIRE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTIER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_DOOR, RenderLayer.getCutoutMipped());
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockRenderer::new);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getEntityRenderDispatcher() != null) {
                try {
                    EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
                    BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, new WindStaffModelRenderer(new EntityRendererFactory.Context(dispatcher, client.getItemRenderer(), client.getBlockRenderManager(), dispatcher.getHeldItemRenderer(), client.getResourceManager(), client.getEntityModelLoader(), client.textRenderer)));
                    System.out.println("EntityRenderDispatcher is not null and model registration succeeded.");
                } catch (Exception e) {
                    System.err.println("Error during model registration: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("EntityRenderDispatcher is null.");
            }
        });
    }
}

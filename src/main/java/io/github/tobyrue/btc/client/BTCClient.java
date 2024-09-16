package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModBlockEntities;
import io.github.tobyrue.btc.ModBlocks;
import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.effect.StatusEffects;
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
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, new WindStaffModelRenderer());
    }
}

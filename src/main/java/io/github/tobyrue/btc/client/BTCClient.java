package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModBlockEntities;
import io.github.tobyrue.btc.ModBlocks;
import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class BTCClient implements ClientModInitializer {

//    private boolean windStaffRendererRegistered = false; // Flag to track registration status
    public static final EntityModelLayer WIND_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "wind_staff"), "main");

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_FIRE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTIER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_DOOR, RenderLayer.getCutoutMipped());
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockRenderer::new);

        // Register renderer only once



//        ClientTickEvents.END_CLIENT_TICK.register(client -> {
//            if (!windStaffRendererRegistered && client.getEntityRenderDispatcher() != null) { // Check if already registered
//                try {
//                    EntityRenderDispatcher dispatcher = client.getEntityRenderDispatcher();
//                    BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, new WindStaffModelRenderer(new EntityRendererFactory.Context(dispatcher, client.getItemRenderer(), client.getBlockRenderManager(), dispatcher.getHeldItemRenderer(), client.getResourceManager(), client.getEntityModelLoader(), client.textRenderer)));
//                    windStaffRendererRegistered = true; // Mark as registered
//                } catch (Exception e) {
//                    System.err.println("Error during WindStaff model registration: " + e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });
//        ModelPart windStaffModelPart = createWindStaffModelPart();
//        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, new WindStaffModelRenderer(windStaffModelPart));

        EntityModelLayerRegistry.registerModelLayer(WIND_STAFF_LAYER, WindStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WIND_STAFF_LAYER);
            new WindStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });
    }
//    private ModelPart createWindStaffModelPart() {
//        ModelData modelData = new ModelData();
//        ModelPartData rootData = modelData.getRoot();
//
//        rootData.addChild("element1", new ModelPart(0, 0, 16, 16), ModelTransform.pivot(0, 0, 0));
//        rootData.addChild("element2", new ModelPart(0, 0, 16, 16), ModelTransform.pivot(0, 0, 0));
//        rootData.addChild("element3", new ModelPart(0, 0, 16, 16), ModelTransform.pivot(0, 0, 0));
//        rootData.addChild("element4", new ModelPart(0, 0, 16, 16), ModelTransform.pivot(0, 0, 0));
//
//        // Initialize and configure the root ModelPart with children and other settings
//        // For example:
//        // root.addChild("part1", new ModelPart(0, 0, 16, 16), ModelTransform.pivot(0, 0, 0));
//        // Add other parts similarly
//        return new ModelPart(modelData.createModel());
//    }
}

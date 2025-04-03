package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.ModBlockEntities;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class BTCClient implements ClientModInitializer {

    public static final EntityModelLayer WIND_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "wind_staff"), "main");
    public static final EntityModelLayer FIRE_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "fire_staff"), "main");
    public static final EntityModelLayer DRAGON_STAFF_LAYER = new EntityModelLayer(Identifier.of("btc", "dragon_staff"), "main");

    public static final EntityModelLayer BOOK_LAYER = new EntityModelLayer(Identifier.of("btc", "spell_book"), "main");

    @Override
    public void onInitializeClient() {
        DrowningEffectOverlay.register();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OMINOUS_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_FIRE, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.FIRE_DISPENSER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ANTIER, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DUNGEON_DOOR, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.KEY_DISPENSER_BLOCK, RenderLayer.getCutoutMipped());
        BlockEntityRendererRegistry.register(ModBlockEntities.PEDESTAL_BLOCK_ENTITY, PedestalBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.OMINOUS_BEACON_BLOCK_ENTITY, OminousBeaconBlockRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.KEY_DISPENSER_ENTITY, KeyDispenserBlockRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(WIND_STAFF_LAYER, WindStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.WIND_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(WIND_STAFF_LAYER);
            new WindStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });
        EntityModelLayerRegistry.registerModelLayer(FIRE_STAFF_LAYER, FireStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.FIRE_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(FIRE_STAFF_LAYER);
            new FireStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });
        EntityModelLayerRegistry.registerModelLayer(DRAGON_STAFF_LAYER, DragonStaffModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.DRAGON_STAFF, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(DRAGON_STAFF_LAYER);
            new DragonStaffModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityModelLayerRegistry.registerModelLayer(BOOK_LAYER, SpellBookModelRenderer::getTexturedModelData);
        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.SPELL_BOOK, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            ModelPart root = MinecraftClient.getInstance().getEntityModelLoader().getModelPart(BOOK_LAYER);
            new SpellBookModelRenderer(root).render(stack, mode, matrices, vertexConsumers, light, overlay);
        });

        EntityRendererRegistry.register(ModEntities.ELDRITCH_LUMINARY, EldritchLuminaryRenderer::new);
        EntityRendererRegistry.register(ModEntities.WATER_BLAST, WaterBlastEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.COPPER_GOLEM, CopperGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.TUFF_GOLEM, TuffGolemRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ELDRITCH_LUMINARY, EldritchLuminaryModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.WATER_BURST, WaterBlastEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.COPPER_GOLEM, CopperGolemModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.TUFF_GOLEM, TuffGolemEntityModel::getTexturedModelData);
    }
}

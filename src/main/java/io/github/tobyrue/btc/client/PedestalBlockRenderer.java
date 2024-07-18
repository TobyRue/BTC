package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import io.github.tobyrue.btc.PedestalBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.VaultBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Scaling;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class PedestalBlockRenderer implements BlockEntityRenderer<PedestalBlockEntity> {
    private static ItemStack stack = new ItemStack(ModItems.STAFF, 1);
    private static ItemStack key1 = new ItemStack(ModItems.RUBY_TRIAL_KEY, 1);



    public PedestalBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(PedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var c = blockEntity.HASH_MAP.getOrDefault(MinecraftClient.getInstance().player.getUuid().toString(),0);
        if(c != -1) {
            // Move the item
            renderKeys(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay, c);
            matrices.translate(0.5, 1.25, 0.5);

            // Rotate the item
            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        }

        // Mandatory call after GL calls
        matrices.pop();
    }
    public void renderItem(PedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderItem(blockEntity, stack, tickDelta, matrices, vertexConsumers, light, overlay, 0.5, 0.5, 0);
    }

    public void renderItem(PedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, double x, double z, float dTheta) {
        matrices.push();
        double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;
        // Move the item
        matrices.translate(x, 1.3 + offset, z);

        // Rotate the item
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4 + (dTheta * 360)));
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        // Mandatory call after GL calls
        matrices.pop();
    }

    public void renderKeys(PedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, int c) {
        matrices.push();
        switch(c) {
            case 4:
                renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.2, 0.25f);
            case 3:
                renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.8, 0.5f);
            case 2:
                renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.8, 0.8, 0.75f);
            case 1:
                renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.8, 0.2, 0f);
        }
        matrices.pop();
    }
}

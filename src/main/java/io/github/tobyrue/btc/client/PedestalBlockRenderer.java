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
        // Move the item
        renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.translate(0.5, 1.25, 0.5);

        // Rotate the item
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.HEAD, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        // Mandatory call after GL calls
        matrices.pop();
    }
    public void renderItem(PedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderItem(blockEntity, stack, tickDelta, matrices, vertexConsumers, light, overlay, 0.5, 0.5);
    }

    public void renderItem(PedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, double x, double z) {
        matrices.push();
        double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;
        // Move the item
        matrices.translate(x, 1.5 + offset, z);

        // Rotate the item
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4));
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        // Mandatory call after GL calls
        matrices.pop();
    }

    public void renderKeys(PedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.2);
        renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.8);
        renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.8, 0.8);
        renderItem(blockEntity, new ItemStack(ModItems.RUBY_TRIAL_KEY), tickDelta, matrices, vertexConsumers, light, overlay, 0.8, 0.2);
        matrices.pop();
    }
}

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.ItemPedestalBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ItemPedestalBlockRenderer implements BlockEntityRenderer<ItemPedestalBlockEntity> {

    public ItemPedestalBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(ItemPedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        MinecraftClient client = MinecraftClient.getInstance();
        if (blockEntity.canShowTo(client.player)) {
            renderItem(blockEntity, blockEntity.getStack(), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.2, 0.25f);
        }
        matrices.pop();
    }
    public void renderItem(ItemPedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderItem(blockEntity, stack, tickDelta, matrices, vertexConsumers, light, overlay, 0.5, 0.5, 0);
    }

    public void renderItem(ItemPedestalBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, double x, double z, float dTheta) {
        matrices.push();
        matrices.translate(0.5, 1.2, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((blockEntity.getWorld().getTime() + tickDelta) * 4 + (dTheta * 360)));
        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, blockEntity.getWorld(), 0);
        matrices.pop();
    }
}

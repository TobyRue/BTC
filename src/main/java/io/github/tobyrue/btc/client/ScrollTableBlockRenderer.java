package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.ScrollTableBlockEntity;
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
public class ScrollTableBlockRenderer implements BlockEntityRenderer<ScrollTableBlockEntity> {
    public ScrollTableBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(
            ScrollTableBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        var world = entity.getWorld();
        if (world == null) return;

        int cachedLight = WorldRenderer.getLightmapCoordinates(world, entity.getPos().up());

        for (int i = 0; i < 9; i++) {
            ItemStack stack = entity.getStack(i);
            if (stack.isEmpty()) continue;

            renderItem(entity, stack, matrices, vertexConsumers, cachedLight, overlay, i);
        }
    }

    public void renderItem(
            ScrollTableBlockEntity blockEntity,
            ItemStack stack,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            int slot
    ) {
        matrices.push();

        switch (slot) {
            case 0 -> matrices.translate(5.0/16.0, 0, 11.0/16.0);
            case 1 -> matrices.translate(4.0/16.0, 0, 8.0/16.0);
            case 2 -> matrices.translate(5.0/16.0, 0, 5.0/16.0);
            case 3 -> matrices.translate(8.0/16.0, 0, 12.0/16.0);
            case 4 -> matrices.translate(8.0/16.0, 0, 8.0/16.0);
            case 5 -> matrices.translate(8.0/16.0, 0, 4.0/16.0);
            case 6 -> matrices.translate(11.0/16.0, 0, 11.0/16.0);
            case 7 -> matrices.translate(12.0/16.0, 0, 8.0/16.0);
            case 8 -> matrices.translate(11.0/16.0, 0, 5.0/16.0);
            default -> matrices.translate(0, 0, 0);
        }

        matrices.translate(0, 0.875, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));

        matrices.scale(3.0f / 16.0f, 3.0f / 16.0f, 3.0f / 16.0f);

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GUI,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                blockEntity.getWorld(),
                0
        );

        matrices.pop();
    }
}
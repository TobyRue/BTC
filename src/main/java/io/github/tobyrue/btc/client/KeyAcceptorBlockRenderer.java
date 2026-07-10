package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.KeyAcceptorBlock;
import io.github.tobyrue.btc.block.entities.KeyAcceptorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class KeyAcceptorBlockRenderer implements BlockEntityRenderer<KeyAcceptorBlockEntity> {
    private static final ItemStack REGULAR_KEY = new ItemStack(Items.TRIAL_KEY, 1);
    private static final ItemStack OMINOUS_KEY = new ItemStack(Items.OMINOUS_TRIAL_KEY, 1);

    public KeyAcceptorBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(KeyAcceptorBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.getWorld() == null) return;

        BlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());
        if (!state.contains(KeyAcceptorBlock.POWERED) || !state.get(KeyAcceptorBlock.POWERED)) {
            return;
        }

        boolean isOminous = state.contains(KeyAcceptorBlock.IS_OMINOUS) && state.get(KeyAcceptorBlock.IS_OMINOUS);
        ItemStack keyToRender = isOminous ? OMINOUS_KEY : REGULAR_KEY;

        float currentDelay = blockEntity.delay;
        if (state.contains(KeyAcceptorBlock.STAYS_POWERED) && state.get(KeyAcceptorBlock.STAYS_POWERED)) {
            double time = (blockEntity.getWorld().getTime() + tickDelta) * 0.1;
            double bobOffset = Math.sin(time) * 0.05;

            renderItem(blockEntity, keyToRender, tickDelta, matrices, vertexConsumers, light, overlay, 1.1 + (40.0 / 60.0) + bobOffset, currentDelay * 0.12f);
        } else {
            double heightOffset = 1.1 + ((double) currentDelay / 60.0);
            renderItem(blockEntity, keyToRender, tickDelta, matrices, vertexConsumers, light, overlay, heightOffset, currentDelay * 0.12f);
        }
    }

    private void renderItem(KeyAcceptorBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, double height, float dTheta) {
        matrices.push();
        matrices.translate(0.5, height, 0.5);

        float spinAngle = (blockEntity.getWorld().getTime() + tickDelta) * 4.0f + (dTheta * 360.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(spinAngle));

        int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GROUND,
                lightAbove,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                blockEntity.getWorld(),
                0
        );
        matrices.pop();
    }
}
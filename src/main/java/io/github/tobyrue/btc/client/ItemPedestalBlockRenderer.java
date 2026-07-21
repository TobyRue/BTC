package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.ItemPedestalBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.VectorUtils;
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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ItemPedestalBlockRenderer implements BlockEntityRenderer<ItemPedestalBlockEntity> {
    private static final float TURN_SPEED = 0.05f; // smoothing factor

    public ItemPedestalBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(ItemPedestalBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        MinecraftClient client = MinecraftClient.getInstance();
        var world = blockEntity.getWorld();
        if (world == null) return;

        long time = world.getTime();

        if (blockEntity.canShowTo(client.player)) {
            if (!client.player.isSpectator()) {
                renderItem(blockEntity, blockEntity.getStack(), tickDelta, matrices, vertexConsumers, light, overlay, 0.2, 0.2, 0.25f);
            } else {




                Entity target = client.player;

                Vec3d eyePos = Vec3d.ofCenter(blockEntity.getPos());
                Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0);

                double[] angles = VectorUtils.getPitchAndYaw(eyePos, targetPos);

                float desiredYaw = (float) angles[0];
                float desiredPitch = (float) angles[1];


                float currentYaw = blockEntity.getEyeYaw();
                float yawDelta = MathHelper.wrapDegrees(desiredYaw - currentYaw);
                currentYaw += yawDelta * TURN_SPEED;
                blockEntity.setEyeYaw(currentYaw);

                float currentPitch = blockEntity.getEyePitch();
                float pitchDelta = MathHelper.wrapDegrees(desiredPitch - currentPitch);
                currentPitch += pitchDelta * TURN_SPEED;
                blockEntity.setEyePitch(currentPitch);

                matrices.push();
                matrices.translate(0.5, 1.2, 0.5);

                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getEyeYaw()));

                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(blockEntity.getEyePitch()));

                int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());

                MinecraftClient.getInstance().getItemRenderer().renderItem(
                        blockEntity.getStack(),
                        ModelTransformationMode.GROUND,
                        lightAbove,
                        OverlayTexture.DEFAULT_UV,
                        matrices,
                        vertexConsumers,
                        world,
                        0
                );

                matrices.pop();
            }
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

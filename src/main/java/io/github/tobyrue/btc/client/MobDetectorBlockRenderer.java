package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import io.github.tobyrue.btc.block.entities.KeyDispenserBlockEntity;
import io.github.tobyrue.btc.block.entities.MobDetectorBlockEntity;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MobDetectorBlockRenderer implements BlockEntityRenderer<MobDetectorBlockEntity> {
    private static ItemStack eye = new ItemStack(Items.ENDER_EYE, 1);

    public MobDetectorBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(MobDetectorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        long time = entity.getWorld().getTime();
        var entities = entity.getEntityList();

        if (entities.isEmpty()) {
            renderItem(entity, eye, tickDelta, matrices, vertexConsumers, light, overlay, 0.25f);
        } else {
            int index = (int) ((time / 40) % entities.size());
            var target = entities.get(index);

            if (target == null) return;

            renderEyeFacingEntity(entity, target, tickDelta, matrices, vertexConsumers, light, overlay);
        }
    }

    public void renderItem(MobDetectorBlockEntity blockEntity, ItemStack stack, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderItem(blockEntity, stack, tickDelta, matrices, vertexConsumers, light, overlay, 0);
    }

    public void renderItem(
            MobDetectorBlockEntity blockEntity,
            ItemStack stack,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            float dTheta
    ) {
        matrices.push();

        float bob = MathHelper.sin((blockEntity.getWorld().getTime() + tickDelta) * 0.1F) * 0.05F;

        matrices.translate(0.5, 0.5 + bob, 0.5);

        matrices.multiply(
                RotationAxis.POSITIVE_Y.rotationDegrees(
                        (blockEntity.getWorld().getTime() + tickDelta) * 4 + (dTheta * 360)
                )
        );

        int lightAbove = WorldRenderer.getLightmapCoordinates(
                blockEntity.getWorld(),
                blockEntity.getPos().up()
        );

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


    private void renderEyeFacingEntity(
            MobDetectorBlockEntity blockEntity,
            net.minecraft.entity.Entity target,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        matrices.push();

        float bob = MathHelper.sin((blockEntity.getWorld().getTime() + tickDelta) * 0.1F) * 0.05F;

        matrices.translate(0.5, 0.5 + bob, 0.5);

        Vec3d targetPos = target.getPos().add(
                target.getVelocity().multiply(tickDelta)
        );

        Vec3d blockPos = Vec3d.ofCenter(blockEntity.getPos());

        Vec3d dir = targetPos.subtract(blockPos).normalize();

        float yaw = (float) (MathHelper.atan2(dir.z, dir.x) * (180 / Math.PI)) - 90.0F;

        float pitch = (float) (-(MathHelper.atan2(dir.y, Math.sqrt(dir.x * dir.x + dir.z * dir.z)) * (180 / Math.PI)));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(pitch));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(
                (blockEntity.getWorld().getTime() + tickDelta) * 2
        ));

        int lightAbove = WorldRenderer.getLightmapCoordinates(
                blockEntity.getWorld(),
                blockEntity.getPos().up()
        );

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                eye,
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



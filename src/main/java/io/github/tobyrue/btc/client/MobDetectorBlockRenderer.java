package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.OminousBeaconBlock;
import io.github.tobyrue.btc.block.entities.KeyDispenserBlockEntity;
import io.github.tobyrue.btc.block.entities.MobDetectorBlockEntity;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.VectorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class MobDetectorBlockRenderer implements BlockEntityRenderer<MobDetectorBlockEntity> {
    private static ItemStack eye = new ItemStack(Items.ENDER_EYE, 1);
    private static final int SWITCH_INTERVAL = 40; // ticks (2 seconds)
    private static final float TURN_SPEED = 0.05f; // smoothing factor

    public MobDetectorBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(
            MobDetectorBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        var world = entity.getWorld();
        if (world == null) return;

        long time = world.getTime();
        var ids = entity.getTrackedEntityIds();

        entity.pruneInvalidEntities(world);

        List<Entity> targets = new ArrayList<>();
        for (Integer id : ids) {
            Entity e = world.getEntityById(id);
            if (e != null && e.isAlive()) {
                targets.add(e);
            }
        }

        if (targets.isEmpty()) {
            renderItem(entity, eye, tickDelta, matrices, vertexConsumers, light, overlay);
            return;
        }

        int targetIndex = Math.min(entity.getTargetIndex(), targets.size() - 1);

        if (time - entity.getLastSwitchTime() > SWITCH_INTERVAL) {
            targetIndex = (targetIndex + 1) % targets.size();
            entity.setLastSwitchTime(time);
        }

        entity.setTargetIndex(targetIndex);

        Entity target = targets.get(targetIndex);

        Vec3d eyePos = Vec3d.ofCenter(entity.getPos());
        Vec3d targetPos = target.getPos().add(0, target.getHeight() * 0.5, 0);

        double[] angles = VectorUtils.getPitchAndYaw(eyePos, targetPos);

        float desiredYaw = (float) angles[0];
        float desiredPitch = (float) angles[1];


        float currentYaw = entity.getEyeYaw();
        float yawDelta = MathHelper.wrapDegrees(desiredYaw - currentYaw);
        currentYaw += yawDelta * TURN_SPEED;
        entity.setEyeYaw(currentYaw);

        float currentPitch = entity.getEyePitch();
        float pitchDelta = MathHelper.wrapDegrees(desiredPitch - currentPitch);
        currentPitch += pitchDelta * TURN_SPEED;
        entity.setEyePitch(currentPitch);

        float timeWrapped = (entity.getWorld().getTime() % 360) + tickDelta; // wrap every 360 ticks
        float bob = MathHelper.sin(timeWrapped * 0.1F) * 0.025F;

        matrices.push();
        matrices.translate(0.5, 0.5 + bob, 0.5);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getEyeYaw()));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getEyePitch()));

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());

        MinecraftClient.getInstance().getItemRenderer().renderItem(
                eye,
                ModelTransformationMode.GROUND,
                lightAbove,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                0
        );

        matrices.pop();

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

        float timeWrapped = (blockEntity.getWorld().getTime() % 360) + tickDelta; // wrap every 360 ticks
        float bob = MathHelper.sin(timeWrapped * 0.1F) * 0.025F;

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
}



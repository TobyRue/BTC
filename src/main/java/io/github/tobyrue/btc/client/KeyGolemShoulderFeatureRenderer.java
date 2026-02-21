package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class KeyGolemShoulderFeatureRenderer<T extends PlayerEntity> extends FeatureRenderer<T, PlayerEntityModel<T>> {
    // Replace with your actual Golem model class
    private final KeyGolemModel model;
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/entity/key_golem.png");

    public KeyGolemShoulderFeatureRenderer(FeatureRendererContext<T, PlayerEntityModel<T>> context, EntityModelLoader loader) {
        super(context);
        // Replace with your actual Model Layer
        this.model = new KeyGolemModel(loader.getModelPart(ModModelLayers.KEY_GOLEM));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        renderShoulderGolem(matrices, vertexConsumers, light, player, limbAngle, limbDistance, headYaw, headPitch, true);
        renderShoulderGolem(matrices, vertexConsumers, light, player, limbAngle, limbDistance, headYaw, headPitch, false);
    }

    private void renderShoulderGolem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T player, float limbAngle, float limbDistance, float headYaw, float headPitch, boolean leftShoulder) {
        NbtCompound nbt = leftShoulder ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
        if (nbt.getString("id").equals("btc:key_golem")) {
            matrices.push();

            matrices.translate(leftShoulder ? 0.4F : -0.4F, player.isInSneakingPose() ? -0.7F : -0.9F, 0.0F);
            matrices.scale(0.6f, 0.6f, 0.6f);
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(KeyGolemEntityRenderer.GOLD));

            this.model.poseOnShoulder(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, limbAngle, limbDistance, headYaw, headPitch, player.age);

            matrices.pop();
        }
    }
}
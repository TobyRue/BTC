package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WaterBlastEntityRenderer extends EntityRenderer<WaterBlastEntity> {
    protected WaterBlastEntityModel model;

    public WaterBlastEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new WaterBlastEntityModel(ctx.getPart(ModModelLayers.WATER_BURST));
    }

    @Override
    public Identifier getTexture(WaterBlastEntity entity) {
        return Identifier.of(BTC.MOD_ID, "textures/entity/water_blast.png");
    }

    @Override
    public void render(WaterBlastEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // Applying rotations
        float interpolatedYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        float interpolatedPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((interpolatedYaw + 180.0F)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(interpolatedPitch));

        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
        this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class WaterBlastEntityRenderer extends EntityRenderer<WaterBlastEntity> {
    protected WaterBlastEntityModel model;

    public WaterBlastEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new WaterBlastEntityModel(ctx.getPart(WaterBlastEntityModel.WATER_BURST));
    }

    @Override
    public Identifier getTexture(WaterBlastEntity entity) {
        return Identifier.of(BTC.MOD_ID, "textures/entity/water_blast.png");
    }

    @Override
    public void render(WaterBlastEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();
        
        // Interpolating yaw for smooth rotation
//        float interpolatedYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());

        // Applying rotations
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(interpolatedYaw - 90.0F)); // Adjusting for Minecraft's coordinate system
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
//        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));
        // Rendering the model
        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
                this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);

//        if(!entity.isGrounded()) {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw())));
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getRenderingRotation() * 5f));
//            matrices.translate(0, -1.0f, 0);
//        } else {
//            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.groundedOffset.getY()));
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.groundedOffset.getX()));
//            matrices.translate(0, -1.0f, 0);
//        }
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
}

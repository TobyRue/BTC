package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.TuffGolemEntityModel;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class TuffGolemClothRarityOverlay extends FeatureRenderer<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> {
    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/red_overlay_blank.png");

    public TuffGolemClothRarityOverlay(FeatureRendererContext<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, TuffGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
        TuffGolemEntityModel<TuffGolemEntity> model = this.getContextModel();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(TEXTURE));
        model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
    }
}

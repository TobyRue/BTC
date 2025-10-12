package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.EldritchLuminaryModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class LuminaryCapeRenderer extends FeatureRenderer<EldritchLuminaryEntity, EldritchLuminaryModel<EldritchLuminaryEntity>> {
    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary_1.png");


    public LuminaryCapeRenderer(FeatureRendererContext<EldritchLuminaryEntity, EldritchLuminaryModel<EldritchLuminaryEntity>> context) {
        super(context);
    }

    @Override
    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            EldritchLuminaryEntity entity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        if (entity.isInvisible()) return;

        matrices.push();
        matrices.translate(0.0F, 0.0F, 0.33F);

        // Simulate simple cape swing using movement and limb animation
        float movement = MathHelper.sin(limbAngle * 0.6662F) * limbDistance;
        float verticalSwing = MathHelper.abs(movement) * 15.0F;
        float baseAngle = 5.0F + verticalSwing;

        // If the entity is sneaking, lift the cape a bit
        if (entity.isInSneakingPose()) {
            matrices.translate(0.0F, 0.2F, 0.05F);
            baseAngle += 10.0F;
        }

        // Smooth idle motion
        float time = entity.age + tickDelta;
        float idleWave = MathHelper.sin(time * 0.1F) * 2.0F;

        // Apply final rotation for flowing effect
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(baseAngle + idleWave));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        // Render cape from model
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        EldritchLuminaryModel<EldritchLuminaryEntity> model = this.getContextModel();
        model.renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
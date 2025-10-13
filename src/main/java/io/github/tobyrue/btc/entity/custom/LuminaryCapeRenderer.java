package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.EldritchLuminaryModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
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

@Environment(value= EnvType.CLIENT)
public class LuminaryCapeRenderer extends FeatureRenderer<EldritchLuminaryEntity, EldritchLuminaryModel<EldritchLuminaryEntity>> {
    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary.png");


    private float prevCapeAngle = 5.0F;

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

        // Calculate target cape angle from movement
        float movementSpeed = MathHelper.clamp(limbDistance * 3.0F, 0.0F, 1.0F);
        float targetAngle = 5.0F + movementSpeed * 25.0F;

        // Apply inertia (smooth transition to target angle)
        // The smaller the 0.15F, the smoother and slower the response
        prevCapeAngle += (targetAngle - prevCapeAngle) * 0.15F;

        float baseAngle = prevCapeAngle;

        // Sneaking adjustment
        if (entity.isInSneakingPose()) {
            matrices.translate(0.0F, 0.2F, 0.05F);
            baseAngle += 10.0F;
        }

        // Gentle idle wave (small breathing motion)
        float time = entity.age + tickDelta;
        float idleWave = MathHelper.sin(time * 0.1F) * 2.0F;

        // Apply transformations
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(baseAngle + idleWave));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        // Render the cape
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        EldritchLuminaryModel<EldritchLuminaryEntity> model = this.getContextModel();
        model.renderCape(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}
package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.MineEntity;
import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class TrialCubeEntityRenderer extends EntityRenderer<TrialCubeEntity> {
    private static final float MIN_BRIGHTNESS = 0.75f;
    private static final float MAX_BRIGHTNESS = 1.0f;
    private static final float PULSE_SPEED = 0.1f;     // lower = slower pulse

    private static final Identifier COMPANION =
            Identifier.of(BTC.MOD_ID, "textures/entity/companion_cube_base.png");

    private static final Identifier COMPANION_GLOW =
            BTC.identifierOf("textures/entity/companion_cube_glow.png");

    private static final Identifier TRIAL =
            Identifier.of(BTC.MOD_ID, "textures/entity/trial_cube_base.png");

    private static final Identifier TRIAL_GLOW =
            Identifier.of(BTC.MOD_ID, "textures/entity/trial_cube_glow.png");

    protected final TrialCubeEntityModel<TrialCubeEntity> model;

    public TrialCubeEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new TrialCubeEntityModel(ctx.getPart(ModModelLayers.TRIAL_CUBE));
    }


    @Override
    public Identifier getTexture(TrialCubeEntity entity) {
        if (entity.isCompanion()) {
            return COMPANION;
        } else {
            return TRIAL;
        }
    }

    public Identifier getGlowTexture(TrialCubeEntity entity) {
        if (entity.isCompanion()) {
            return COMPANION_GLOW;
        } else {
            return TRIAL_GLOW;
        }
    }

    @Override
    public void render(TrialCubeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        float time = (Objects.requireNonNull(entity.getWorld()).getTime() + tickDelta);
        float wave = (float)((Math.cos(time * PULSE_SPEED) + 1.0) / 2.0);
        float pulse = MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * wave;
        int color = getColor(pulse);

        VertexConsumer baseBuffer =
                vertexConsumers.getBuffer(model.getLayer(getTexture(entity)));
        model.render(matrices, baseBuffer, light, OverlayTexture.DEFAULT_UV);

        VertexConsumer glowBuffer =
                vertexConsumers.getBuffer(RenderLayer.getEyes(getGlowTexture(entity)));
        model.render(matrices, glowBuffer, light, OverlayTexture.DEFAULT_UV, color);

        matrices.pop();
    }

    private static int getColor(float pulse) {
        int r = 255;
        int g = 255;
        int b = 255;

        r = (int)(r * pulse);
        g = (int)(g * pulse);
        b = (int)(b * pulse);

        r = Math.min(255, r);
        g = Math.min(255, g);
        b = Math.min(255, b);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }
}
package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.*;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EarthSpikeRenderer extends EntityRenderer<EarthSpikeEntity> {

    protected EarthSpikeModel model;

    private static final Identifier SPIKE = Identifier.of(BTC.MOD_ID, "textures/entity/earth_spike.png");

    public EarthSpikeRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new EarthSpikeModel<>(ctx.getPart(ModModelLayers.EARTH_SPIKE));
    }


    @Override
    public Identifier getTexture(EarthSpikeEntity entity) {
        return SPIKE;
    }

    @Override
    public void render(EarthSpikeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
        this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}

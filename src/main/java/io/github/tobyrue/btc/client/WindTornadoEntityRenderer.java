package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.Identifier;

public class WindTornadoEntityRenderer extends EntityRenderer<WindTornadoEntity> {
    protected WindTornadoEntityModel model;

    protected WindTornadoEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new WindTornadoEntityModel(ctx.getPart(ModModelLayers.WIND_TORNADO));
    }

    @Override
    public Identifier getTexture(WindTornadoEntity entity) {
        return Identifier.of(BTC.MOD_ID, "textures/entity/wind_tornado.png");
    }
    @Override
    public void render(WindTornadoEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        float h = (float)entity.age + tickDelta;
        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
        this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);
        this.model.setAngles(entity, 0.0f, 0.0f, h, 0.0f, 0.0f);
        matrices.pop();
    }
}

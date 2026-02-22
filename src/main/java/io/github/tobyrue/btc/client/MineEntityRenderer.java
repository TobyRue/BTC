package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.MineEntity;
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
public class MineEntityRenderer extends EntityRenderer<MineEntity> {
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/mine.png");
    protected MineEntityModel model;

    protected MineEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new MineEntityModel(ctx.getPart(ModModelLayers.MINE));
    }

    @Override
    public void render(MineEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
       super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
       matrices.push();
       VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
       this.model.getLayer(getTexture(entity)), false, false);
       this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);
       matrices.pop();
    }

    @Override
    public Identifier getTexture(MineEntity entity) {
        return TEXTURE;
    }
}

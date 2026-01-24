package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class SuperHappyKillBallEntityRenderer extends EntityRenderer<SuperHappyKillBallEntity>{
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/high_energy_pellet.png");
    protected SuperHappyKillBallEntityModel model;

    public SuperHappyKillBallEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new SuperHappyKillBallEntityModel(ctx.getPart(ModModelLayers.SHKB));
    }

    protected int getBlockLight(SuperHappyKillBallEntity entity, BlockPos blockPos) {
        return 15;
    }

    public void render(SuperHappyKillBallEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        float h = (float)entity.age + g;
        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumerProvider,
                this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrixStack, vertexconsumer, i, OverlayTexture.DEFAULT_UV);
        this.model.setAngles(entity, 0.0f, 0.0f, h, 0.0f, 0.0f);
        matrixStack.pop();
    }


    public Identifier getTexture(SuperHappyKillBallEntity entity) {
        return TEXTURE;
    }
}

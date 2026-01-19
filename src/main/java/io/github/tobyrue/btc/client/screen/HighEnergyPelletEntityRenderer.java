package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.HighEnergyPelletEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class HighEnergyPelletEntityRenderer extends EntityRenderer<HighEnergyPelletEntity>{
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/high_energy_pellet.png");
    private static final RenderLayer LAYER;

    public HighEnergyPelletEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    protected int getBlockLight(HighEnergyPelletEntity entity, BlockPos blockPos) {
        return 15;
    }

    public void render(HighEnergyPelletEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.scale(2.0F, 2.0F, 2.0F);
        matrixStack.multiply(this.dispatcher.getRotation());
        MatrixStack.Entry entry = matrixStack.peek();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        produceVertex(vertexConsumer, entry, i, 0.0F, 0, 0, 1);
        produceVertex(vertexConsumer, entry, i, 1.0F, 0, 1, 1);
        produceVertex(vertexConsumer, entry, i, 1.0F, 1, 1, 0);
        produceVertex(vertexConsumer, entry, i, 0.0F, 1, 0, 0);
        matrixStack.pop();
        super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void produceVertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, int light, float x, int z, int textureU, int textureV) {
        vertexConsumer.vertex(matrix, x - 0.5F, (float)z - 0.25F, 0.0F).color(-1).texture((float)textureU, (float)textureV).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    public Identifier getTexture(HighEnergyPelletEntity entity) {
        return TEXTURE;
    }

    static {
        LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);
    }
}

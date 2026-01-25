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
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
        this.shadowRadius = 1 * (float)entity.getSize();

        matrixStack.push();
        float h = (float)entity.age + g;
        scale(entity, matrixStack);
        VertexConsumer vertexconsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        this.model.render(matrixStack, vertexconsumer, i, OverlayTexture.DEFAULT_UV);
        this.model.setAngles(entity, 0.0f, 0.0f, h, 0.0f, 0.0f);
        matrixStack.pop();
    }

    protected void scale(SuperHappyKillBallEntity entity, MatrixStack matrixStack) {
        matrixStack.scale(1F, 1F, 1F);
        matrixStack.translate(0.0F, 0.001F, 0.0F);
        float h = entity.getSize();
        matrixStack.scale(h, h, h);
    }


    public Identifier getTexture(SuperHappyKillBallEntity entity) {
        return TEXTURE;
    }
}

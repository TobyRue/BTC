package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class HuggerEntityRenderer extends MobEntityRenderer<HuggerEntity, HuggerEntityModel<HuggerEntity>> {

    private static final Identifier NORMAL =
            Identifier.of(BTC.MOD_ID, "textures/entity/hugger.png");
    private static final Identifier ANGRY =
            Identifier.of(BTC.MOD_ID, "textures/entity/hugger_angry.png");

    public HuggerEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new HuggerEntityModel<>(ctx.getPart(ModModelLayers.HUGGER)), 0.4f);
    }


    @Override
    public Identifier getTexture(HuggerEntity entity) {
        return (entity.getTarget() != null || entity.isAttacking() || entity.hasVehicle()) ? ANGRY : NORMAL;
    }


    @Override
    public void render(HuggerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
//        matrices.push();
//        matrices.pop();
    }
}
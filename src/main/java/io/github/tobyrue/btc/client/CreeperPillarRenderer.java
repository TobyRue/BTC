package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
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
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperPillarRenderer extends EntityRenderer<CreeperPillarEntity> {

    protected CreeperPillarModel model;

    private static final Identifier PILLAR = Identifier.of(BTC.MOD_ID, "textures/entity/creeper_pillar.png");
    private static final Identifier EXPLOSIVE = Identifier.of(BTC.MOD_ID, "textures/entity/explosive_creeper_pillar.png");

    public CreeperPillarRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new CreeperPillarModel<>(ctx.getPart(ModModelLayers.CREEPER_PILLAR));
    }

    public Identifier getTexture(CreeperPillarEntity entity) {
        CreeperPillarType type = entity.getCreeperPillarType();
        if (type == null) {
            type = CreeperPillarType.NORMAL;
        }

        return switch (type) {
            case EXPLOSIVE -> EXPLOSIVE;
            case NORMAL, RANDOM -> PILLAR;
        };
    }
    @Override
    public void render(CreeperPillarEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        VertexConsumer vertexconsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers,
        this.model.getLayer(getTexture(entity)), false, false);
        this.model.render(matrices, vertexconsumer, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}

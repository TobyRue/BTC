package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;

import net.fabricmc.api.EnvType;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;


@Environment(EnvType.CLIENT)
public class TuffGolemRenderer extends MobEntityRenderer<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> {

    private static final Identifier RED_EYES_OPEN = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_red_open.png");
    private static final Identifier RED_EYES_CLOSED = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_red_closed.png");

    public TuffGolemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new TuffGolemEntityModel<>(ctx.getPart(ModModelLayers.TUFF_GOLEM)), 0.5f);
    }

    @Override
    public Identifier getTexture(TuffGolemEntity entity) {
        if (!entity.isSleeping()) {
            return RED_EYES_OPEN;
        } else {
            return RED_EYES_CLOSED;
        }
    }
}

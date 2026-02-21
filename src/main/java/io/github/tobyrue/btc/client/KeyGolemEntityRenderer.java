package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class KeyGolemEntityRenderer extends MobEntityRenderer<KeyGolemEntity, KeyGolemModel<KeyGolemEntity>> {
    public static final Identifier GOLD = Identifier.of(BTC.MOD_ID, "textures/entity/key_golem_gold.png");
    //TODO Make texture for diamond
    public static final Identifier DIAMOND = Identifier.of(BTC.MOD_ID, "textures/entity/key_golem_diamond.png");

    public KeyGolemEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new KeyGolemModel<>(context.getPart(ModModelLayers.KEY_GOLEM)), 0.5f);
    }

    @Override
    public Identifier getTexture(KeyGolemEntity entity) {
        return GOLD;
    }
}

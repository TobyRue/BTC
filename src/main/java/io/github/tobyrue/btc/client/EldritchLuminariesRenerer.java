package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminariesEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class EldritchLuminariesRenerer extends MobEntityRenderer<EldritchLuminariesEntity, EldritchLuminariesModel<EldritchLuminariesEntity>> {

    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminaries.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminaries_angry.png");

    public EldritchLuminariesRenerer(EntityRendererFactory.Context context) {
        super(context, new EldritchLuminariesModel<>(context.getPart(ModModelLayers.ELDRITCH_LUMINARIES)), 0.5f);
    }

    @Override
    public Identifier getTexture(EldritchLuminariesEntity entity) {
        return TEXTURE;
    }

//    @Override
//    public void render(EldritchLuminariesEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
//        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
//    }
}

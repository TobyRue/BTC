package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.TuffGolemEntityModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

@Environment(value= EnvType.CLIENT)
public class TuffGolemClothOverlay extends FeatureRenderer<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> {

    private static final Identifier CLOTH = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_cloth_overlay.png");
    private static final Identifier DRAGON_EGG = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_dragon_egg_cloth_overlay.png");


    public TuffGolemClothOverlay(FeatureRendererContext<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, TuffGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch)  {
        TuffGolemEntityModel<TuffGolemEntity> model = this.getContextModel();
        if (entity.getHeldItem().getItem() != Items.DRAGON_EGG) {
            VertexConsumer clothVertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(CLOTH));
            model.render(matrixStack, clothVertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f), entity.getColor());
        } else {
            VertexConsumer dragonVertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(DRAGON_EGG));
            model.render(matrixStack, dragonVertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
        }
    }

}

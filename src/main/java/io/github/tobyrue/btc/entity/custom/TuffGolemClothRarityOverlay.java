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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

@Environment(value= EnvType.CLIENT)
public class TuffGolemClothRarityOverlay extends FeatureRenderer<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> {
    private static final Identifier IRON = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_iron_overlay.png");
    private static final Identifier GOLD = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_gold_overlay.png");
    private static final Identifier AMETHYST = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_amethyst_overlay.png");
    private static final Identifier DIAMOND = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_diamond_overlay.png");

    public TuffGolemClothRarityOverlay(FeatureRendererContext<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, TuffGolemEntity entity, float limbAngle, float limbDistance, float tickDelta, float customAngle, float headYaw, float headPitch) {
        TuffGolemEntityModel<TuffGolemEntity> model = this.getContextModel();
        Rarity heldItemRarity = entity.getHeldItem().getRarity();
        if (!entity.getHeldItem().isEmpty()) {
            if (heldItemRarity == Rarity.COMMON) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(IRON));
                model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
            } else if (heldItemRarity == Rarity.UNCOMMON) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(GOLD));
                model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
            } else if (heldItemRarity == Rarity.RARE) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(DIAMOND));
                model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
            } else if (heldItemRarity == Rarity.EPIC) {
                VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(AMETHYST));
                model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
            }
        } else {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(model.getLayer(GOLD));
            model.render(matrixStack, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0f));
        }
    }
}

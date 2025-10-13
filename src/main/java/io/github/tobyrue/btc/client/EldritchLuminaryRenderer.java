package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.custom.LuminaryCapeRenderer;
import io.github.tobyrue.btc.entity.custom.TuffGolemClothOverlay;
import io.github.tobyrue.btc.enums.AttackType;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.mixin.client.rendering.CapeFeatureRendererMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EldritchLuminaryRenderer extends MobEntityRenderer<EldritchLuminaryEntity, EldritchLuminaryModel<EldritchLuminaryEntity>> {

    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary.png");

    public EldritchLuminaryRenderer(EntityRendererFactory.Context context) {
        super(context, new EldritchLuminaryModel<>(context.getPart(ModModelLayers.ELDRITCH_LUMINARY)), 0.5f);
        this.addFeature(new LuminaryCapeRenderer(this));
    }

    @Override
    public Identifier getTexture(EldritchLuminaryEntity luminary) {
        return TEXTURE;
    } 

    @Override
    public void render(EldritchLuminaryEntity livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrices, vertexConsumerProvider, i);
    }
}

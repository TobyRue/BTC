package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.model.SkeletonEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class EldritchLuminariesRenerer extends MobEntityRenderer<EldritchLuminaryEntity, EldritchLuminariesModel<EldritchLuminaryEntity>> {

    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary.png");
    private static final Identifier TEXTURE_ANGRY = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary_angry.png");

    public EldritchLuminariesRenerer(EntityRendererFactory.Context context) {
        super(context, new EldritchLuminariesModel<>(context.getPart(ModModelLayers.ELDRITCH_LUMINARY)), 0.5f);
    }

    @Override
    public Identifier getTexture(EldritchLuminaryEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(EldritchLuminaryEntity livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrices, vertexConsumerProvider, i);
        if (livingEntity.getHeldStaff() != null) {
            renderItem(new ItemStack(livingEntity.getHeldStaff()), ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, matrices, vertexConsumerProvider, livingEntity.getWorld().getLightLevel(livingEntity.getBlockPos()), 0);
        }
    }
    public void renderItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        matrices.translate(0.5, 1.4, 0.2);
        minecraft.getItemRenderer().renderItem(stack, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();
    }
}

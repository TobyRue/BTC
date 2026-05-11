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
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class EldritchLuminaryRenderer extends MobEntityRenderer<EldritchLuminaryEntity, EldritchLuminaryModel<EldritchLuminaryEntity>> {

    private static final Identifier TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary.png");
    private static final Identifier TEXTURE_PYRO = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary_pyromancer.png");
    private static final Identifier TEXTURE_STORM = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary_storm.png");
    private static final Identifier TEXTURE_SHADOW = Identifier.of(BTC.MOD_ID, "textures/entity/eldritch_luminary_shadow.png");

    public EldritchLuminaryRenderer(EntityRendererFactory.Context context) {
        super(context, new EldritchLuminaryModel<>(context.getPart(ModModelLayers.ELDRITCH_LUMINARY)), 0.5f);
        this.addFeature(new LuminaryCapeRenderer(this));
    }

    @Override
    public Identifier getTexture(EldritchLuminaryEntity luminary) {
        return switch (luminary.getArchetype()) {
            case EMPTY, ALL -> TEXTURE;
            case PYROMANCER -> TEXTURE_PYRO;
            case STORM_WARDEN -> TEXTURE_STORM;
            case SHADOW_SUMMONER -> TEXTURE_SHADOW;
        };
    }

    @Override
    public void render(EldritchLuminaryEntity livingEntity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (livingEntity.getIllusionTime() > 0) {
            Vec3d[] vec3ds = livingEntity.getMirrorCopyOffsets(g);
            float h = this.getAnimationProgress(livingEntity, g);

            double baseSpread = 2.5;
            Random random = Random.create(livingEntity.getId());

            if (livingEntity.getIllusionOffsets() == null) {
                Vec3d[] randomOffsets = new Vec3d[vec3ds.length];
                for (int j = 0; j < vec3ds.length; ++j) {
                    double x = (random.nextDouble() - 0.5) * baseSpread * 2;
                    double y = (random.nextDouble() - 0.5) * 0.5;
                    double z = (random.nextDouble() - 0.5) * baseSpread * 2;
                    randomOffsets[j] = new Vec3d(x, y, z);
                }
                livingEntity.setIllusionOffsets(randomOffsets);
            }

            Vec3d[] illusionOffsets = livingEntity.getIllusionOffsets();

            for (int j = 0; j < illusionOffsets.length; ++j) {
                Vec3d off = illusionOffsets[j];

                matrices.push();
                double bobX = MathHelper.cos((float) j + h * 0.5f) * 0.025;
                double bobY = MathHelper.cos((float) j + h * 0.75f) * 0.0125;
                double bobZ = MathHelper.cos((float) j + h * 0.7f) * 0.025;

                matrices.translate(off.x + bobX, off.y + bobY, off.z + bobZ);
                super.render(livingEntity, f, g, matrices, vertexConsumerProvider, i);
                matrices.pop();
            }
        } else {
            super.render(livingEntity, f, g, matrices, vertexConsumerProvider, i);
        }
    }

    @Override
    protected float getShadowRadius(EldritchLuminaryEntity mobEntity) {
        if (mobEntity.getIllusionTime() > 0) {
            return 0;
        }
        return super.getShadowRadius(mobEntity);
    }
}

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CopperGolemRenderer extends MobEntityRenderer<CopperGolemEntity, CopperGolemModel<CopperGolemEntity>> {
    private static final Identifier NORM = Identifier.of(BTC.MOD_ID, "textures/entity/copper_golem.png");
    private static final Identifier EXPOSED = Identifier.of(BTC.MOD_ID, "textures/entity/exposed_copper_golem.png");
    private static final Identifier WEATHERED = Identifier.of(BTC.MOD_ID, "textures/entity/weathered_copper_golem.png");
    private static final Identifier OXIDIZED = Identifier.of(BTC.MOD_ID, "textures/entity/oxidized_copper_golem.png");

    public CopperGolemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new CopperGolemModel<>(ctx.getPart(ModModelLayers.COPPER_GOLEM)), 0.5f);
    }


    @Override
    public Identifier getTexture(CopperGolemEntity entity) {
        if (entity.getOxidation() == CopperGolemEntity.Oxidation.UNOXIDIZED) {
            return NORM;
        } else if (entity.getOxidation() == CopperGolemEntity.Oxidation.EXPOSED) {
            return EXPOSED;
        } else if (entity.getOxidation() == CopperGolemEntity.Oxidation.WEATHERED) {
            return WEATHERED;
        } else {
            return OXIDIZED;
        }
    }
}

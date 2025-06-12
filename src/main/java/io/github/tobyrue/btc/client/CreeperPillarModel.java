package io.github.tobyrue.btc.client;// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreeperPillarModel <T extends CreeperPillarEntity> extends SinglePartEntityModel<T> {
	private final ModelPart pillar;
	public CreeperPillarModel(ModelPart root) {
		this.pillar = root.getChild("pillar");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData pillar = modelPartData.addChild("pillar", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -32.0F, -4.0F, 8.0F, 32.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 48, 48);
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		pillar.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return pillar;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
	}
}
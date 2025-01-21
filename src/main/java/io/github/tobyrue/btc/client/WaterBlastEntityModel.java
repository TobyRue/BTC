package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.animation.ModAnimations;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 4.12.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class WaterBlastEntityModel<T extends WaterBlastEntity> extends SinglePartEntityModel<T> {
	private final ModelPart waterBlast;
	public static final EntityModelLayer WATER_BURST = new EntityModelLayer(Identifier.of(BTC.MOD_ID, "water_burst"), "main");

	public WaterBlastEntityModel(ModelPart root) {
		this.waterBlast = root.getChild("waterBlast");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData waterBlast = modelPartData.addChild("waterBlast", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.0F, -2.0F));

		ModelPartData head = waterBlast.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -18.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

		ModelPartData tail = waterBlast.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData bottom = tail.addChild("bottom", ModelPartBuilder.create().uv(0, 13).cuboid(-3.0F, -22.0F, -1.0F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -2.0F, 3.0F));

		ModelPartData top = tail.addChild("top", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0F, -22.0F, -1.0F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -6.0F, 3.0F));

		ModelPartData right = tail.addChild("right", ModelPartBuilder.create().uv(18, 0).cuboid(1.0F, -22.0F, -1.0F, 0.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -6.0F, 3.0F));

		ModelPartData left = tail.addChild("left", ModelPartBuilder.create().uv(0, 18).cuboid(5.0F, -22.0F, -1.0F, 0.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -6.0F, 3.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		waterBlast.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return waterBlast;
	}

	@Override
	public void setAngles(WaterBlastEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}
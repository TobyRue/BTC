package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.LuminaryAnimations;
import io.github.tobyrue.btc.entity.animation.SuperHappyKillBallAnimation;
import io.github.tobyrue.btc.entity.custom.SuperHappyKillBallEntity;
import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class SuperHappyKillBallEntityModel<T extends SuperHappyKillBallEntity> extends SinglePartEntityModel<T> {
	private final ModelPart SHKB;
	private final ModelPart Core;
	private final ModelPart Layer1;
	private final ModelPart Layer2;
	public SuperHappyKillBallEntityModel(ModelPart root) {
		this.SHKB = root.getChild("SHKB");
		this.Core = this.SHKB.getChild("Core");
		this.Layer1 = this.SHKB.getChild("Layer1");
		this.Layer2 = this.SHKB.getChild("Layer2");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData SHKB = modelPartData.addChild("SHKB", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData Core = SHKB.addChild("Core", ModelPartBuilder.create().uv(48, 32).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Layer1 = SHKB.addChild("Layer1", ModelPartBuilder.create().uv(0, 32).cuboid(-6.0F, -6.0F, -6.0F, 12.0F, 12.0F, 12.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Layer2 = SHKB.addChild("Layer2", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(SuperHappyKillBallEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.updateAnimation(entity.state, SuperHappyKillBallAnimation.ROTATE, animationProgress, 2f);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		SHKB.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return SHKB;
	}
}
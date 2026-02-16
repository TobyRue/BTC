package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.animation.KeyGolemAnimations;
import io.github.tobyrue.btc.entity.animation.LuminaryAnimations;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.entity.custom.KeyGolemEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class KeyGolemModel<T extends KeyGolemEntity> extends SinglePartEntityModel<T> {
	private final ModelPart root;
	private final ModelPart key_golem;
	private final ModelPart body;
	private final ModelPart key;
	private final ModelPart head;
	private final ModelPart main;
	private final ModelPart overlay;
	private final ModelPart eyelids;
	private final ModelPart right_eyelid;
	private final ModelPart left_eyelid;
	private final ModelPart eyes;
	private final ModelPart left_eye;
	private final ModelPart left_white;
	private final ModelPart left_black;
	private final ModelPart right_eye;
	private final ModelPart right_white;
	private final ModelPart right_black;
	private final ModelPart mouth;
	private final ModelPart legs;
	private final ModelPart left;
	private final ModelPart main_left;
	private final ModelPart left_toe;
	private final ModelPart right;
	private final ModelPart main_right;
	private final ModelPart right_toe;

	public KeyGolemModel(ModelPart root) {
		this.root = root;
		this.key_golem = root.getChild("key_golem");
		this.body = this.key_golem.getChild("body");
		this.key = this.body.getChild("key");
		this.head = this.body.getChild("head");
		this.main = this.head.getChild("main");
		this.overlay = this.head.getChild("overlay");
		this.eyelids = this.overlay.getChild("eyelids");
		this.right_eyelid = this.eyelids.getChild("right_eyelid");
		this.left_eyelid = this.eyelids.getChild("left_eyelid");
		this.eyes = this.overlay.getChild("eyes");
		this.left_eye = this.eyes.getChild("left_eye");
		this.left_white = this.left_eye.getChild("left_white");
		this.left_black = this.left_eye.getChild("left_black");
		this.right_eye = this.eyes.getChild("right_eye");
		this.right_white = this.right_eye.getChild("right_white");
		this.right_black = this.right_eye.getChild("right_black");
		this.mouth = this.overlay.getChild("mouth");
		this.legs = this.key_golem.getChild("legs");
		this.left = this.legs.getChild("left");
		this.main_left = this.left.getChild("main_left");
		this.left_toe = this.left.getChild("left_toe");
		this.right = this.legs.getChild("right");
		this.main_right = this.right.getChild("main_right");
		this.right_toe = this.right.getChild("right_toe");
	}

    public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData key_golem = modelPartData.addChild("key_golem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 19.0F, 0.0F));

		ModelPartData body = key_golem.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData key = body.addChild("key", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -19.0F, -1.0F, 2.0F, 11.0F, 2.0F, new Dilation(0.0F))
		.uv(8, 16).cuboid(1.0F, -18.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
		.uv(18, 16).cuboid(1.0F, -15.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData main = head.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData overlay = head.addChild("overlay", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, -4.0F));

		ModelPartData eyelids = overlay.addChild("eyelids", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_eyelid = eyelids.addChild("right_eyelid", ModelPartBuilder.create().uv(20, 30).cuboid(-1.0F, 0.0F, -0.1F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

		ModelPartData left_eyelid = eyelids.addChild("left_eyelid", ModelPartBuilder.create().uv(26, 30).cuboid(-1.0F, 0.0F, -0.1F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 0.0F, 0.0F));

		ModelPartData eyes = overlay.addChild("eyes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.5F, 0.5F));

		ModelPartData left_eye = eyes.addChild("left_eye", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 0.0F, 0.0F));

		ModelPartData left_white = left_eye.addChild("left_white", ModelPartBuilder.create().uv(26, 28).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_black = left_eye.addChild("left_black", ModelPartBuilder.create().uv(27, 26).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-0.5F, 0.0F, 0.0F));

		ModelPartData right_eye = eyes.addChild("right_eye", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

		ModelPartData right_white = right_eye.addChild("right_white", ModelPartBuilder.create().uv(20, 28).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_black = right_eye.addChild("right_black", ModelPartBuilder.create().uv(21, 26).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.5F, 0.0F, 0.0F));

		ModelPartData mouth = overlay.addChild("mouth", ModelPartBuilder.create().uv(14, 30).cuboid(-1.0F, -0.5F, -0.1F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.5F, 0.0F));

		ModelPartData legs = key_golem.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left = legs.addChild("left", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 0.5F, 0.0F));

		ModelPartData main_left = left.addChild("main_left", ModelPartBuilder.create().uv(10, 22).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_toe = left.addChild("left_toe", ModelPartBuilder.create().uv(16, 20).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.5F, 0.0F));

		ModelPartData right = legs.addChild("right", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 0.5F, 0.0F));

		ModelPartData main_right = right.addChild("main_right", ModelPartBuilder.create().uv(8, 22).cuboid(-0.5F, -0.5F, 0.0F, 1.0F, 5.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_toe = right.addChild("right_toe", ModelPartBuilder.create().uv(8, 20).cuboid(-1.0F, 0.0F, -2.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.5F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, headPitch);

		this.updateAnimation(entity.baseAnimationState, KeyGolemAnimations.BASE, animationProgress, 1f);

		this.updateAnimation(entity.idleAnimationState, KeyGolemAnimations.IDLE, animationProgress, 1f);
		this.updateAnimation(entity.fallAsleepAnimationState, KeyGolemAnimations.FALL_ASLEEP, animationProgress, 1f);
		this.updateAnimation(entity.sleepAnimationState, KeyGolemAnimations.SLEEP, animationProgress, 1f);
		this.updateAnimation(entity.wakeUpAnimationState, KeyGolemAnimations.WAKE_UP, animationProgress, 1f);
		this.updateAnimation(entity.disappointedAnimationState, entity.getIsDisappointed() == 1 ?
				KeyGolemAnimations.PLAYER_DIE_1 : entity.getIsDisappointed() == 2 ?
				KeyGolemAnimations.PLAYER_DIE_2 : KeyGolemAnimations.BASE, animationProgress, 1f);
		if (!entity.isWakingUp()) {
			this.animateMovement(entity.isPanicked() ? KeyGolemAnimations.PANIC :  KeyGolemAnimations.RUN, limbAngle, limbDistance, 2f, 2.5f);
		}
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		root.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

}
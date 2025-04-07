// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.CopperGolemAnimations;
import io.github.tobyrue.btc.entity.animation.TuffGolemAnimations;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TuffGolemEntityModel <T extends TuffGolemEntity> extends SinglePartEntityModel<T> {
	private final ModelPart tuff_golem;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart main_head;
	private final ModelPart arms;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart cloth;
	private final ModelPart front;
	private final ModelPart sides;
	private final ModelPart back;
	private final ModelPart body_2;
	private final ModelPart slider;
	private final ModelPart main_body;
	private final ModelPart legs;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public TuffGolemEntityModel(ModelPart root) {
		this.tuff_golem = root.getChild("tuff_golem");
		this.body = tuff_golem.getChild("body");
		this.head = body.getChild("head");
		this.nose = head.getChild("nose");
		this.main_head = head.getChild("main_head");
		this.arms = body.getChild("arms");
		this.left_arm = arms.getChild("left_arm");
		this.right_arm = arms.getChild("right_arm");
		this.cloth = body.getChild("cloth");
		this.front = cloth.getChild("front");
		this.sides = cloth.getChild("sides");
		this.back = cloth.getChild("back");
		this.body_2 = body.getChild("body_2");
		this.slider = body_2.getChild("slider");
		this.main_body = body_2.getChild("main_body");
		this.legs = tuff_golem.getChild("legs");
		this.left_leg = legs.getChild("left_leg");
		this.right_leg = legs.getChild("right_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData tuff_golem = modelPartData.addChild("tuff_golem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 13.0F, 0.0F));

		ModelPartData body = tuff_golem.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData nose = head.addChild("nose", ModelPartBuilder.create().uv(32, 43).cuboid(-1.0F, -1.0F, -2.0044F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, -4.0F));

		ModelPartData main_head = head.addChild("main_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData arms = body.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData left_arm = arms.addChild("left_arm", ModelPartBuilder.create().uv(42, 30).cuboid(0.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 0.0F, 0.0F));

		ModelPartData right_arm = arms.addChild("right_arm", ModelPartBuilder.create().uv(32, 30).cuboid(-2.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 0.0F, 0.0F));

		ModelPartData cloth = body.addChild("cloth", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 3.0F, 0.0F));

		ModelPartData front = cloth.addChild("front", ModelPartBuilder.create().uv(32, 18).cuboid(-4.0F, 1.0F, -4.0F, 8.0F, 6.0F, 0.0F, new Dilation(0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData sides = cloth.addChild("sides", ModelPartBuilder.create().uv(0, 29).cuboid(4.0F, 1.0F, -4.0F, 0.0F, 6.0F, 8.0F, new Dilation(-0.02F))
				.uv(16, 29).cuboid(-4.0F, 1.0F, -4.0F, 0.0F, 6.0F, 8.0F, new Dilation(0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData back = cloth.addChild("back", ModelPartBuilder.create().uv(32, 24).cuboid(-4.0F, 1.0F, 4.0F, 8.0F, 6.0F, 0.0F, new Dilation(-0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData body_2 = body.addChild("body_2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData slider = body_2.addChild("slider", ModelPartBuilder.create().uv(34, 43).cuboid(-4.0F, -0.91F, -3.1F, 8.0F, 2.0F, 6.0F, new Dilation(-0.01F))
				.uv(34, 51).cuboid(-4.0F, -0.91F, -3.1F, 8.0F, 3.0F, 0.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 2.8F, -1.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData main_body = body_2.addChild("main_body", ModelPartBuilder.create().uv(0, 15).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData legs = tuff_golem.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(32, 9).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 6.0F, 0.0F));

		ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 6.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}


	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		tuff_golem.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return tuff_golem;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(headYaw, headPitch);

		this.updateAnimation(entity.idleAnimationState, TuffGolemAnimations.TUFF_IDLE, animationProgress, 1f);

		this.updateAnimation(entity.sleepAnimationState, TuffGolemAnimations.TUFF_SLEEP, animationProgress, 1f);

		this.updateAnimation(entity.wakeAnimationState, TuffGolemAnimations.TUFF_WAKE, animationProgress, 1f);

		this.updateAnimation(entity.pickUpItemAnimationState, TuffGolemAnimations.TUFF_PICK_UP_ITEM, animationProgress, 1f);
		this.updateAnimation(entity.dropItemAnimationState, TuffGolemAnimations.TUFF_DROP_ITEM, animationProgress, 1f);


		if (entity.getHeldItem() == ItemStack.EMPTY) {
			this.animateMovement(TuffGolemAnimations.TUFF_WALK_WITHOUT_ITEM, limbAngle, limbDistance, 2f, 2.5f);
		} else {
			this.animateMovement(TuffGolemAnimations.TUFF_WALK_WITH_ITEM, limbAngle, limbDistance, 2f, 2.5f);
		}
	}
	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -20.0f, 30.0f);

								this.body.yaw = headYaw * 0.017453292F;
//		this.body.pitch = headPitch * 0.017453292F;
	}
}
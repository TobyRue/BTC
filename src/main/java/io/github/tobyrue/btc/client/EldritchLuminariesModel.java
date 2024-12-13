// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.ModAnimations;
import io.github.tobyrue.btc.entity.custom.EldritchLuminariesEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class EldritchLuminariesModel<T extends EldritchLuminariesEntity> extends SinglePartEntityModel<T> {
	private final ModelPart eldritch_luminaries;
	private final ModelPart head;
	private final ModelPart fullbody;
	private final ModelPart arms;
	private final ModelPart legs;


	public EldritchLuminariesModel(ModelPart root) {
		this.eldritch_luminaries = root.getChild("eldritch_luminaries");
		this.head = eldritch_luminaries.getChild("head");
		this.fullbody = eldritch_luminaries.getChild("fullbody");
		this.arms = eldritch_luminaries.getChild("arms");
		this.legs = eldritch_luminaries.getChild("legs");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData eldritch_luminaries = modelPartData.addChild("eldritch_luminaries", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData head = eldritch_luminaries.addChild("head", ModelPartBuilder.create().uv(24, 50).cuboid(-1.0F, -3.0F, -6.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 18).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData fullbody = eldritch_luminaries.addChild("fullbody", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 12.0F, 0.0F));

		ModelPartData body = fullbody.addChild("body", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -12.0F, -3.0F, 10.0F, 12.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cloak = fullbody.addChild("cloak", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData cloak1 = cloak.addChild("cloak1", ModelPartBuilder.create().uv(12, 50).cuboid(5.0F, 0.0F, -3.0F, 0.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData cloak2 = cloak.addChild("cloak2", ModelPartBuilder.create().uv(48, 30).cuboid(-5.0F, 0.0F, -3.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData cloak3 = cloak.addChild("cloak3", ModelPartBuilder.create().uv(0, 48).cuboid(-5.0F, 0.0F, -3.0F, 0.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData cloak4 = cloak.addChild("cloak4", ModelPartBuilder.create().uv(48, 34).cuboid(-5.0F, 0.0F, 3.0F, 10.0F, 4.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -8.0F, 0.0F));

		ModelPartData arms = eldritch_luminaries.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 3.0F, 1.0F));

		ModelPartData sidearmsfull2 = arms.addChild("sidearmsfull2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 17.0F, -1.0F));

		ModelPartData sidearmsfull1 = sidearmsfull2.addChild("sidearmsfull1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sidearmsfull = sidearmsfull1.addChild("sidearmsfull", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -17.0F, 1.0F));

		ModelPartData armfullside1 = sidearmsfull.addChild("armfullside1", ModelPartBuilder.create().uv(0, 61).cuboid(-4.0F, -2.0F, -13.0F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(-5.0F, 0.0F, 0.0F));

		ModelPartData armfullside2 = sidearmsfull.addChild("armfullside2", ModelPartBuilder.create().uv(40, 61).cuboid(-0.1206F, -2.0F, -13.316F, 4.0F, 4.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, 0.0F, 0.0F));

		ModelPartData armscrossed = arms.addChild("armscrossed", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData arm1 = armscrossed.addChild("arm1", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, -1.0F, -1.0F));

		ModelPartData sidearm1 = arm1.addChild("sidearm1", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 18.0F, 0.0F));

		ModelPartData arm1_r1 = sidearm1.addChild("arm1_r1", ModelPartBuilder.create().uv(32, 15).cuboid(-1.0F, -4.0F, -9.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -16.0F, 3.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData middlearm1 = arm1.addChild("middlearm1", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, 18.0F, 0.0F));

		ModelPartData armmiddle1_r1 = middlearm1.addChild("armmiddle1_r1", ModelPartBuilder.create().uv(16, 42).cuboid(-5.0F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -10.9548F, -1.9453F, 0.7854F, 0.0F, 0.0F));

		ModelPartData arm2 = armscrossed.addChild("arm2", ModelPartBuilder.create(), ModelTransform.pivot(7.0F, -1.0F, -1.0F));

		ModelPartData sidearm2 = arm2.addChild("sidearm2", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, 18.0F, 0.0F));

		ModelPartData arm2_r1 = sidearm2.addChild("arm2_r1", ModelPartBuilder.create().uv(32, 0).cuboid(-1.0F, -4.0F, -9.0F, 4.0F, 4.0F, 11.0F, new Dilation(0.0F)), ModelTransform.of(6.0F, -16.0F, 3.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData middlearm2 = arm2.addChild("middlearm2", ModelPartBuilder.create(), ModelTransform.pivot(-7.0F, 18.0F, 0.0F));

		ModelPartData armmiddle2_r1 = middlearm2.addChild("armmiddle2_r1", ModelPartBuilder.create().uv(34, 42).cuboid(-5.0F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(5.0F, -10.9639F, -1.9453F, 0.7854F, 0.0F, 0.0F));

		ModelPartData legs = eldritch_luminaries.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 20.0F, 1.0F));

		ModelPartData leg1 = legs.addChild("leg1", ModelPartBuilder.create().uv(0, 36).cuboid(-2.0F, -1.0F, -3.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, -7.0F, 0.0F));

		ModelPartData leg2 = legs.addChild("leg2", ModelPartBuilder.create().uv(32, 30).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(3.0F, -8.0F, -1.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}
	@Override
	public void setAngles(EldritchLuminariesEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, headPitch);

		this.animateMovement(ModAnimations.ELDRITCH_LUMINARIES_SPRINT, limbSwing, limbSwingAmount, 2f, 2.5f);
		this.updateAnimation(entity.idleAnimationState, ModAnimations.ELDRITCH_LUMINARIES_IDLE, ageInTicks, 1f);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		eldritch_luminaries.render(matrices, vertexConsumer, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return eldritch_luminaries;
	}
}
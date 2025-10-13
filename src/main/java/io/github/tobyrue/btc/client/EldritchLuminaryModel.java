// Made with Blockbench 4.11.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.LuminaryAnimations;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.WanderingTraderEntityRenderer;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EldritchLuminaryModel<T extends EldritchLuminaryEntity> extends SinglePartEntityModel<T> {

	private final ModelPart root;
	private final ModelPart eldritch_luminary;
	private final ModelPart head;
	private final ModelPart main_head;
	private final ModelPart cloak;
	private final ModelPart whole_body;
	private final ModelPart arms;
	private final ModelPart right_arm;
	private final ModelPart main_right_arm;
	private final ModelPart cloak_right_arm;
	private final ModelPart left_arm;
	private final ModelPart main_left_arm;
	private final ModelPart cloak_left_arm;
	private final ModelPart body;
	private final ModelPart main_body;
	private final ModelPart body_cloak;
	private final ModelPart legs;
	private final ModelPart right_leg;
	private final ModelPart main_right_leg;
	private final ModelPart right_leg_cloak;
	private final ModelPart top2;
	private final ModelPart bottom2;
	private final ModelPart left_leg;
	private final ModelPart main_left_leg;
	private final ModelPart left_leg_cloak;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart cape;

	public EldritchLuminaryModel(ModelPart root) {
		this.root = root;
        this.eldritch_luminary = root.getChild("eldritch_luminary");
		this.head = this.eldritch_luminary.getChild("head");
		this.main_head = this.head.getChild("main_head");
		this.cloak = this.head.getChild("cloak");
		this.whole_body = this.eldritch_luminary.getChild("whole_body");
		this.arms = this.whole_body.getChild("arms");
		this.right_arm = this.arms.getChild("right_arm");
		this.main_right_arm = this.right_arm.getChild("main_right_arm");
		this.cloak_right_arm = this.right_arm.getChild("cloak_right_arm");
		this.left_arm = this.arms.getChild("left_arm");
		this.main_left_arm = this.left_arm.getChild("main_left_arm");
		this.cloak_left_arm = this.left_arm.getChild("cloak_left_arm");
		this.body = this.whole_body.getChild("body");
		this.main_body = this.body.getChild("main_body");
		this.body_cloak = this.body.getChild("body_cloak");
		this.legs = this.eldritch_luminary.getChild("legs");
		this.right_leg = this.legs.getChild("right_leg");
		this.main_right_leg = this.right_leg.getChild("main_right_leg");
		this.right_leg_cloak = this.right_leg.getChild("right_leg_cloak");
		this.top2 = this.right_leg_cloak.getChild("top2");
		this.bottom2 = this.right_leg_cloak.getChild("bottom2");
		this.left_leg = this.legs.getChild("left_leg");
		this.main_left_leg = this.left_leg.getChild("main_left_leg");
		this.left_leg_cloak = this.left_leg.getChild("left_leg_cloak");
		this.top = this.left_leg_cloak.getChild("top");
		this.bottom = this.left_leg_cloak.getChild("bottom");
		this.cape = root.getChild("cape");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData eldritch_luminary = modelPartData.addChild("eldritch_luminary", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData head = eldritch_luminary.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -23.0F, 0.0F));

		ModelPartData main_head = head.addChild("main_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -29.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 20.0F, 0.0F));

		ModelPartData cloak = head.addChild("cloak", ModelPartBuilder.create().uv(0, 16).cuboid(-4.0F, -29.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.4F)), ModelTransform.pivot(0.0F, 20.0F, 0.0F));

		ModelPartData whole_body = eldritch_luminary.addChild("whole_body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -2.0F, 0.0F));

		ModelPartData arms = whole_body.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -19.9F, 0.0F));

		ModelPartData right_arm = arms.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 0.0F, 0.0F));

		ModelPartData main_right_arm = right_arm.addChild("main_right_arm", ModelPartBuilder.create().uv(0, 48).cuboid(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -0.2F, 0.0F));

		ModelPartData cloak_right_arm = right_arm.addChild("cloak_right_arm", ModelPartBuilder.create().uv(16, 48).cuboid(4.5F, -19.2F, -1.1F, 4.0F, 12.0F, 4.0F, new Dilation(0.4F)), ModelTransform.pivot(-2.5F, 17.0F, -0.9F));

		ModelPartData left_arm = arms.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 0.0F, 0.0F));

		ModelPartData main_left_arm = left_arm.addChild("main_left_arm", ModelPartBuilder.create().uv(40, 32).cuboid(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -0.2F, 0.0F));

		ModelPartData cloak_left_arm = left_arm.addChild("cloak_left_arm", ModelPartBuilder.create().uv(48, 16).cuboid(-4.0F, -2.1F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.4F)), ModelTransform.pivot(-2.0F, -0.1F, 0.0F));

		ModelPartData body = whole_body.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.0F, 0.0F));

		ModelPartData main_body = body.addChild("main_body", ModelPartBuilder.create().uv(0, 32).cuboid(-4.0F, -22.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, 0.0F));

		ModelPartData body_cloak = body.addChild("body_cloak", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -12.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.3F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData legs = eldritch_luminary.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, -11.0F, 0.0F));

		ModelPartData main_right_leg = right_leg.addChild("main_right_leg", ModelPartBuilder.create().uv(32, 16).cuboid(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 11.0F, 0.0F));

		ModelPartData right_leg_cloak = right_leg.addChild("right_leg_cloak", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, 11.0F, 0.0F));

		ModelPartData top2 = right_leg_cloak.addChild("top2", ModelPartBuilder.create().uv(48, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.8F, 4.0F, new Dilation(0.2F)), ModelTransform.pivot(2.0F, -12.0F, 0.0F));

		ModelPartData bottom2 = right_leg_cloak.addChild("bottom2", ModelPartBuilder.create().uv(56, 0).cuboid(-2.0F, 0.2F, -2.0F, 4.0F, 4.8F, 4.0F, new Dilation(0.2F)), ModelTransform.pivot(2.0F, -5.0F, 0.0F));

		ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create(), ModelTransform.pivot(-2.0F, -11.0F, 0.0F));

		ModelPartData main_left_leg = left_leg.addChild("main_left_leg", ModelPartBuilder.create().uv(24, 32).cuboid(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 11.0F, 0.0F));

		ModelPartData left_leg_cloak = left_leg.addChild("left_leg_cloak", ModelPartBuilder.create(), ModelTransform.pivot(2.0F, 11.0F, 0.0F));

		ModelPartData top = left_leg_cloak.addChild("top", ModelPartBuilder.create().uv(32, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 6.8F, 4.0F, new Dilation(0.2F)), ModelTransform.pivot(-2.0F, -12.0F, 0.0F));

		ModelPartData bottom = left_leg_cloak.addChild("bottom", ModelPartBuilder.create().uv(56, 32).cuboid(-2.0F, 0.2F, -2.0F, 4.0F, 4.8F, 4.0F, new Dilation(0.2F)), ModelTransform.pivot(-2.0F, -5.0F, 0.0F));

		ModelPartData cape = modelPartData.addChild("cape", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 2.0F));

		ModelPartData cube_r1 = cape.addChild("cube_r1", ModelPartBuilder.create().uv(0, 64).cuboid(-5.0F, 0.0F, 0.0F, 10.0F, 16.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void setAngles(EldritchLuminaryEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(netHeadYaw, headPitch);
		this.updateAnimation(entity.attackAnimationState, LuminaryAnimations.LUMINARY_CAST, ageInTicks, 1f);

		this.updateAnimation(entity.idleAnimationState, LuminaryAnimations.LUMINARY_IDLE, ageInTicks, 1f);

		if (entity.attackAnimationState.isRunning()) {
			this.animateMovement(LuminaryAnimations.LUMINARY_WALK_NO_ARMS, limbSwing, limbSwingAmount, 2f, 2.5f);
		} else {
			this.animateMovement(LuminaryAnimations.LUMINARY_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
		}
//		if (entity.attackAnimationState.isRunning()) {
//			this.armfullside1.visible = true;
//			this.armfullside2.visible = true;
//			this.armscrossed.visible = false;
//		} else {
//			this.armfullside1.visible = false;
//			this.armfullside2.visible = false;
//			this.armscrossed.visible = true;
//		}
//
//		this.updateAnimation(entity.attackAnimationState, LuminaryAnimations.ELDRITCH_LUMINARY_CAST, ageInTicks, 1f);
//
//		this.animateMovement(LuminaryAnimations.ELDRITCH_LUMINARY_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
//		this.updateAnimation(entity.idleAnimationState, LuminaryAnimations.ELDRITCH_LUMINARY_IDLE, ageInTicks, 1f);
	}

	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		eldritch_luminary.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

//	@Override
//	public ModelPart getPart() {
//		return eldritch_luminary;
//	}

	public void renderCape(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
		this.cape.render(matrices, vertices, light, overlay);
	}

	public ModelPart getEldritch_luminary() {
		return eldritch_luminary;
	}

	public ModelPart getBody() {
		return body;
	}

	public ModelPart getArms() {
		return arms;
	}

	public ModelPart getCape() {
		return cape;
	}
}
// Made with Blockbench 4.12.3
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.CopperGolemAnimations;
import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CopperGolemModel <T extends CopperGolemEntity> extends SinglePartEntityModel<T> {
	private final ModelPart copper_golem;
	private final ModelPart head;
	private final ModelPart rod;
	private final ModelPart top;
	private final ModelPart bottom;
	private final ModelPart head2;
	private final ModelPart nose;
	private final ModelPart main;
	private final ModelPart body;
	private final ModelPart arms;
	private final ModelPart rightarm;
	private final ModelPart leftarm;
	private final ModelPart mainbody;
	private final ModelPart legs;
	private final ModelPart leftleg;
	private final ModelPart rightleg;
	public CopperGolemModel(ModelPart root) {
		this.copper_golem = root.getChild("copper_golem");
		this.head = copper_golem.getChild("head");
		this.rod = head.getChild("rod");
		this.top = rod.getChild("top");
		this.bottom = rod.getChild("bottom");
		this.head2 = head.getChild("head2");
		this.nose = head2.getChild("nose");
		this.main = head2.getChild("main");
		this.body = copper_golem.getChild("body");
		this.arms = body.getChild("arms");
		this.rightarm = arms.getChild("rightarm");
		this.leftarm = arms.getChild("leftarm");
		this.mainbody = body.getChild("mainbody");
		this.legs = copper_golem.getChild("legs");
		this.leftleg = legs.getChild("leftleg");
		this.rightleg = legs.getChild("rightleg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData copper_golem = modelPartData.addChild("copper_golem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData head = copper_golem.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -10.0F, -0.5F));

		ModelPartData rod = head.addChild("rod", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 10.0F, 0.0F));

		ModelPartData top = rod.addChild("top", ModelPartBuilder.create().uv(24, 26).cuboid(-2.0F, -22.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData bottom = rod.addChild("bottom", ModelPartBuilder.create().uv(0, 35).cuboid(-1.0F, -18.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head2 = head.addChild("head2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 10.0F, 0.5F));

		ModelPartData nose = head2.addChild("nose", ModelPartBuilder.create().uv(32, 8).cuboid(-1.0F, -12.0F, -6.5F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData main = head2.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -15.0F, -4.5F, 8.0F, 5.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData body = copper_golem.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -7.0F, -0.5F));

		ModelPartData arms = body.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 7.0F, 0.5F));

		ModelPartData rightarm = arms.addChild("rightarm", ModelPartBuilder.create(), ModelTransform.pivot(-4.0F, -9.0F, -0.5F));

		ModelPartData cube_r1 = rightarm.addChild("cube_r1", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 3.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -1.0F, 0.5F, 1.5708F, 0.0F, 0.0F));

		ModelPartData leftarm = arms.addChild("leftarm", ModelPartBuilder.create(), ModelTransform.pivot(4.0F, -9.0F, -0.5F));

		ModelPartData cube_r2 = leftarm.addChild("cube_r2", ModelPartBuilder.create().uv(24, 13).cuboid(-1.0F, -2.0F, -9.0F, 2.0F, 3.0F, 10.0F, new Dilation(0.0F)), ModelTransform.of(1.0F, -1.0F, 0.5F, 1.5708F, 0.0F, 0.0F));

		ModelPartData mainbody = body.addChild("mainbody", ModelPartBuilder.create().uv(0, 26).cuboid(-4.0F, -10.0F, -2.5F, 8.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.0F, 0.5F));

		ModelPartData legs = copper_golem.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leftleg = legs.addChild("leftleg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, -4.0F, 0.0F));

		ModelPartData rightleg = legs.addChild("rightleg", ModelPartBuilder.create().uv(24, 34).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 5.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -4.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		copper_golem.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return copper_golem;
	}
	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -25.0f, 45.0f);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(headYaw, headPitch);
		this.updateAnimation(entity.wakeUpAnimationState, CopperGolemAnimations.COPPER_WAKE_UP, animationProgress, 1f);

		this.updateAnimation(entity.idleAnimationState, CopperGolemAnimations.COPPER_IDLE, animationProgress, 1f);

		this.updateAnimation(entity.buttonPressFrontAnimationState, CopperGolemAnimations.COPPER_PRESS_BUTTON_DOWN, animationProgress, 1f);
		this.updateAnimation(entity.buttonPressUpAnimationState, CopperGolemAnimations.COPPER_PRESS_BUTTON_UP, animationProgress, 1f);
		this.updateAnimation(entity.buttonPressDownAnimationState, CopperGolemAnimations.COPPER_BUTTON_FAR_DOWN, animationProgress, 1f);

		this.animateMovement(CopperGolemAnimations.COPPER_WALK, limbAngle, limbDistance, 2f, 2.5f);
	}
}
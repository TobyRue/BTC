package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class HuggerEntityModel<T extends HuggerEntity> extends SinglePartEntityModel<T> {
	private final ModelPart hugger;
	private final ModelPart head;
	private final ModelPart legs;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;
	private final ModelPart leg5;
	private final ModelPart leg6;
	private final ModelPart leg7;
	private final ModelPart leg8;
	public HuggerEntityModel(ModelPart root) {
		this.hugger = root.getChild("hugger");
		this.head = this.hugger.getChild("head");
		this.legs = this.hugger.getChild("legs");
		this.leg1 = this.legs.getChild("leg1");
		this.leg2 = this.legs.getChild("leg2");
		this.leg3 = this.legs.getChild("leg3");
		this.leg4 = this.legs.getChild("leg4");
		this.leg5 = this.legs.getChild("leg5");
		this.leg6 = this.legs.getChild("leg6");
		this.leg7 = this.legs.getChild("leg7");
		this.leg8 = this.legs.getChild("leg8");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData hugger = modelPartData.addChild("hugger", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
		ModelPartData head = hugger.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
				.uv(0, 23).cuboid(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F))
				.uv(15, 14).cuboid(4.0F, -10.0F, 3.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F))
				.uv(15, 14).cuboid(-4.0F, -10.0F, 3.0F, 0.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -0.5F, 0.0F));

		ModelPartData legs = hugger.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData leg1 = legs.addChild("leg1", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r1 = leg1.addChild("Leg_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 3.0F, -0.2618F, 0.0F, 0.0F));

		ModelPartData leg2 = legs.addChild("leg2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r2 = leg2.addChild("Leg_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, -1.0F, 2.5F, -0.2618F, 0.7854F, 0.0F));

		ModelPartData leg3 = legs.addChild("leg3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r3 = leg3.addChild("Leg_r3", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -1.0F, 2.5F, -0.2618F, -0.7854F, 0.0F));

		ModelPartData leg4 = legs.addChild("leg4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r4 = leg4.addChild("Leg_r4", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -3.0F, 0.2618F, 0.0F, 0.0F));

		ModelPartData leg5 = legs.addChild("leg5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r5 = leg5.addChild("Leg_r5", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -1.0F, 0.0F, 0.0F, -1.5708F, -0.2618F));

		ModelPartData leg6 = legs.addChild("leg6", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r6 = leg6.addChild("Leg_r6", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -1.0F, -2.5F, 0.2618F, 0.7854F, 0.0F));

		ModelPartData leg7 = legs.addChild("leg7", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r7 = leg7.addChild("Leg_r7", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(3.0F, -1.0F, 0.0F, 0.0F, -1.5708F, 0.2618F));

		ModelPartData leg8 = legs.addChild("leg8", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r8 = leg8.addChild("Leg_r8", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(2.6F, -1.0F, -2.5F, 0.2618F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 32, 32);
	}
	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.getPart().traverse().forEach(ModelPart::resetTransform);
		this.setHeadAngles(headYaw, headPitch);
//yaw = y
//pitch = x
//roll = z

//		float k = (float) (Math.PI / 4);
//		this.leg1.roll = (float) (-Math.PI / 4); //1
//		this.leg2.roll = (float) (Math.PI / 4); //2
//		this.leg3.roll = -0.58119464F; //3
//		this.leg4.roll = 0.58119464F; //4
//		this.leg5.roll = -0.58119464F; //5
//		this.leg6.roll = 0.58119464F; //6
//		this.leg7.roll = (float) (-Math.PI / 4); //7
//		this.leg8.roll = (float) (Math.PI / 4); //8
//		float l = -0.0F;
//		float m = (float) (Math.PI / 8);
//		this.leg1.yaw = (float) (Math.PI / 4);
//		this.leg2.yaw = (float) (-Math.PI / 4);
//		this.leg3.yaw = (float) (Math.PI / 8);
//		this.leg4.yaw = (float) (-Math.PI / 8);
//		this.leg5.yaw = (float) (-Math.PI / 8);
//		this.leg6.yaw = (float) (Math.PI / 8);
//		this.leg7.yaw = (float) (-Math.PI / 4);
//		this.leg8.yaw = (float) (Math.PI / 4);
		float n = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance /2;
		float o = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance/2;
		float p = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance/2;
		float q = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance/2;
		float r = (float) Math.abs(Math.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance/2;
		float s = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance/2;
		float t = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance/2;
		float u = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance/2;
		this.leg1.yaw += n;
		this.leg2.yaw += -n;
		this.leg3.yaw += o;
		this.leg4.yaw += -o;
		this.leg5.yaw += p;
		this.leg6.yaw += -p;
		this.leg7.yaw += q;
		this.leg8.yaw += -q;
		this.leg1.roll += r;
		this.leg2.roll += -r;
		this.leg3.roll += s;
		this.leg4.roll += -s;
		this.leg5.roll += t;
		this.leg6.roll += -t;
		this.leg7.roll += u;
		this.leg8.roll += -u;
	}
	private void setHeadAngles(float headYaw, float headPitch) {
		headYaw = MathHelper.clamp(headYaw, -30.0f, 30.0f);
		headPitch = MathHelper.clamp(headPitch, -10.0f, 10.0f);

		this.head.yaw = headYaw * 0.017453292F;
		this.head.pitch = headPitch * 0.017453292F;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		hugger.render(matrices, vertices, light, overlay);
	}

	@Override
	public ModelPart getPart() {
		return hugger;
	}
}
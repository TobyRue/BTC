package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.WeakHashMap;

// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17+ for Yarn
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

	private static class AnimationState {
		float smoothYaw = 0.0F;
		float smoothPitch = 0.0F;
		float smoothWindUp = 0.0F;
	}
	private final WeakHashMap<T, AnimationState> entityStates = new WeakHashMap<>();
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

		ModelPartData Leg_r1 = leg1.addChild("Leg_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, 3.0F, -2.8798F, 0.0F, -3.1416F));

		ModelPartData leg2 = legs.addChild("leg2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r2 = leg2.addChild("Leg_r2", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, -1.0F, 2.5F, -2.8798F, -0.7854F, -3.1416F));

		ModelPartData leg3 = legs.addChild("leg3", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r3 = leg3.addChild("Leg_r3", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-2.5F, -1.0F, 2.5F, -2.8798F, 0.7854F, 3.1416F));

		ModelPartData leg4 = legs.addChild("leg4", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r4 = leg4.addChild("Leg_r4", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -1.0F, -3.0F, 0.2618F, 0.0F, 0.0F));

		ModelPartData leg5 = legs.addChild("leg5", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData Leg_r5 = leg5.addChild("Leg_r5", ModelPartBuilder.create().uv(0, 16).cuboid(-1.0F, -2.0F, -5.0F, 2.0F, 2.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(-3.0F, -1.0F, 0.0F, 0.0F, 1.5708F, -0.2618F));

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

		AnimationState state = this.entityStates.computeIfAbsent(entity, e -> new AnimationState());

		float targetWindUp = entity.isWindingUp() ? 1.0F : 0.0F;
		state.smoothWindUp = MathHelper.lerp(0.25F, state.smoothWindUp, targetWindUp);

		if (state.smoothWindUp > 0.01F) {
			float squeezeAmount = (float) ((Math.PI)/4 * state.smoothWindUp);
			float legRise = 5.0F * state.smoothWindUp;
			float bodyRise = 2.0F * state.smoothWindUp;

			this.leg1.pitch -= squeezeAmount;
			this.leg2.pitch -= squeezeAmount;
			this.leg3.pitch -= squeezeAmount;
			this.leg4.pitch += squeezeAmount;
			this.leg6.pitch += squeezeAmount;
			this.leg8.pitch += squeezeAmount;

			this.leg5.roll -= squeezeAmount;
			this.leg7.roll += squeezeAmount;

			this.leg1.pivotY -= legRise;
			this.leg2.pivotY -= legRise;
			this.leg3.pivotY -= legRise;
			this.leg4.pivotY -= legRise;
			this.leg6.pivotY -= legRise;
			this.leg8.pivotY -= legRise;
			this.leg5.pivotY -= legRise;
			this.leg7.pivotY -= legRise;
			this.head.pivotY -= bodyRise;
		}
		else if (!entity.isOnGround() && (entity.getVelocity().y > 0.02D || entity.isAttacking())) {
			Vec3d velocity = entity.getVelocity();
			if (velocity.lengthSquared() > 0.005) {
				float travelPitch = (float) MathHelper.atan2(velocity.y, velocity.horizontalLength());

				float targetPitch = (float) (travelPitch - Math.PI/2);

				state.smoothPitch = MathHelper.lerp(0.2F, state.smoothPitch, targetPitch);
			}
			this.hugger.pitch = state.smoothPitch;
		}
		else {
			state.smoothYaw = MathHelper.lerp(0.2F, state.smoothYaw, 0.0F);
			state.smoothPitch = MathHelper.lerp(0.2F, state.smoothPitch, 0.0F);
			this.hugger.yaw = state.smoothYaw;
			this.hugger.pitch = state.smoothPitch;

			float n = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + 0.0F) * 0.4F) * limbDistance / 2;
			float o = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbDistance / 2;
			float p = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI / 2)) * 0.4F) * limbDistance / 2;
			float q = (float) -(Math.cos(limbAngle * 0.6662F * 2.0F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance / 2;
			float r = (float) Math.abs(Math.sin(limbAngle * 0.6662F + 0.0F) * 0.4F) * limbDistance / 2;
			float s = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) Math.PI) * 0.4F) * limbDistance / 2;
			float t = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) (Math.PI / 2)) * 0.4F) * limbDistance / 2;
			float u = (float) Math.abs(Math.sin(limbAngle * 0.6662F + (float) (Math.PI * 3.0 / 2.0)) * 0.4F) * limbDistance / 2;

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
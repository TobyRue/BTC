// Made with Blockbench 5.1.0
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.custom.TrialCubeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class TrialCubeEntityModel<T extends TrialCubeEntity> extends SinglePartEntityModel<T> {
	private final ModelPart trial_cube;
	private final ModelPart norm_light;
	private final ModelPart in;
	private final ModelPart out;
	public TrialCubeEntityModel(ModelPart root) {
		this.trial_cube = root.getChild("trial_cube");
		this.norm_light = trial_cube.getChild("norm_light");
		this.in = norm_light.getChild("in");
		this.out = norm_light.getChild("out");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData trial_cube = modelPartData.addChild("trial_cube", ModelPartBuilder.create(), ModelTransform.of(0.0F, 24.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		ModelPartData norm_light = trial_cube.addChild("norm_light", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData n_in = norm_light.addChild("in", ModelPartBuilder.create().uv(0, 32).cuboid(-8.0F, 8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData n_out = norm_light.addChild("out", ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, 8.0F, -8.0F, 16.0F, 16.0F, 16.0F, new Dilation(0.5F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return trial_cube;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		trial_cube.render(matrices, vertices, light, overlay, color);
	}
}
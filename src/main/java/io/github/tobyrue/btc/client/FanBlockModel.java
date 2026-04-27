package io.github.tobyrue.btc.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class FanBlockModel extends Model {
	private final ModelPart fan_blades;

	public FanBlockModel(ModelPart root) {
		super(RenderLayer::getEntityCutout);
		this.fan_blades = root.getChild("fan_blades");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("fan_blades",
				ModelPartBuilder.create().uv(0, 0)
						.cuboid(-8.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, new Dilation(0.0F)),
				ModelTransform.pivot(8.0F, 8.0F, 8.0F));
		return TexturedModelData.of(modelData, 32, 16);
	}

	public void setRotation(float roll) {
		this.fan_blades.roll = roll;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		fan_blades.render(matrices, vertices, light, overlay, color);
	}
}
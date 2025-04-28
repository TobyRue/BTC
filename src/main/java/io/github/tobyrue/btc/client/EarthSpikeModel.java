// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.animation.EarthSpikeAnimation;
import io.github.tobyrue.btc.entity.animation.TuffGolemAnimations;
import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;

public class EarthSpikeModel<T extends EarthSpikeEntity> extends SinglePartEntityModel<T> {
	private final ModelPart earth_spike;
	private final ModelPart top;
	private final ModelPart mid_top;
	private final ModelPart mid;
	private final ModelPart mid_bottom;
	private final ModelPart bottom;

	public EarthSpikeModel(ModelPart root) {
		this.earth_spike = root.getChild("earth_spike");
		this.top = earth_spike.getChild("top");
		this.mid_top = earth_spike.getChild("mid_top");
		this.mid = earth_spike.getChild("mid");
		this.mid_bottom = earth_spike.getChild("mid_bottom");
		this.bottom = earth_spike.getChild("bottom");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData earth_spike = modelPartData.addChild("earth_spike", ModelPartBuilder.create(), ModelTransform.of(0.0F, 9.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		ModelPartData top = earth_spike.addChild("top", ModelPartBuilder.create().uv(32, 13).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -6.0F, 0.0F));

		ModelPartData mid_top = earth_spike.addChild("mid_top", ModelPartBuilder.create().uv(24, 25).cuboid(-2.0F, -12.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

		ModelPartData mid = earth_spike.addChild("mid", ModelPartBuilder.create().uv(0, 25).cuboid(-3.0F, -8.0F, -3.0F, 6.0F, 4.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

		ModelPartData mid_bottom = earth_spike.addChild("mid_bottom", ModelPartBuilder.create().uv(0, 13).cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

		ModelPartData bottom = earth_spike.addChild("bottom", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, 2.0F, -5.0F, 10.0F, 3.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}


	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		earth_spike.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return earth_spike;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}
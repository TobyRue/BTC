// Made with Blockbench 4.12.5
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.AdvancementUtils;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import io.github.tobyrue.btc.enums.SpellRegistryEnum;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.WindChargeEntity;

import javax.swing.text.html.parser.Entity;

public class WindTornadoEntityModel<T extends WindTornadoEntity> extends SinglePartEntityModel<T> {
	private final ModelPart tornado;
	private final ModelPart inside;
	private final ModelPart outside;
	public WindTornadoEntityModel(ModelPart root) {
		this.tornado = root.getChild("tornado");
		this.inside = tornado.getChild("inside");
		this.outside = tornado.getChild("outside");
	}
	public static TexturedModelData getTexturedModelData() {

		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData tornado = modelPartData.addChild("tornado", ModelPartBuilder.create(), ModelTransform.of(0.0F, 7.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

		ModelPartData inside = tornado.addChild("inside", ModelPartBuilder.create().uv(64, 14).cuboid(-1.0F, -4.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
				.uv(0, 64).cuboid(-2.0F, -6.0F, -2.0F, 4.0F, 2.0F, 4.0F, new Dilation(0.0F))
				.uv(28, 58).cuboid(-2.0F, -10.0F, -5.0F, 7.0F, 2.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 55).cuboid(-3.0F, -8.0F, -4.0F, 7.0F, 2.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 44).cuboid(-6.0F, -14.0F, -5.0F, 9.0F, 2.0F, 9.0F, new Dilation(0.0F))
				.uv(56, 58).cuboid(-3.0F, -12.0F, -6.0F, 7.0F, 2.0F, 7.0F, new Dilation(0.0F))
				.uv(18, 0).cuboid(3.0F, -16.0F, -6.0F, 2.0F, 2.0F, 12.0F, new Dilation(0.0F))
				.uv(13, 7).cuboid(-5.0F, -16.0F, -6.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F))
				.uv(2, 2).cuboid(-7.0F, -16.0F, -6.0F, 2.0F, 2.0F, 10.0F, new Dilation(0.0F))
				.uv(12, 0).cuboid(-7.0F, -16.0F, 4.0F, 10.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, 0.0F));

		ModelPartData outside = tornado.addChild("outside", ModelPartBuilder.create().uv(0, 82).cuboid(-8.0F, -16.0F, -8.0F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F))
				.uv(0, 70).cuboid(-8.0F, -16.0F, -8.0F, 16.0F, 14.0F, 0.0F, new Dilation(0.0F))
				.uv(0, 68).cuboid(8.0F, -16.0F, -8.0F, 0.0F, 14.0F, 16.0F, new Dilation(0.0F))
				.uv(0, 112).cuboid(-8.0F, -16.0F, 8.0F, 16.0F, 14.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 9.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		tornado.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return tornado;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		// Rotate inside clockwise
		this.inside.yaw = -animationProgress * 16.0f * ((float)Math.PI / 180);
		this.outside.yaw = animationProgress * 16.0f * ((float)Math.PI / 180);
		// Rotate outside counter-clockwise
	}
}
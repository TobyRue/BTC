package io.github.tobyrue.btc.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import javax.swing.text.html.parser.Entity;

// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class SpyGlassBlockModel extends Model {
	final ModelPart spy_glass_block;
	private final ModelPart post;
	final ModelPart axis_1;

	public SpyGlassBlockModel(ModelPart root) {
		super(RenderLayer::getEntityCutout);
		this.spy_glass_block = root.getChild("spy_glass_block");
		this.post = this.spy_glass_block.getChild("post");
		this.axis_1 = this.spy_glass_block.getChild("axis_1");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData spy_glass_block = modelPartData.addChild("spy_glass_block", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		ModelPartData post = spy_glass_block.addChild("post", ModelPartBuilder.create().uv(4, 11).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData axis_1 = spy_glass_block.addChild("axis_1", ModelPartBuilder.create().uv(6, 7).cuboid(-0.5F, -6.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(10, 7).cuboid(-2.5F, -6.0F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F))
		.uv(11, 3).cuboid(-2.5F, -9.0F, -0.5F, 1.0F, 3.0F, 1.0F, new Dilation(0.0F))
		.uv(7, 3).cuboid(-1.5F, -9.0F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
		return TexturedModelData.of(modelData, 16, 16);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		spy_glass_block.render(matrices, vertices, light, overlay, color);
	}
}
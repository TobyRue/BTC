package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.entity.custom.MineEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class MineEntityModel<T extends MineEntity> extends SinglePartEntityModel<T> {
	private final ModelPart mine;
	private final ModelPart base;
	private final ModelPart spines;
	private final ModelPart sided;
	private final ModelPart axes;
	private final ModelPart x;
	private final ModelPart y;
	private final ModelPart z;
	private final ModelPart up_2_axes;
	private final ModelPart down_2_axes;
	public MineEntityModel(ModelPart root) {
		this.mine = root.getChild("mine");
		this.base = this.mine.getChild("base");
		this.spines = this.mine.getChild("spines");
		this.sided = this.spines.getChild("sided");
		this.axes = this.spines.getChild("axes");
		this.x = this.axes.getChild("x");
		this.y = this.axes.getChild("y");
		this.z = this.axes.getChild("z");
		this.up_2_axes = this.axes.getChild("up_2_axes");
		this.down_2_axes = this.axes.getChild("down_2_axes");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData mine = modelPartData.addChild("mine", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));

		ModelPartData base = mine.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -5.0F, -5.0F, 10.0F, 10.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData spines = mine.addChild("spines", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData sided = spines.addChild("sided", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, 5.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(56, 5).cuboid(-1.0F, -8.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F))
				.uv(54, 0).cuboid(-1.0F, -1.0F, -8.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(54, 0).cuboid(-1.0F, -1.0F, 5.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F))
				.uv(54, 10).cuboid(-8.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F))
				.uv(54, 10).cuboid(5.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData axes = spines.addChild("axes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData x = axes.addChild("x", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r1 = x.addChild("cube_r1", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, 4.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r2 = x.addChild("cube_r2", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, 4.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r3 = x.addChild("cube_r3", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 4.0F, -4.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData cube_r4 = x.addChild("cube_r4", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -4.0F, -4.0F, 0.7854F, 0.0F, 0.0F));

		ModelPartData y = axes.addChild("y", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r5 = y.addChild("cube_r5", ModelPartBuilder.create().uv(54, 10).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 4.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r6 = y.addChild("cube_r6", ModelPartBuilder.create().uv(54, 10).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r7 = y.addChild("cube_r7", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 4.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData cube_r8 = y.addChild("cube_r8", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -4.0F, 0.0F, -0.7854F, 0.0F));

		ModelPartData z = axes.addChild("z", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData cube_r9 = z.addChild("cube_r9", ModelPartBuilder.create().uv(54, 10).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r10 = z.addChild("cube_r10", ModelPartBuilder.create().uv(54, 10).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r11 = z.addChild("cube_r11", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, -4.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData cube_r12 = z.addChild("cube_r12", ModelPartBuilder.create().uv(56, 5).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 4.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

		ModelPartData up_2_axes = axes.addChild("up_2_axes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -4.0F, 0.0F));

		ModelPartData cube_r13 = up_2_axes.addChild("cube_r13", ModelPartBuilder.create().uv(54, 10).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -4.0F, -0.6155F, -0.5236F, 0.9553F));

		ModelPartData cube_r14 = up_2_axes.addChild("cube_r14", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 4.0F, 0.7854F, -0.7854F, 0.0F));

		ModelPartData cube_r15 = up_2_axes.addChild("cube_r15", ModelPartBuilder.create().uv(54, 10).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 4.0F, 0.6155F, -0.5236F, -0.9553F));

		ModelPartData cube_r16 = up_2_axes.addChild("cube_r16", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -4.0F, -0.7854F, -0.7854F, 0.0F));

		ModelPartData down_2_axes = axes.addChild("down_2_axes", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 4.0F, 0.0F));

		ModelPartData cube_r17 = down_2_axes.addChild("cube_r17", ModelPartBuilder.create().uv(54, 10).cuboid(0.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, 4.0F, -0.6155F, -0.5236F, 0.9553F));

		ModelPartData cube_r18 = down_2_axes.addChild("cube_r18", ModelPartBuilder.create().uv(54, 10).cuboid(-3.0F, -1.0F, -1.0F, 3.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, -4.0F, 0.6155F, -0.5236F, -0.9553F));

		ModelPartData cube_r19 = down_2_axes.addChild("cube_r19", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.0F, 0.0F, 4.0F, -0.7854F, -0.7854F, 0.0F));

		ModelPartData cube_r20 = down_2_axes.addChild("cube_r20", ModelPartBuilder.create().uv(54, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(4.0F, 0.0F, -4.0F, 0.7854F, -0.7854F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		mine.render(matrices, vertices, light, overlay, color);
	}

	@Override
	public ModelPart getPart() {
		return mine;
	}

	@Override
	public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

	}
}
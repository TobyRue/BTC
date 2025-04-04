// Made with Blockbench 4.12.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

package com.example.mod;
   
public class TuffGolemEntityModel extends EntityModel<Entity> {
	private final ModelPart tuff_golem;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart nose;
	private final ModelPart main_head;
	private final ModelPart arms;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart cloth;
	private final ModelPart front;
	private final ModelPart sides;
	private final ModelPart back;
	private final ModelPart body_2;
	private final ModelPart slider;
	private final ModelPart main_body;
	private final ModelPart legs;
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	public TuffGolemEntityModel(ModelPart root) {
		this.tuff_golem = root.getChild("tuff_golem");
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.nose = root.getChild("nose");
		this.main_head = root.getChild("main_head");
		this.arms = root.getChild("arms");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.cloth = root.getChild("cloth");
		this.front = root.getChild("front");
		this.sides = root.getChild("sides");
		this.back = root.getChild("back");
		this.body_2 = root.getChild("body_2");
		this.slider = root.getChild("slider");
		this.main_body = root.getChild("main_body");
		this.legs = root.getChild("legs");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
	}
	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData tuff_golem = modelPartData.addChild("tuff_golem", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 13.0F, 0.0F));

		ModelPartData body = tuff_golem.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData nose = head.addChild("nose", ModelPartBuilder.create().uv(32, 43).cuboid(-1.0F, -1.0F, -2.0044F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -2.0F, -4.0F));

		ModelPartData main_head = head.addChild("main_head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -7.0F, -4.0F, 8.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData arms = body.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));

		ModelPartData left_arm = arms.addChild("left_arm", ModelPartBuilder.create().uv(42, 30).cuboid(0.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, 0.0F, 0.0F));

		ModelPartData right_arm = arms.addChild("right_arm", ModelPartBuilder.create().uv(32, 30).cuboid(-2.0F, -2.0F, -1.5F, 2.0F, 10.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, 0.0F, 0.0F));

		ModelPartData cloth = body.addChild("cloth", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 3.0F, 0.0F));

		ModelPartData front = cloth.addChild("front", ModelPartBuilder.create().uv(32, 18).cuboid(-4.0F, 1.0F, -4.0F, 8.0F, 6.0F, 0.0F, new Dilation(0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData sides = cloth.addChild("sides", ModelPartBuilder.create().uv(0, 29).cuboid(4.0F, 1.0F, -4.0F, 0.0F, 6.0F, 8.0F, new Dilation(-0.02F))
		.uv(16, 29).cuboid(-4.0F, 1.0F, -4.0F, 0.0F, 6.0F, 8.0F, new Dilation(0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData back = cloth.addChild("back", ModelPartBuilder.create().uv(32, 24).cuboid(-4.0F, 1.0F, 4.0F, 8.0F, 6.0F, 0.0F, new Dilation(-0.02F)), ModelTransform.pivot(0.0F, -3.0F, 0.0F));

		ModelPartData body_2 = body.addChild("body_2", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData slider = body_2.addChild("slider", ModelPartBuilder.create().uv(34, 43).cuboid(-4.0F, -0.91F, -3.0F, 8.0F, 2.0F, 6.0F, new Dilation(0.0F))
		.uv(34, 51).cuboid(-4.0F, -1.91F, -3.0F, 8.0F, 3.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 2.9F, -1.0F, 1.5708F, 0.0F, 0.0F));

		ModelPartData main_body = body_2.addChild("main_body", ModelPartBuilder.create().uv(0, 15).cuboid(-4.0F, 0.0F, -4.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData legs = tuff_golem.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(32, 9).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(2.0F, 6.0F, 0.0F));

		ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(32, 0).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 6.0F, 0.0F));
		return TexturedModelData.of(modelData, 64, 64);
	}
	@Override
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		tuff_golem.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
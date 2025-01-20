//// Made with Blockbench 4.12.1
//// Exported for Minecraft version 1.17+ for Yarn
//// Paste this class into your mod and generate all required imports
//public class WaterBlastxxxxxx extends EntityModel<Entity> {
//    private final ModelPart waterBlast;
//    private final ModelPart head;
//    private final ModelPart tail;
//    private final ModelPart bottom;
//    private final ModelPart top;
//    private final ModelPart right;
//    private final ModelPart left;
//    public WaterBlastxxxxxx(ModelPart root) {
//        this.waterBlast = root.getChild("waterBlast");
//        this.head = this.waterBlast.getChild("head");
//        this.tail = this.waterBlast.getChild("tail");
//        this.bottom = this.tail.getChild("bottom");
//        this.top = this.tail.getChild("top");
//        this.right = this.tail.getChild("right");
//        this.left = this.tail.getChild("left");
//    }
//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//        ModelPartData waterBlast = modelPartData.addChild("waterBlast", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 22.0F, -2.0F));
//
//        ModelPartData head = waterBlast.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 1.0F, 0.0F));
//
//        ModelPartData cube_r1 = head.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -18.0F, 0.0F, 0.0F, -1.5708F, 0.0F));
//
//        ModelPartData tail = waterBlast.addChild("tail", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
//
//        ModelPartData bottom = tail.addChild("bottom", ModelPartBuilder.create().uv(0, 13).cuboid(-3.0F, -22.0F, -1.0F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -2.0F, 3.0F));
//
//        ModelPartData top = tail.addChild("top", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0F, -22.0F, -1.0F, 4.0F, 0.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(1.0F, -6.0F, 3.0F));
//
//        ModelPartData right = tail.addChild("right", ModelPartBuilder.create().uv(18, 0).cuboid(1.0F, -22.0F, -1.0F, 0.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -6.0F, 3.0F));
//
//        ModelPartData left = tail.addChild("left", ModelPartBuilder.create().uv(0, 18).cuboid(5.0F, -22.0F, -1.0F, 0.0F, 4.0F, 5.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, -6.0F, 3.0F));
//        return TexturedModelData.of(modelData, 32, 32);
//    }
//    @Override
//    public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//    }
//    @Override
//    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
//        waterBlast.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
//    }
//}
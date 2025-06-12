package io.github.tobyrue.btc.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SpellBookModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/item/spell_book.png");

    private final ModelPart book;
    private final ModelPart spine;
    private final ModelPart left;
    private final ModelPart left_cover;
    private final ModelPart left_page_no_flip;
    private final ModelPart fliping_pages2;
    private final ModelPart bottomleft;
    private final ModelPart topleft;
    private final ModelPart right;
    private final ModelPart right_page_no_flip;
    private final ModelPart right_cover;
    private final ModelPart fliping_pages;
    private final ModelPart bottomright;
    private final ModelPart topright;


    public SpellBookModelRenderer(ModelPart book) {
        this.book = book.getChild("book");
        this.spine = this.book.getChild("spine");
        this.left = this.book.getChild("left");
        this.left_cover = this.left.getChild("left_cover");
        this.left_page_no_flip = this.left.getChild("left_page_no_flip");
        this.fliping_pages2 = this.left.getChild("fliping_pages2");
        this.bottomleft = this.fliping_pages2.getChild("bottomleft");
        this.topleft = this.fliping_pages2.getChild("topleft");
        this.right = this.book.getChild("right");
        this.right_page_no_flip = this.right.getChild("right_page_no_flip");
        this.right_cover = this.right.getChild("right_cover");
        this.fliping_pages = this.right.getChild("fliping_pages");
        this.bottomright = this.fliping_pages.getChild("bottomright");
        this.topright = this.fliping_pages.getChild("topright");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData book = modelPartData.addChild("book", ModelPartBuilder.create(), ModelTransform.of(-1.0F, 23.0F, 2.0F, 0.9599F, 0.0F, 0.0F));

        ModelPartData spine = book.addChild("spine", ModelPartBuilder.create().uv(8, 22).cuboid(0.0F, -27.0F, -14.0F, 2.0F, 7.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 20.0F, 13.0F));

        ModelPartData left = book.addChild("left", ModelPartBuilder.create(), ModelTransform.of(1.0F, -3.0F, 0.0F, 0.0F, -0.3491F, 0.0F));

        ModelPartData left_cover = left.addChild("left_cover", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, -7.0F, -1.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 3.0F, 0.0F));

        ModelPartData left_page_no_flip = left.addChild("left_page_no_flip", ModelPartBuilder.create().uv(8, 16).cuboid(1.0F, -6.0F, -1.0F, 4.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 3.0F, 0.0F));

        ModelPartData fliping_pages2 = left.addChild("fliping_pages2", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.3054F, 0.0F));

        ModelPartData bottomleft = fliping_pages2.addChild("bottomleft", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r1 = bottomleft.addChild("cube_r1", ModelPartBuilder.create().uv(0, 7).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.2217F, 0.0F));

        ModelPartData topleft = fliping_pages2.addChild("topleft", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r2 = topleft.addChild("cube_r2", ModelPartBuilder.create().uv(8, 7).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 1.1781F, 0.0F));

        ModelPartData right = book.addChild("right", ModelPartBuilder.create(), ModelTransform.of(1.0F, -3.0F, 0.0F, 0.0F, 0.3054F, 0.0F));

        ModelPartData right_page_no_flip = right.addChild("right_page_no_flip", ModelPartBuilder.create().uv(18, 16).cuboid(-3.0F, -6.0F, -1.0F, 4.0F, 5.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 3.0F, 0.0F));

        ModelPartData right_cover = right.addChild("right_cover", ModelPartBuilder.create().uv(10, 0).cuboid(-4.0F, -7.0F, -1.0F, 5.0F, 7.0F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 3.0F, 0.0F));

        ModelPartData fliping_pages = right.addChild("fliping_pages", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.3054F, 0.0F));

        ModelPartData bottomright = fliping_pages.addChild("bottomright", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r3 = bottomright.addChild("cube_r3", ModelPartBuilder.create().uv(0, 16).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.2217F, 0.0F));

        ModelPartData topright = fliping_pages.addChild("topright", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData cube_r4 = topright.addChild("cube_r4", ModelPartBuilder.create().uv(16, 7).cuboid(0.0F, -3.0F, 0.0F, 0.0F, 5.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -1.1781F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }
    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.book.render(matrices, vertices, light, overlay);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    }
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();


        matrices.translate(0.5, 0.5, 0.20);

        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        if (mode == ModelTransformationMode.GUI) {
            matrices.translate(0, -0.76, 1.3);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(50));
        } else if (mode == ModelTransformationMode.FIXED) {
            matrices.translate(0, 0.9, -0.8);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(130));
        } else {
            matrices.translate(0, -0.627, 1.3);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(50));
        }
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        renderModel(stack, matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}

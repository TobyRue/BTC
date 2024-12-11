package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class DragonStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer{
    public static final ItemStack HANDLE_DRAGON = new ItemStack(ModItems.STAFF, 1);
    public static final ItemStack ENDER_PEARL = new ItemStack(Items.ENDER_PEARL, 1);
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/item/dragon_breath_staff.png");
    private static final String ELEMENT1 = "element1";
    private static final String ELEMENT2 = "element2";
    private static final String ELEMENT3 = "element3";
    private static final String ELEMENT4 = "element4";

    private final ModelPart element1;
    private final ModelPart element2;
    private final ModelPart element3;
    private final ModelPart element4;
    private final ModelPart root;

    public DragonStaffModelRenderer(ModelPart root) {
        this.root = root;
        this.element1 = root.getChild("element1");
        this.element2 = root.getChild("element2");
        this.element3 = root.getChild("element3");
        this.element4 = root.getChild("element4");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("element1", ModelPartBuilder.create()
                .uv(11, 16).cuboid(5.5F, -12.7F, -4.15F, 1.0F, 22.0F, 1.0F), ModelTransform.of(-7.0F, -5F, -1.5F, 2.65F, 0.0F, 0.0F));

        modelPartData.addChild("element2", ModelPartBuilder.create()
                .uv(8, 16).cuboid(6.5F, -12.7F, -5.15F, 1.0F, 22.0F, 1.0F), ModelTransform.of(-7.0F, -5F, -1.5F, 2.65F, 0.0F, 0.0F));

        modelPartData.addChild("element3", ModelPartBuilder.create()
                .uv(18, 25).cuboid(6.5F, -12.7F, -3.15F, 1.0F, 22.0F, 1.0F), ModelTransform.of(-7.0F, -5F, -1.5F, 2.65F, 0.0F, 0.0F));

        modelPartData.addChild("element4", ModelPartBuilder.create()
                .uv(24, 12).cuboid(7.5F, -12.7F, -4.15F, 1.0F, 22.0F, 1.0F), ModelTransform.of(-7.0F, -5F, -1.5F, 2.65F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    public ModelPart getPart1() {return this.element1;}
    public ModelPart getPart2() {return this.element2;}
    public ModelPart getPart3() {return this.element3;}
    public ModelPart getPart4() {return this.element4;}

    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    }
    public void renderFire(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        // Rotate the fire charge continuously
        long time = System.currentTimeMillis() % 3600L; // Get time to create smooth rotation
        float angle = (time / 10.0f) % 360; // Adjust rotation speed by changing divisor

        matrices.translate(0.5, 1.4, 0.2);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle)); // Apply rotation on Y-axis

        minecraft.getItemRenderer().renderItem(ENDER_PEARL, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();
    }
    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        renderFire(stack, mode, matrices, vertexConsumers, light, overlay);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20));

        matrices.translate(0.5, 0.5, 0.20);

        minecraft.getItemRenderer().renderItem(HANDLE_DRAGON, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));

        renderModel(stack, matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}

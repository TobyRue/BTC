package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import java.util.Stack;

import static io.github.tobyrue.btc.client.BTCClient.WIND_STAFF_LAYER;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    private static final DummyWindCharge dummy = new DummyWindCharge(); // Static variable
    private static int renderCounter = 0; // Counter to slow down age update
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/item/breeze_rods.png");

    private static final String ELEMENT1 = "element1";
    private static final String ELEMENT2 = "element2";
    private static final String ELEMENT3 = "element3";
    private static final String ELEMENT4 = "element4";




    private final ModelPart element1;
    private final ModelPart element2;
    private final ModelPart element3;
    private final ModelPart element4;
    private final ModelPart root;

    public WindStaffModelRenderer(ModelPart root) {
        this.root = root;
        this.element1 = root.getChild("element1");
        this.element2 = root.getChild("element2");
        this.element3 = root.getChild("element3");
        this.element4 = root.getChild("element4");
    }
//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//
//        // Adjusting element1
//        modelPartData.addChild("element1", ModelPartBuilder.create()
//                        .uv(11, 16).cuboid(6.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F),
//                ModelTransform.of(5.5F, -6F, 7.5F, 0.0F, 30.0F, 0.0F)); // Yaw rotation of 45 degrees
//
//        // Adjusting element2
//        modelPartData.addChild("element2", ModelPartBuilder.create()
//                        .uv(8, 16).cuboid(7.5F, -5F, 6.5F, 1.0F, 22.0F, 1.0F),
//                ModelTransform.of(6.5F, -6F, 6.5F, 0.0F, 30.0F, 0.0F)); // No changes here
//
//        // Adjusting element3 with a slight upward shift and rotation
//        modelPartData.addChild("element3", ModelPartBuilder.create()
//                        .uv(18, 25).cuboid(7.5F, -5F, 8.5F, 1.0F, 22.0F, 1.0F),
//                ModelTransform.of(6.5F, -5F, 8.5F, 0.0F, 30.0F, 0.0F)); // Rotated 90 degrees around Y-axis
//
//        // Adjusting element4 with a rotation around the X-axis
//        modelPartData.addChild("element4", ModelPartBuilder.create()
//                        .uv(24, 12).cuboid(8.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F),
//                ModelTransform.of(7.5F, -6F, 7.5F, 0.0F, 30.0F, 0.0F)); // Rotated 30 degrees around X-axis (pitch)
//
//        return TexturedModelData.of(modelData, 16, 16);
//    }

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
//    public static TexturedModelData getTexturedModelData() {
//        ModelData modelData = new ModelData();
//        ModelPartData modelPartData = modelData.getRoot();
//
//        modelPartData.addChild("element1", ModelPartBuilder.create()
//                .uv(11, 16).cuboid(-7.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F), ModelTransform.of(5.5F, -6F, 7.5F, 0.0F, 0.0F, 0.0F));
//
//        modelPartData.addChild("element2", ModelPartBuilder.create()
//                .uv(8, 16).cuboid(-6.5F, -5F, 6.5F, 1.0F, 22.0F, 1.0F), ModelTransform.of(6.5F, -6F, 6.5F, 0.0F, 0.0F, 0.0F));
//
//        modelPartData.addChild("element3", ModelPartBuilder.create()
//                .uv(18, 25).cuboid(-6.5F, -5F, 8.5F, 1.0F, 22.0F, 1.0F), ModelTransform.of(6.5F, -6F, 8.5F, 0.0F, 0.0F, 0.0F));
//
//        modelPartData.addChild("element4", ModelPartBuilder.create()
//                .uv(24, 12).cuboid(-5.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F), ModelTransform.of(7.5F, -6F, 7.5F, 0.0F, 0.0F, 0.0F));
//        return TexturedModelData.of(modelData, 16, 16);
//    }
    public ModelPart getPart1() {return this.element1;}
    public ModelPart getPart2() {return this.element2;}
    public ModelPart getPart3() {return this.element3;}
    public ModelPart getPart4() {return this.element4;}

    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    }


    private void updateDummy() {
        // Increment the age of the dummy entity every 10 render calls
        if (renderCounter % 10 == 0) {
            dummy.age++;
        }
        renderCounter++;
    }


    public void renderWind(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        EntityRendererFactory.Context context = new EntityRendererFactory.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderManager(), null, minecraft.getResourceManager(), minecraft.getEntityModelLoader(), minecraft.textRenderer
        );

        /*String name = stack.getName().getLiteralString();
        float x = 0, y = 0, z = 0;
        try {
            if(name.startsWith("@")) {
                var t = name.substring(1).split(",");
                x = Float.parseFloat(t[0].trim());
                y = Float.parseFloat(t[1].trim());
                z = Float.parseFloat(t[2].trim());
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }*/
        // Move the item
        matrices.translate(0.5, 1.5, 0.2);


        // Rotate the item

        updateDummy();
        new WindChargeEntityRenderer(context).render(dummy, 0, tickDelta, matrices, vertexConsumers, light);

        // Render the Wind Charge entity
        //WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, minecraft.world);
        //minecraft.getEntityRenderDispatcher().render(windChargeEntity, 0, 0, 0, 0.0f, tickDelta, matrices, vertexConsumers, lightAbove);

        // Mandatory call after GL calls
        matrices.pop();
    }

//    public void renderRods(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        matrices.push();
//
//        matrices.pop();
//    }

        @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        //double offset = Math.sin((MinecraftClient.getInstance().world.getTime() + tickDelta) / 8.0) / 8.0;

        renderWind(stack, mode, matrices, vertexConsumers, light, overlay);

        // Move the item
        matrices.translate(0.5, 1.3, 0.5);

        // Mandatory call after GL calls

        /*String name = stack.getName().getLiteralString();
        float x = 0, y = 0, z = 0, ry = 0, rz = 0, rx = 0;
        try {
            if(name.startsWith("@")) {
                var t = name.substring(1).split(",");
                x = Float.parseFloat(t[0].trim());
                y = Float.parseFloat(t[1].trim());
                z = Float.parseFloat(t[2].trim());
                rx = Float.parseFloat(t[3].trim());
                ry = Float.parseFloat(t[4].trim());
                rz = Float.parseFloat(t[5].trim());
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }*/

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20));
        matrices.translate(0, -0.8, 0.15);
        minecraft.getItemRenderer().renderItem(HANDLE, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        // Bind texture
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);

        // Create VertexConsumer for the texture
        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        renderModel(stack, matrices, vertices, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
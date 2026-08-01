package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FireStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE_FIRE = new ItemStack(ModItems.STAFF, 1);
    public static final ItemStack FIRE_CHARGE = new ItemStack(Items.FIRE_CHARGE, 1);
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/item/blaze_rods.png");

    // Static values modified by the chat tuner or defaults
    public static float fireTransX = 0.5f;
    public static float fireTransY = 1.1f;
    public static float fireTransZ = 0.35f;

    public static float rotX = 27.0f;
    public static float rotY = 0.0f;
    public static float rotZ = 0.0f;
    public static float transX = 0.5f;
    public static float transY = 0.67f;
    public static float transZ = 0.20f;
    public static float fireScale = 1f;
    public static float scale = 1f;

    private final ModelPart element1;
    private final ModelPart element2;
    private final ModelPart element3;
    private final ModelPart element4;
    private final ModelPart root;

    public FireStaffModelRenderer(ModelPart root) {
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

    public void renderFire(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float currentFireX, float currentFireY, float currentFireZ, float currentRotX, float currentRotZ, float currentFireScale) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        long time = System.currentTimeMillis() % 3600L;
        float angle = (time / 10.0f) % 360;
        matrices.translate(currentFireX, currentFireY, currentFireZ);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));
        matrices.scale(currentFireScale, currentFireScale, currentFireScale);

        minecraft.getItemRenderer().renderItem(FIRE_CHARGE, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float currentRotX = rotX;
        float currentRotY = rotY;
        float currentRotZ = rotZ;

        float currentTransX = transX;
        float currentTransY = transY;
        float currentTransZ = transZ;

        float currentFireX = fireTransX;
        float currentFireY = fireTransY;
        float currentFireZ = fireTransZ;

        float currentFireScale = fireScale;
        float currentScale = scale;

        switch (mode) {
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                currentRotX = 27;
                currentRotY = 0;    
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.2f;

                currentFireX = 0.5f;
                currentFireY = 1.1f;
                currentFireZ = 0.77f;

                currentFireScale = 1;
                currentScale = 1;
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                currentRotX = 32;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.12f;
                currentTransZ = 0.59f;

                currentFireX = 0.5f;
                currentFireY = 0.5f;
                currentFireZ = 1.02f;

                currentFireScale = 1;
                currentScale = 1;
            }
            case GUI -> {
                currentRotX = 45;
                currentRotY = 45;
                currentRotZ = rotZ;

                currentTransX = 0.75f;
                currentTransY = 1;
                currentTransZ = 0.2f;

                currentFireX = 0.3f;
                currentFireY = 1.7f;
                currentFireZ = 0;

                currentFireScale = 1;
                currentScale = 2;
            }
            case GROUND -> {
                currentRotX = 20;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.2f;

                currentFireX = 0.5f;
                currentFireY = 1.05f;
                currentFireZ = 0.59f;

                currentFireScale = 0.5f;
                currentScale = 1;
            }
            case FIXED -> {
                currentRotX = 0;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.5f;

                currentFireX = 0.5f;
                currentFireY = 1.3f;
                currentFireZ = 0.5f;
            }
            case HEAD -> {
                // TODO
                currentRotX = rotX;     // 27.0f
                currentRotY = rotY;     // 0.0f
                currentRotZ = rotZ;     // 0.0f

                currentTransX = transX; // 0.50f
                currentTransY = transY; // 0.67f
                currentTransZ = transZ; // 0.20f

                currentFireX = fireTransX; // 0.50f
                currentFireY = fireTransY; // 1.10f
                currentFireZ = fireTransZ; // 0.35f
            }
            default -> {}
        }

        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        renderFire(stack, mode, matrices, vertexConsumers, light, overlay, currentFireX, currentFireY, currentFireZ, currentRotX, currentRotZ, currentFireScale);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentRotY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));

        matrices.translate(currentTransX, currentTransY, currentTransZ);
        matrices.scale(currentScale, currentScale, currentScale);

        minecraft.getItemRenderer().renderItem(HANDLE_FIRE, mode, light, overlay, matrices, vertexConsumers, minecraft.world, 0);

        matrices.pop();
    }
}
package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class FireStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    public static final ItemStack FIRE_CHARGE = new ItemStack(Items.FIRE_CHARGE, 1);
    public static final Identifier TEXTURE = BTC.identifierOf("textures/item/fire_staff_overlay.png");
    public static final SpriteIdentifier SPRITE_ID = AnimatedTextureHelper.createItemSpriteId(TEXTURE);
    private final ModelPart root;

    public FireStaffModelRenderer(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -16.5F, -1.5F, 3.0F, 14.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData cube_r1 = root.addChild("cube_r1", ModelPartBuilder.create().uv(0, 17).mirrored().cuboid(8.8636F, -5.7062F, -9.5F, 3.0F, 8.0F, 3.0F, new Dilation(-0.3F)).mirrored(false), ModelTransform.of(-5.6F, -25.4F, 8.0F, 0.0F, 0.0F, 0.7854F));

        ModelPartData cube_r2 = root.addChild("cube_r2", ModelPartBuilder.create().uv(0, 17).cuboid(-11.8636F, -5.7062F, -9.5F, 3.0F, 8.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.of(5.6F, -25.4F, 8.0F, 0.0F, 0.0F, -0.7854F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        VertexConsumer vertexConsumer = AnimatedTextureHelper.getBuffer(vertices, SPRITE_ID);
        this.root.render(matrices, vertexConsumer, light, overlay);
    }

    public void renderItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float currentItemX, float currentItemY, float currentItemZ, float currentRotX, float currentRotZ, float currentItemScale) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        long time = System.currentTimeMillis() % 3600L;
        float angle = (time / 10.0f) % 360;

        applyTransformations(matrices, currentItemX, currentItemY, currentItemZ, currentRotX, angle, currentRotZ, currentItemScale);

        minecraft.getItemRenderer().renderItem(FIRE_CHARGE, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();
    }

    private void applyTransformations(MatrixStack matrices, float transX, float transY, float transZ, float rotX, float rotY, float rotZ, float scale) {
        matrices.translate(transX, transY, transZ);
        if (rotX != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotX));
        if (rotY != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotY));
        if (rotZ != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotZ));
        if (scale != 1.0f) matrices.scale(scale, scale, scale);
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float currentRotX = DragonStaffModelRenderer.rotX, currentRotY = DragonStaffModelRenderer.rotY, currentRotZ = DragonStaffModelRenderer.rotZ;
        float currentTransX = DragonStaffModelRenderer.transX, currentTransY = DragonStaffModelRenderer.transY, currentTransZ = DragonStaffModelRenderer.transZ;
        float currentItemX = DragonStaffModelRenderer.itemTransX, currentItemY = DragonStaffModelRenderer.itemTransY, currentItemZ = DragonStaffModelRenderer.itemTransZ;
        float currentItemScale = DragonStaffModelRenderer.itemScale, currentScale = DragonStaffModelRenderer.scale;

        switch (mode) {
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                currentRotX = 27; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.5f; currentTransZ = 0.5f;
                currentItemX = 0.5f; currentItemY = 1.1f; currentItemZ = 0.8f;
                currentItemScale = 0.75f; currentScale = 1;
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                currentRotX = 32; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = -0.2f; currentTransZ = 0.55f;
                currentItemX = 0.5f; currentItemY = 0.55f; currentItemZ = 1;
                currentItemScale = 1; currentScale = 1;
            }
            case GUI -> {
                currentRotX = 45; currentRotY = 45; currentRotZ = 0;
                currentTransX = 0.4f; currentTransY = 1; currentTransZ = 0.2f;
                currentItemX = 0.3f; currentItemY = 1.6f; currentItemZ = 0;
                currentItemScale = 1; currentScale = 2;
            }
            case GROUND -> {
                currentRotX = 20; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.15f; currentTransZ = 0.55f;
                currentItemX = 0.5f; currentItemY = 1.2f; currentItemZ = 0.95f;
                currentItemScale = 0.9f; currentScale = 2;
            }
            case FIXED -> {
                currentRotX = 0; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.67f; currentTransZ = 0.5f;
                currentItemX = 0.5f; currentItemY = 1.3f; currentItemZ = 0.5f;
                currentItemScale = 1; currentScale = 1.3f;
            }
            case HEAD -> {
                currentRotX = 0; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.9f; currentTransZ = 0.25f;
                currentItemX = 0.5f; currentItemY = 1.5f; currentItemZ = 0.3f;
                currentItemScale = 1; currentScale = 1.3f;
            }
            default -> {}
        }

        float currentOverlayRotX = DragonStaffModelRenderer.rotX;
        float currentOverlayRotY = DragonStaffModelRenderer.rotY;
        float currentOverlayRotZ = DragonStaffModelRenderer.rotZ;

        float currentOverlayTransX = DragonStaffModelRenderer.transX;
        float currentOverlayTransY = DragonStaffModelRenderer.transY;
        float currentOverlayTransZ = DragonStaffModelRenderer.transZ;

        float currentOverlayScale = 1.0f;

        switch (mode) {
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                currentOverlayRotX = 180; currentOverlayRotY = 90; currentOverlayRotZ = 0;
                currentOverlayTransX = 0; currentOverlayTransY = 0.85f; currentOverlayTransZ = 0;
                currentOverlayScale = 1;
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                currentOverlayRotX = 180; currentOverlayRotY = 90; currentOverlayRotZ = 0;
                currentOverlayTransX = 0; currentOverlayTransY = 1; currentOverlayTransZ = 0;
                currentOverlayScale = 1;
            }
            case GUI -> {
                currentOverlayRotX = 180; currentOverlayRotY = -10; currentOverlayRotZ = 45;
                currentOverlayTransX = 0.27f; currentOverlayTransY = 0.3f; currentOverlayTransZ = 0;
                currentOverlayScale = 0.65f;
            }
            case GROUND -> {
                currentOverlayRotX = 180; currentOverlayRotY = 0; currentOverlayRotZ = 0;
                currentOverlayTransX = 0; currentOverlayTransY = 0.625f; currentOverlayTransZ = 0;
                currentOverlayScale = 0.5f;
            }
            case FIXED -> {
                currentOverlayRotX = 180; currentOverlayRotY = 0; currentOverlayRotZ = 0;
                currentOverlayTransX = 0; currentOverlayTransY = 0.625f; currentOverlayTransZ = 0;
                currentOverlayScale = 0.75f;
            }
            case HEAD -> {
                currentOverlayRotX = 180f; currentOverlayRotY = 0f; currentOverlayRotZ = 0f;
                currentOverlayTransX = 0; currentOverlayTransY = 0.725f; currentOverlayTransZ = 0;
                currentOverlayScale = 1;
            }
            default -> {}
        }

        var minecraft = MinecraftClient.getInstance();

        renderItem(stack, mode, matrices, vertexConsumers, light, overlay, currentItemX, currentItemY, currentItemZ, currentRotX, currentRotZ, currentItemScale);

        matrices.push();
        applyTransformations(matrices, currentTransX, currentTransY, currentTransZ, currentRotX, currentRotY, currentRotZ, currentScale);
        minecraft.getItemRenderer().renderItem(HANDLE, mode, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();

        matrices.push();
        applyTransformations(matrices, currentTransX, currentTransY, currentTransZ, currentRotX, currentRotY, currentRotZ, currentScale);
        applyTransformations(matrices, currentOverlayTransX, currentOverlayTransY, currentOverlayTransZ, currentOverlayRotX, currentOverlayRotY, currentOverlayRotZ, currentOverlayScale);

//        renderModel(stack, matrices, vertexConsumers, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }
}
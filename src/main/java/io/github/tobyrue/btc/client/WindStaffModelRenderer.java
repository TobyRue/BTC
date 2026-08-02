package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    private final DummyWindCharge dummy = new DummyWindCharge();
    public static final Identifier TEXTURE = BTC.identifierOf("textures/item/wind_staff_overlay.png");
    public static final SpriteIdentifier SPRITE_ID = AnimatedTextureHelper.createItemSpriteId(TEXTURE);

    public static float itemTransX = 0.5f;
    public static float itemTransY = 1.1f;
    public static float itemTransZ = 0.35f;

    public static float rotX = 27.0f;
    public static float rotY = 0.0f;
    public static float rotZ = 0.0f;
    public static float transX = 0.5f;
    public static float transY = 0.67f;
    public static float transZ = 0.20f;
    public static float itemScale = 1f;
    public static float scale = 1f;


    private final ModelPart root;

    public WindStaffModelRenderer(ModelPart root) {
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create().uv(0, 6).cuboid(-1.5F, -16.5F, -1.5F, 3.0F, 14.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData arm_overlay_r1 = root.addChild("arm_overlay_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -1.0F, -1.0F, 8.0F, 3.0F, 3.0F, new Dilation(-0.3F)), ModelTransform.of(0.5F, -17.6F, -0.5F, 0.0F, 0.0F, -0.7854F));

        ModelPartData arm_overlay_r2 = root.addChild("arm_overlay_r2", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-4.0F, -1.5F, -1.5F, 8.0F, 3.0F, 3.0F, new Dilation(-0.3F)).mirrored(false), ModelTransform.of(-2.9749F, -19.3678F, 0.0F, 0.0F, 0.0F, 0.7854F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        VertexConsumer vertexConsumer = AnimatedTextureHelper.getBuffer(vertices, SPRITE_ID);
        this.root.render(matrices, vertexConsumer, light, overlay);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    }

    private void updateDummyAge() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
            dummy.age = (int) (client.world.getTime() % Integer.MAX_VALUE);
        } else {
            dummy.age = (int) ((System.currentTimeMillis() / 50L) % Integer.MAX_VALUE);
        }
    }

    public void renderItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float currentItemX, float currentItemY, float currentItemZ, float currentRotX, float currentRotZ, float currentItemScale) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);

        long time = System.currentTimeMillis() % 3600L;
        float angle = (time / 10.0f) % 360;
        matrices.translate(currentItemX, currentItemY, currentItemZ);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));
        matrices.scale(currentItemScale, currentItemScale, currentItemScale);

        EntityRendererFactory.Context context = new EntityRendererFactory.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderManager(), null, minecraft.getResourceManager(), minecraft.getEntityModelLoader(), minecraft.textRenderer);
        updateDummyAge();
        new WindChargeEntityRenderer(context).render(dummy, 0, tickDelta, matrices, vertexConsumers, light);

        matrices.pop();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float currentRotX = 0;
        float currentRotY = 0;
        float currentRotZ = 0;

        float currentTransX = 0;
        float currentTransY = 0;
        float currentTransZ = 0;

        float currentItemX = 0;
        float currentItemY = 0;
        float currentItemZ = 0;

        float currentItemScale = 0;
        float currentScale = 0;



        switch (mode) {
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                currentRotX = 27; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.67f; currentTransZ = 0.2f;
                currentItemX = 0.5f; currentItemY = 1.2f; currentItemZ = 0.85f;
                currentItemScale = 0.8f; currentScale = 1;
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                currentRotX = 32; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.12f; currentTransZ = 0.59f;
                currentItemX = 0.5f; currentItemY = 0.75f; currentItemZ = 1.15f;
                currentItemScale = 1; currentScale = 1;
            }
            case GUI -> {
                currentRotX = 45; currentRotY = 45; currentRotZ = 0;
                currentTransX = 0.75f; currentTransY = 1; currentTransZ = 0.2f;
                currentItemX = 0.3f; currentItemY = 1.8f; currentItemZ = 0;
                currentItemScale = 1; currentScale = 2;
            }
            case GROUND -> {
                currentRotX = 20; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.67f; currentTransZ = 0.2f;
                currentItemX = 0.5f; currentItemY = 1.7f; currentItemZ = 0.80f;
                currentItemScale = 0.9f; currentScale = 2;
            }
            case FIXED -> {
                currentRotX = 0; currentRotY = 0; currentRotZ = 0;
                currentTransX = 0.5f; currentTransY = 0.67f; currentTransZ = 0.5f;
                currentItemX = 0.5f; currentItemY = 1.5f; currentItemZ = 0.5f;
                currentItemScale = 1; currentScale = 1.3f;
            }
            case HEAD -> {
                currentRotX = 0f; currentRotY = 0f; currentRotZ = 0f;
                currentTransX = 0.5f; currentTransY = 1f; currentTransZ = 0.2f;
                currentItemX = 0.5f; currentItemY = 1.7f; currentItemZ = 0.2f;
                currentItemScale = 1; currentScale = 1;
            }
            default -> {}
        }

        float currentOverlayRotX = DragonStaffModelRenderer.rotX;
        float currentOverlayRotY = DragonStaffModelRenderer.rotY;
        float currentOverlayRotZ = DragonStaffModelRenderer.rotZ;

        float currentOverlayTransX = DragonStaffModelRenderer.transX;
        float currentOverlayTransY = DragonStaffModelRenderer.transY;
        float currentOverlayTransZ = DragonStaffModelRenderer.transZ;

        float currentOverlayScale = 0;

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


        matrices.push();
        var minecraft = MinecraftClient.getInstance();

        renderItem(stack, mode, matrices, vertexConsumers, light, overlay, currentItemX, currentItemY, currentItemZ, currentRotX, currentRotZ, currentItemScale);

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentRotY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));

        matrices.translate(currentTransX, currentTransY, currentTransZ);
        matrices.scale(currentScale, currentScale, currentScale);

        minecraft.getItemRenderer().renderItem(HANDLE, mode, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();


        matrices.push();
//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
//        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentRotY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));

        matrices.translate(currentTransX, currentTransY, currentTransZ);
        matrices.scale(currentScale, currentScale, currentScale);

        matrices.translate(currentOverlayTransX, currentOverlayTransY, currentOverlayTransZ);
        matrices.scale(currentOverlayScale, currentOverlayScale, currentOverlayScale);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentOverlayRotX));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(currentOverlayRotY));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentOverlayRotZ));
        renderModel(stack, matrices, vertexConsumers, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();

    }
}
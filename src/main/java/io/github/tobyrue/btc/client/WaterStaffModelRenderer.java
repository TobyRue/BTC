package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class WaterStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    public static final ItemStack WATER_BLAST = new ItemStack(ModItems.WATER_BLAST, 1);
    public static final Identifier TEXTURE = Identifier.of("btc", "textures/item/water_rods.png");
    private static final String ELEMENT1 = "element1";
    private static final String ELEMENT2 = "element2";
    private static final String ELEMENT3 = "element3";
    private static final String ELEMENT4 = "element4";

    private final DummyConduitBlockEntity dummy = new DummyConduitBlockEntity(BlockPos.ORIGIN, Blocks.CONDUIT.getDefaultState());

    private final ModelPart element1;
    private final ModelPart element2;
    private final ModelPart element3;
    private final ModelPart element4;
    private final ModelPart root;

    public WaterStaffModelRenderer(ModelPart root) {
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

    public ModelPart getPart1() { return this.element1; }
    public ModelPart getPart2() { return this.element2; }
    public ModelPart getPart3() { return this.element3; }
    public ModelPart getPart4() { return this.element4; }

    public void renderModel(ItemStack stack, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.root.render(matrices, vertices, light, overlay);
        MinecraftClient.getInstance().getTextureManager().bindTexture(TEXTURE);
    }

    public void renderItem(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float currentItemX, float currentItemY, float currentItemZ, float currentRotX, float currentRotZ, float currentItemScale) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = minecraft.getRenderTickCounter().getTickDelta(false);

        matrices.translate(currentItemX, currentItemY, currentItemZ);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(currentRotX));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(currentRotZ));

//        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
//        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));

        matrices.scale(currentItemScale, currentItemScale, currentItemScale);

        if (minecraft.world != null) {
            dummy.setWorld(minecraft.world);
            dummy.ticks = (int) (minecraft.world.getTime() % Integer.MAX_VALUE);
        } else {
            dummy.ticks = (int) ((System.currentTimeMillis() / 50L) % Integer.MAX_VALUE);
        }

        BlockEntityRenderDispatcher dispatcher = minecraft.getBlockEntityRenderDispatcher();
        BlockEntityRenderer<ConduitBlockEntity> renderer = dispatcher.get(dummy);

        if (renderer != null) {
            renderer.render(dummy, tickDelta, matrices, vertexConsumers, light, overlay);
        }

//        minecraft.getItemRenderer().renderItem(WATER_BLAST, ModelTransformationMode.GROUND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
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
                currentRotX = 27;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.2f;

                currentItemX = 0.25f;
                currentItemY = 1.1f;
                currentItemZ = 0.5f;

                currentItemScale = 0.5f;
                currentScale = 1;
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                currentRotX = 32;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.12f;
                currentTransZ = 0.59f;

                currentItemX = 0.25f;
                currentItemY = 0.6f;
                currentItemZ = 0.75f;

                currentItemScale = 0.5f;
                currentScale = 1;
            }
            case GUI -> {
                currentRotX = 45;
                currentRotY = 45;
                currentRotZ = 0;

                currentTransX = 0.75f;
                currentTransY = 1;
                currentTransZ = 0.2f;

                currentItemX = 1.1f;
                currentItemY = 1.6f;
                currentItemZ = 0.55f;

                currentItemScale = 0.65f;
                currentScale = 2;
            }
            case GROUND -> {
                currentRotX = 20;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.2f;

                currentItemX = 0.25f;
                currentItemY = 1.6f;
                currentItemZ = 0.55f;

                currentItemScale = 0.5f;
                currentScale = 2;
            }
            case FIXED -> {
                currentRotX = 0;
                currentRotY = 0;
                currentRotZ = 0;

                currentTransX = 0.5f;
                currentTransY = 0.67f;
                currentTransZ = 0.5f;

                currentItemX = 0.25f;
                currentItemY = 1.2f;
                currentItemZ = 0.2f;

                currentItemScale = 0.5f;
                currentScale = 1.3f;
            }
            case HEAD -> {
                currentRotX = 0f;
                currentRotY = 0f;
                currentRotZ = 0f;

                currentTransX = 0.5f;
                currentTransY = 1f;
                currentTransZ = 0.2f;

                currentItemX = 0.25f;
                currentItemY = 1.6f;
                currentItemZ = 0;

                currentItemScale = 0.5f;
                currentScale = 1;
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
    }
}
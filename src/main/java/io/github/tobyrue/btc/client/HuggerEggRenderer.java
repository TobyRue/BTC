package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.HuggerEggBlock;
import io.github.tobyrue.btc.block.entities.HuggerEggBlockEntity;
import io.github.tobyrue.btc.util.VectorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class HuggerEggRenderer implements BlockEntityRenderer<HuggerEggBlockEntity> {
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/hugger_egg.png");
    private static final float TURN_SPEED = 0.05f;
    private final ModelPart all;
    private final ModelPart egg;
    private final ModelPart growth_0;
    private final ModelPart growth_1;
    private final ModelPart growth_2;

    public HuggerEggRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart root = ctx.getLayerModelPart(BTCClient.HUGGER_EGG_LAYER);
        this.all = root.getChild("All");
        this.egg = this.all.getChild("Egg");
        this.growth_0 = this.all.getChild("growth_0");
        this.growth_1 = this.all.getChild("growth_1");
        this.growth_2 = this.all.getChild("growth_2");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData All = modelPartData.addChild("All", ModelPartBuilder.create(), ModelTransform.pivot(3.0F, 0.0F, -3.0F));

        ModelPartData Egg = All.addChild("Egg", ModelPartBuilder.create().uv(0, 0).cuboid(-7.0F, 0.0F, -1.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData growth_0 = All.addChild("growth_0", ModelPartBuilder.create().uv(24, 16).cuboid(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 4.0F, 3.0F));

        ModelPartData growth_1 = All.addChild("growth_1", ModelPartBuilder.create().uv(16, 16).cuboid(-1.0F, -0.25F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 20).cuboid(-1.0F, -1.25F, -1.0F, 2.0F, 1.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 3.25F, 3.0F));

        ModelPartData growth_2 = All.addChild("growth_2", ModelPartBuilder.create().uv(0, 24).cuboid(-2.0F, -1.75F, -2.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-3.0F, 2.75F, 3.0F));

        ModelPartData Body_r1 = growth_2.addChild("Body_r1", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 1.25F, 0.0F, 0.0F, 0.0F, -3.1416F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(HuggerEggBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        float yOffset = 0.0F;
        float shake = 0.0F;
        if (entity.getWorld() != null && MinecraftClient.getInstance().player != null) {
            yOffset = (float) (Math.sin((entity.getWorld().getTime() + tickDelta) / 12) / 64.0);
            shake = (float) (Math.sin((entity.getWorld().getTime() + tickDelta) / 4) * 2);
        }

        int growth = entity.getCachedState().get(HuggerEggBlock.GROWTH_LEVEL);

        matrices.push();

        matrices.translate(0.5F, 0, 0.5F);
        if (entity.getWorld() != null && MinecraftClient.getInstance().player != null) {
            double[] angles = VectorUtils.getPitchAndYaw(Vec3d.of(entity.getPos()).add(0.5, 0, 0.5), MinecraftClient.getInstance().player.getPos());
            float currentYaw = entity.getHuggerYaw();
            float desiredYaw = (float) angles[0];
            float yawDelta = MathHelper.wrapDegrees(desiredYaw - currentYaw);
            currentYaw += yawDelta * TURN_SPEED;
            entity.setHuggerYaw(currentYaw);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(shake + entity.getHuggerYaw() - 180));
        }
        matrices.translate(3/16F, 0.01F + yOffset, -3/16F);

        VertexConsumer solidConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        if (growth == 0) {
            this.growth_0.render(matrices, solidConsumer, light, overlay);
        } else if (growth == 1) {
            this.growth_1.render(matrices, solidConsumer, light, overlay);
        } else {
            this.growth_2.render(matrices, solidConsumer, light, overlay);
        }
        matrices.pop();

        matrices.push();
        matrices.translate(0.6875F, 0.01F, 0.3125F);
        VertexConsumer translucentConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE));
        this.egg.render(matrices, translucentConsumer, light, overlay);
        matrices.pop();
    }
}
package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;

import io.github.tobyrue.btc.block.entities.KeyDispenserBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.StaffItem;
import net.fabricmc.api.EnvType;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;


@Environment(EnvType.CLIENT)
public class TuffGolemRenderer extends MobEntityRenderer<TuffGolemEntity, TuffGolemEntityModel<TuffGolemEntity>> {

    private static final Identifier RED_EYES_OPEN = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_red_open.png");
    private static final Identifier RED_EYES_CLOSED = Identifier.of(BTC.MOD_ID, "textures/entity/tuff_golem_red_closed.png");

    public TuffGolemRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new TuffGolemEntityModel<>(ctx.getPart(ModModelLayers.TUFF_GOLEM)), 0.5f);
    }

    @Override
    public Identifier getTexture(TuffGolemEntity entity) {
        if (!entity.isSleeping()) {
            return RED_EYES_OPEN;
        } else {
            return RED_EYES_CLOSED;
        }
    }

    @Override
    public void render(TuffGolemEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        // Push the pose stack to start transformation
        matrixStack.push();

        // Check if the entity is holding an item
        ItemStack heldItem = livingEntity.getHeldItem();  // or getHeldItem() based on your entity's method

        // Only render the item if it's not empty
        if (!heldItem.isEmpty()) {
            matrixStack.push();
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((livingEntity.getHeadYaw() * -1) + 180));  // Rotate based on head yaw
            if (livingEntity.isSleeping()) {
                matrixStack.translate(0.0D, 0.7D, -0.5D);
            } else {
                matrixStack.translate(0.0D, 0.75D, -0.5D);
            }
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((livingEntity.getWorld().getTime() + f) * 4 + (0.25f * 360))); // Adjust rotation speed

            int lightAbove = WorldRenderer.getLightmapCoordinates(livingEntity.getWorld(), livingEntity.getBlockPos());

            MinecraftClient.getInstance().getItemRenderer().renderItem(heldItem, ModelTransformationMode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, livingEntity.getWorld(), 0);

            matrixStack.pop();
        }

        // Render the entity model
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);

        // Pop the pose stack after rendering
        matrixStack.pop();
    }}

package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

public class BonfireBlockEntityRenderer implements BlockEntityRenderer<BonfireBlockEntity> {
    private static ItemStack stack = new ItemStack(ModItems.STAFF, 1);

    public BonfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BonfireBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        var localPlayer = MinecraftClient.getInstance().player;
        if (localPlayer == null) return;

        if (entity.getActivatedBy().contains(localPlayer.getUuid())) {
            matrices.push();

            matrices.translate(0.5, 1.25, 0.5);

            float angle = (entity.getWorld().getTime() + tickDelta) * 4.0f;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));

            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.GROUND, 
                    light,
                    overlay,
                    matrices,
                    vertexConsumers,
                    entity.getWorld(),
                    0
            );
            matrices.pop();
        }
    }
}
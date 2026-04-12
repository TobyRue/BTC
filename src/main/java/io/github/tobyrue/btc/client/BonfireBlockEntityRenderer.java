package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;

public class BonfireBlockEntityRenderer implements BlockEntityRenderer<BonfireBlockEntity> {
    public BonfireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(BonfireBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

    }
}
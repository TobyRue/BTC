package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.FanBlock;
import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Direction;

public class FanBlockEntityRenderer implements BlockEntityRenderer<FanBlockEntity> {
    private final FanBlockModel model;
    private static final Identifier TEXTURE = BTC.identifierOf("textures/block/fan_blades.png");

    public FanBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new FanBlockModel(ctx.getLayerModelPart(ModModelLayers.FAN_BLADES_LAYER));
    }

    @Override
    public void render(FanBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();



        Direction facing = entity.getCachedState().get(FanBlock.FACING);

        matrices.translate(0.5, 0.5, 0.5);

        switch (facing) {
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case WEST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case EAST  -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
            case UP    -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            case DOWN  -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            case NORTH -> {}
        }
        matrices.translate(-0.5, -0.5, -0.5);


        matrices.translate(0, 0, -0.3);

        float renderRotation = entity.visualRotation + (entity.getFanSpeed() * tickDelta);

        boolean isPulling = entity.getCachedState().get(FanBlock.MODE) == FanBlock.FanMode.PULL;
        model.setRotation(isPulling ? -renderRotation : renderRotation);

        int lightAbove = net.minecraft.client.render.WorldRenderer.getLightmapCoordinates(
                entity.getWorld(),
                entity.getPos().offset(entity.getCachedState().get(FanBlock.FACING))
        );

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        this.model.render(matrices, vertexConsumer, lightAbove, overlay, 0xFFFFFFFF);
        matrices.pop();
    }
}
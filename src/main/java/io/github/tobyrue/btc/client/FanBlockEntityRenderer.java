package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.WaxedCopperFanBlock;
import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.block.entities.FanBlockEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Direction;

public class FanBlockEntityRenderer implements BlockEntityRenderer<FanBlockEntity> {
    private final FanBlockModel model;
    private static final Identifier COPPER = BTC.identifierOf("textures/block/copper_fan_blades.png");
    private static final Identifier EXPOSED = BTC.identifierOf("textures/block/exposed_copper_fan_blades.png");
    private static final Identifier WEATHERED = BTC.identifierOf("textures/block/weathered_copper_fan_blades.png");
    private static final Identifier OXIDIZED = BTC.identifierOf("textures/block/oxidized_copper_fan_blades.png");

    public FanBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new FanBlockModel(ctx.getLayerModelPart(ModModelLayers.FAN_BLADES_LAYER));
    }

    private static Identifier getTexture(BlockState state) {
        return switch (state.getBlock()) {
            case Block block when state.isOf(ModBlocks.COPPER_TRIAL_FAN) || state.isOf(ModBlocks.WAXED_COPPER_TRIAL_FAN) -> COPPER;
            case Block block when state.isOf(ModBlocks.EXPOSED_COPPER_TRIAL_FAN) || state.isOf(ModBlocks.WAXED_EXPOSED_COPPER_TRIAL_FAN) -> EXPOSED;
            case Block block when state.isOf(ModBlocks.WEATHERED_COPPER_TRIAL_FAN) || state.isOf(ModBlocks.WAXED_WEATHERED_COPPER_TRIAL_FAN) -> WEATHERED;
            case Block block when state.isOf(ModBlocks.OXIDIZED_COPPER_TRIAL_FAN) || state.isOf(ModBlocks.WAXED_OXIDIZED_COPPER_TRIAL_FAN) -> OXIDIZED;
            default -> throw new IllegalStateException("Unexpected value: " + state.getBlock());
        };
    }

    @Override
    public void render(FanBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        Direction facing = entity.getCachedState().get(WaxedCopperFanBlock.FACING);

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

        boolean isPulling = entity.getCachedState().get(WaxedCopperFanBlock.MODE) == WaxedCopperFanBlock.FanMode.PULL;
        model.setRotation(isPulling ? -renderRotation : renderRotation);

        int lightAbove = WorldRenderer.getLightmapCoordinates(
                entity.getWorld(),
                entity.getPos().offset(entity.getCachedState().get(WaxedCopperFanBlock.FACING))
        );

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(getTexture(entity.getCachedState())));
        this.model.render(matrices, vertexConsumer, lightAbove, overlay, 0xFFFFFFFF);
        matrices.pop();
    }
}
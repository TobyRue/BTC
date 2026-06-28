package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.SpyGlassBlock;
import io.github.tobyrue.btc.block.entities.SpyGlassBlockEntity;
import io.github.tobyrue.btc.regestries.ModModelLayers;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SpyGlassBlockEntityRenderer implements BlockEntityRenderer<SpyGlassBlockEntity> {
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/spy_glass_block.png");
    private final SpyGlassBlockModel model;
    private final ItemRenderer itemRenderer;
    private final ItemStack spyglassStack = new ItemStack(Items.SPYGLASS);

    public SpyGlassBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new SpyGlassBlockModel(ctx.getLayerModelPart(ModModelLayers.SPY_GLASS_BLOCK_LAYER));
        this.itemRenderer = ctx.getItemRenderer();
    }



    @Override
    public void render(SpyGlassBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        if (!(state.getBlock() instanceof SpyGlassBlock)) return;

        Direction facing = state.get(SpyGlassBlock.FACING);
        BlockFace location = state.get(SpyGlassBlock.FACE);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        switch (location) {
            case WALL -> {
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                switch (facing) {
                    case SOUTH -> {
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
                    }
                    case WEST  -> {
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(270));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                    }
                    case EAST  -> {
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90));
                    }
                    case NORTH -> {
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
                    }
                }
            }
            case FLOOR  -> {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
                switch (facing) {
                    case SOUTH, NORTH -> {}
                    case WEST, EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
                }
            }
            case CEILING -> {}
        }

        matrices.translate(0, -1, 0);

        float facingDegrees = -facing.asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(facingDegrees));

        this.model.axis_1.yaw = (float) Math.toRadians(entity.getYaw());

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE));
        this.model.render(matrices, consumer, light, overlay, 0xFFFFFFFF);

        matrices.push();

        matrices.translate(0, 0.975, 0);

        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        if (location == BlockFace.FLOOR) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        }

        if (location == BlockFace.FLOOR) {
            matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(entity.getYaw()));
        } else {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getYaw()));
        }
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));


        matrices.scale(0.8F, 0.8F, 0.8F);

        this.itemRenderer.renderItem(
                spyglassStack,
                ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
                light,
                overlay,
                matrices,
                vertexConsumers,
                MinecraftClient.getInstance().world,
                0
        );

        matrices.pop();
        matrices.pop();
    }
}
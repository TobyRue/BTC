package io.github.tobyrue.btc.wires;

import io.github.tobyrue.btc.BTC;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class WireBlockEntityRenderer implements BlockEntityRenderer<WireBlockEntity> {

    private static final SpriteIdentifier[] SPRITE_IDS = new SpriteIdentifier[]{
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_base")),              // 0
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_overlay")),           // 1
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection")),        // 2
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_overlay")),// 3
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_input")),  // 4
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_output")), // 5
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_input")),             // 6
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_output")),            // 7
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_operator_overlay")),  // 8
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_none_overlay")),      // 9
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_redstone_input")),    // 10
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_redstone_output")),   // 11
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_redstone_input")), // 12
            new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, BTC.identifierOf("block/wire_connection_redstone_output")) // 13
    };

    public WireBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(WireBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        if (world == null) return;

        BlockPos pos = entity.getPos();


        BlockState state = entity.getCachedState();
        boolean isPowered = state.get(WireBlock.POWERED);
        int powerColor = isPowered ? 0xFFE50000 : 0xFF990000;
        int white = 0xFFFFFFFF;

        for (Direction face : Direction.values()) {
            matrices.push();

            applyFaceRotation(matrices, face);

            renderFace(matrices, vertexConsumers, SPRITE_IDS[0], white, light, overlay, getLayer(0), face);
            renderFace(matrices, vertexConsumers, SPRITE_IDS[1], powerColor, light, overlay, getLayer(1), face);

            WireBlock.ConnectionType conn = entity.getConnection(face);
            if (conn == WireBlock.ConnectionType.INPUT) {
                renderFace(matrices, vertexConsumers, SPRITE_IDS[6], white, light, overlay, getLayer(0), face.getAxis() == Direction.Axis.Y ? face.getOpposite() : face /*THIS DID NOTHING*/);
            }
            else if (conn == WireBlock.ConnectionType.OUTPUT) {
                renderFace(matrices, vertexConsumers, SPRITE_IDS[7], white, light, overlay, getLayer(1), face.getAxis() == Direction.Axis.Y ? face.getOpposite() : face);
            }
            else if (conn == WireBlock.ConnectionType.REDSTONE_INPUT) {
                renderFace(matrices, vertexConsumers, SPRITE_IDS[10], white, light, overlay, getLayer(1), face.getAxis() == Direction.Axis.Y ? face.getOpposite() : face);
            }
            else if (conn == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                renderFace(matrices, vertexConsumers, SPRITE_IDS[11], white, light, overlay, getLayer(1), face.getAxis() == Direction.Axis.Y ? face.getOpposite() : face);
            }

            for (Direction connectionDir : Direction.values()) {
                if (connectionDir.getAxis() == face.getAxis()) continue;
                WireBlock.ConnectionType adjConn = entity.getConnection(connectionDir);

                matrices.push();
                matrices.translate(0.5, 0.5, 0);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getSubRotation(face, connectionDir)));
                matrices.translate(-0.5, -0.5, 0);

                if (adjConn != WireBlock.ConnectionType.NONE) {
                    renderFace(matrices, vertexConsumers, SPRITE_IDS[3], powerColor, light, overlay, getLayer(1), face);
                    if (adjConn == WireBlock.ConnectionType.INPUT) {
                        renderFace(matrices, vertexConsumers, SPRITE_IDS[4], white, light, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.OUTPUT) {
                        renderFace(matrices, vertexConsumers, SPRITE_IDS[5], white, light, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.REDSTONE_INPUT) {
                        renderFace(matrices, vertexConsumers, SPRITE_IDS[12], white, light, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                        renderFace(matrices, vertexConsumers, SPRITE_IDS[13], white, light, overlay, getLayer(1), face);
                    }
                } else {
                    renderFace(matrices, vertexConsumers, SPRITE_IDS[9], powerColor, light, overlay, getLayer(1), face);
                }
                matrices.pop();
            }

            int opColor = entity.getOperator().getColor() | 0xFF000000;
            if (!(Direction.stream().filter(d -> entity.getConnection(d) == WireBlock.ConnectionType.INPUT).count() == 1 && entity.getOperator() == WireBlock.Operator.OR)) {
                renderFace(matrices, vertexConsumers, SPRITE_IDS[8], opColor, light, overlay, getLayer(1), face);
            }

            matrices.pop();
        }
    }

    private float getLayer(int layer) {
        return layer == 0 ? 0f : -0.0001f * layer;
    }

    private void renderFace(MatrixStack matrices, VertexConsumerProvider consumers, SpriteIdentifier spriteId, int color, int light, int overlay, float z, Direction face) {
        Sprite sprite = spriteId.getSprite();
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getEntityCutout(spriteId.getAtlasId()));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        float a = ((color >> 24) & 0xFF) / 255.0f;

        buffer.vertex(matrix, 0, 1, z).color(r, g, b, a).texture(sprite.getMinU(), sprite.getMinV()).overlay(overlay).light(light).normal(0, 0, 1);
        buffer.vertex(matrix, 1, 1, z).color(r, g, b, a).texture(sprite.getMaxU(), sprite.getMinV()).overlay(overlay).light(light).normal(0, 0, 1);
        buffer.vertex(matrix, 1, 0, z).color(r, g, b, a).texture(sprite.getMaxU(), sprite.getMaxV()).overlay(overlay).light(light).normal(0, 0, 1);
        buffer.vertex(matrix, 0, 0, z).color(r, g, b, a).texture(sprite.getMinU(), sprite.getMaxV()).overlay(overlay).light(light).normal(0, 0, 1);
    }

    private void applyFaceRotation(MatrixStack matrices, Direction face) {
        matrices.translate(0.5, 0.5, 0.5);
        switch (face) {
            case DOWN -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            case UP -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            case NORTH -> {}
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
        }
        matrices.translate(-0.5, -0.5, -0.5);
    }

    private float getSubRotation(Direction face, Direction to) {
        if (face.getAxis() == Direction.Axis.Y) {
            boolean isUp = face == Direction.UP;
            return switch (to) {
                case NORTH -> isUp ? 0 : 180;
                case SOUTH -> isUp ? 180 : 0;
                case EAST -> isUp ? 90 : 270;
                case WEST -> isUp ? 270 : 90;
                default -> 0;
            };
        }
        return switch (to) {
            case UP -> 0;
            case DOWN -> 180;
            case NORTH, SOUTH, EAST, WEST -> (to.getHorizontal() - face.getHorizontal() + 4) % 4 == 1 ? 270 : 90;
            default -> 0;
        };
    }
}
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
        var pos = entity.getPos();
        if (world == null) return;



        BlockState state = entity.getCachedState();
        boolean isPowered = state.get(WireBlock.POWERED);
        int powerColor = isPowered ? 0xFFE50000 : 0xFF990000;
        int white = 0xFFFFFFFF;

        for (Direction face : Direction.values()) {
            matrices.push();

            applyFaceRotation(matrices, face);

            renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[0], white, overlay, getLayer(0), face);
            renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[1], powerColor, overlay, getLayer(1), face);

            WireBlock.ConnectionType conn = entity.getConnection(face, world, state, pos);
            if (conn == WireBlock.ConnectionType.INPUT) {
                renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[6], white, overlay, getLayer(1), face);
            }
            else if (conn == WireBlock.ConnectionType.OUTPUT) {
                renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[7], white, overlay, getLayer(1), face);
            }
            else if (conn == WireBlock.ConnectionType.REDSTONE_INPUT) {
                renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[10], white, overlay, getLayer(1), face);
            }
            else if (conn == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[11], white, overlay, getLayer(1), face);
            }

            for (Direction connectionDir : Direction.values()) {
                if (connectionDir.getAxis() == face.getAxis()) continue;
                WireBlock.ConnectionType adjConn = entity.getConnection(connectionDir, world, state, pos);

                matrices.push();
                matrices.translate(0.5, 0.5, 0);
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(getSubRotation(face, connectionDir)));
                matrices.translate(-0.5, -0.5, 0);

                if (adjConn != WireBlock.ConnectionType.NONE) {
                    renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[3], powerColor, overlay, getLayer(1), face);
                    if (adjConn == WireBlock.ConnectionType.INPUT) {
                        renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[4], white, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.OUTPUT) {
                        renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[5], white, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.REDSTONE_INPUT) {
                        renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[12], white, overlay, getLayer(1), face);
                    }
                    else if (adjConn == WireBlock.ConnectionType.REDSTONE_OUTPUT) {
                        renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[13], white, overlay, getLayer(1), face);
                    }
                } else if (entity.getConnection(face, world, state, pos) != WireBlock.ConnectionType.NONE) {
                    renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[9], powerColor, overlay, getLayer(1), face);
                }
                matrices.pop();
            }

            int opColor = entity.getOperator(world, state, pos).getColor() | 0xFF000000;
            if (!(Direction.stream().filter(d -> entity.getConnection(d, world, state, pos) == WireBlock.ConnectionType.INPUT).count() == 1 && entity.getOperator(world, state, pos) == WireBlock.Operator.OR)) {
                renderFace(entity, matrices, vertexConsumers, SPRITE_IDS[8], opColor, overlay, getLayer(1), face);
            }

            matrices.pop();
        }
    }

    private float getLayer(int layer) {
        return layer == 0 ? 0f : -0.0005f * layer;
    }

    private void renderFace(WireBlockEntity entity, MatrixStack matrices, VertexConsumerProvider consumers, SpriteIdentifier spriteId, int color, int overlay, float z, Direction face) {
        Sprite sprite = spriteId.getSprite();
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getEntityCutout(spriteId.getAtlasId()));
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().offset(face, 1));

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
            case DOWN ->  {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            }
            case UP -> {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            }
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
                case NORTH -> isUp ? 180 : 0;
                case SOUTH -> isUp ? 0 : 180;
                case EAST -> isUp ? 270 : 90;
                case WEST -> isUp ? 90 : 270;
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
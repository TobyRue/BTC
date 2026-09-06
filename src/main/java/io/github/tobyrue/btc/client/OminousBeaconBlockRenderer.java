package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity;
import io.github.tobyrue.btc.block.entities.OminousBeaconBlockEntity.BeamSegmentPath;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public class OminousBeaconBlockRenderer implements BlockEntityRenderer<OminousBeaconBlockEntity> {

    public static final Identifier OMINOUS_BEAM_TEXTURE = Identifier.of(BTC.MOD_ID, "textures/entity/ominous_beacon_beam.png");

    public OminousBeaconBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(OminousBeaconBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        List<BeamSegmentPath> paths = entity.getBeamPaths();
        if (paths.isEmpty()) return;

        long time = entity.getWorld().getTime();

        for (BeamSegmentPath path : paths) {
            matrices.push();

            double xOffset = path.getStartPos().getX() - entity.getPos().getX();
            double yOffset = path.getStartPos().getY() - entity.getPos().getY();
            double zOffset = path.getStartPos().getZ() - entity.getPos().getZ();

            matrices.translate(xOffset, yOffset, zOffset);

            Direction facing = path.getDirection();
            switch (facing) {
                case DOWN -> {
                    matrices.translate(0.5, 0.5, 0.5);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
                    matrices.translate(-0.5, -0.5, -0.5);
                }
                case NORTH -> {
                    matrices.translate(0.5, 0.5, 0.5);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
                    matrices.translate(-0.5, -0.5, -0.5);
                }
                case SOUTH -> {
                    matrices.translate(0.5, 0.5, 0.5);
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                    matrices.translate(-0.5, -0.5, -0.5);
                }
                case WEST -> {
                    matrices.translate(0.5, 0.5, 0.5);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0f));
                    matrices.translate(-0.5, -0.5, -0.5);
                }
                case EAST -> {
                    matrices.translate(0.5, 0.5, 0.5);
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-90.0f));
                    matrices.translate(-0.5, -0.5, -0.5);
                }
                case UP -> { }
            }

            renderBeam(matrices, vertexConsumers, tickDelta, time, 0, path.getLength(), path.getSegment().getColor());

            matrices.pop();
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, int color) {
        renderBeam(matrices, vertexConsumers, OMINOUS_BEAM_TEXTURE, tickDelta, 1.0F, worldTime, yOffset, maxY, color, 0.2F, 0.25F);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, int color, float innerRadius, float outerRadius) {
        int i = yOffset + maxY;
        matrices.push();

        matrices.translate(0.5, 0.0, 0.5);

        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2F - (float) MathHelper.floor(g * 0.1F));

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 2.25F - 45.0F));

        float k = innerRadius;
        float l = innerRadius;
        float n = -innerRadius;
        float q = -innerRadius;
        float t = -1.0F + h;
        float u = (float)maxY * heightScale * (0.5F / innerRadius) + t;

        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), color, yOffset, i, 0.0F, k, l, 0.0F, n, 0.0F, 0.0F, q, 0.0F, 1.0F, u, t);
        matrices.pop();

        float j = -outerRadius;
        k = -outerRadius;
        l = outerRadius;
        float m = -outerRadius;
        n = -outerRadius;
        float o = outerRadius;
        float p = outerRadius;
        q = outerRadius;
        t = -1.0F + h;
        u = (float)maxY * heightScale + t;

        renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), ColorHelper.Argb.withAlpha(32, color), yOffset, i, j, k, l, m, n, o, p, q, 0.0F, 1.0F, u, t);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        renderBeamFace(entry, vertices, color, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        renderBeamFace(entry, vertices, color, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        renderBeamFace(entry, vertices, color, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        renderBeamFace(entry, vertices, color, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int yOffset, int height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        renderBeamVertex(matrix, vertices, color, height, x1, z1, u2, v1);
        renderBeamVertex(matrix, vertices, color, yOffset, x1, z1, u2, v2);
        renderBeamVertex(matrix, vertices, color, yOffset, x2, z2, u1, v2);
        renderBeamVertex(matrix, vertices, color, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(MatrixStack.Entry matrix, VertexConsumer vertices, int color, int y, float x, float z, float u, float v) {
        vertices.vertex(matrix, x, (float)y, z).color(color).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public boolean rendersOutsideBoundingBox(OminousBeaconBlockEntity ominousBeaconBlockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    @Override
    public boolean isInRenderDistance(OminousBeaconBlockEntity ominousBeaconBlockEntity, Vec3d vec3d) {
        return Vec3d.ofCenter(ominousBeaconBlockEntity.getPos()).multiply(1.0, 0.0, 1.0).isInRange(vec3d.multiply(1.0, 0.0, 1.0), (double)this.getRenderDistance());
    }
}
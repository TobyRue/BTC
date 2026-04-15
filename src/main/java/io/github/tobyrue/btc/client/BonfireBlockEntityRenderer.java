package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.BonfireBlockEntity;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.util.BonfirePlayerData;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
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

        NbtCompound bonfireData = ((BonfirePlayerData) localPlayer).bTC$getBonfireData();

        if (bonfireData != null && bonfireData.contains("pos")) {
            BlockPos savedPos = BlockPos.fromLong(bonfireData.getLong("pos"));

            if (savedPos.equals(entity.getPos())) {

                matrices.push();

                matrices.translate(0.5, 0.1, 0.5);

                matrices.scale(0.5f, 0.5f, 0.5f);

                matrices.translate(-0.5, 0, -0.5);

                int fireLight = 16711935;

                MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
                        Blocks.FIRE.getDefaultState(),
                        matrices,
                        vertexConsumers,
                        fireLight,
                        OverlayTexture.DEFAULT_UV
                );

                matrices.pop();

                if (entity.getWorld().random.nextFloat() < 0.15f) {
                    entity.getWorld().addParticle(
                            net.minecraft.particle.ParticleTypes.FLAME,
                            entity.getPos().getX() + 0.5 + (entity.getWorld().random.nextGaussian() * 0.1),
                            entity.getPos().getY() + 0.3,
                            entity.getPos().getZ() + 0.5 + (entity.getWorld().random.nextGaussian() * 0.1),
                            0, 0.03, 0
                    );
                }
            }
        }

    }
    private void spawnPrivateParticles(BonfireBlockEntity entity) {
        if (entity.getWorld().random.nextFloat() < 0.2f) {
            double px = entity.getPos().getX() + 0.5 + (entity.getWorld().random.nextDouble() - 0.5) * 0.4;
            double py = entity.getPos().getY() + 0.3;
            double pz = entity.getPos().getZ() + 0.5 + (entity.getWorld().random.nextDouble() - 0.5) * 0.4;

            entity.getWorld().addParticle(net.minecraft.particle.ParticleTypes.FLAME, px, py, pz, 0, 0.04, 0);

            if (entity.getWorld().random.nextFloat() < 0.1f) {
                entity.getWorld().addParticle(net.minecraft.particle.ParticleTypes.LARGE_SMOKE, px, py + 0.2, pz, 0, 0.02, 0);
            }
        }
    }
}
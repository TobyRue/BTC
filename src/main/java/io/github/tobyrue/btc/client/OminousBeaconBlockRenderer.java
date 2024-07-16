package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.OminousBeaconBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class OminousBeaconBlockRenderer {
    @Environment(EnvType.CLIENT)
    public class OminousBeaconBlockEntityRenderer implements BlockEntityRenderer<OminousBeaconBlockEntity> {

        public OminousBeaconBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        }

        @Override
        public void render(OminousBeaconBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        }
    }
}

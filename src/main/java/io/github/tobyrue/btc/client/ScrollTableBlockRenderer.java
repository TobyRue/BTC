package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.block.entities.ScrollTableBlockEntity;
import io.github.tobyrue.btc.block.entities.ScrollTableBlockEntity;
import io.github.tobyrue.btc.util.VectorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ScrollTableBlockRenderer implements BlockEntityRenderer<ScrollTableBlockEntity> {
    public ScrollTableBlockRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(
            ScrollTableBlockEntity entity,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay
    ) {
        var world = entity.getWorld();
        if (world == null) return;

        matrices.push();
        for (int i = 0; i < 9; i++) {
//            if (!MinecraftClient.getInstance().isPaused()) {
//                System.out.println(entity.getStack(i));
//            }
            renderItem(entity, entity.getStack(i), tickDelta, matrices, vertexConsumers, light, overlay, 0, i);
        }
        matrices.pop();

    }

    public void renderItem(
            ScrollTableBlockEntity blockEntity,
            ItemStack stack,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            int overlay,
            float dTheta,
            int slot
    ) {
        matrices.push();
        if (blockEntity.getWorld() != null) {

            switch (slot) {
                case 0 -> matrices.translate((double)5/16, 0, (double)11/16);
                case 1 -> matrices.translate((double)4/16, 0, (double)8/16);
                case 2 -> matrices.translate((double)5/16, 0, (double)5/16);
                case 3 -> matrices.translate((double)8/16, 0, (double)12/16);
                case 4 -> matrices.translate((double)8/16, 0, (double)8/16);
                case 5 -> matrices.translate((double)8/16, 0, (double)4/16);
                case 6 -> matrices.translate((double)11/16, 0, (double)11/16);
                case 7 -> matrices.translate((double)12/16, 0, (double)8/16);
                case 8 -> matrices.translate((double)11/16, 0, (double)5/16);
                default -> matrices.translate(0, 0, 0);
            }

            matrices.translate(0,0.875,0);
            matrices.multiply(
                    RotationAxis.POSITIVE_X.rotationDegrees(
                            90
                    )
            );
            matrices.multiply(
                    RotationAxis.POSITIVE_Z.rotationDegrees(
                            90
                    )
            );
            int lightAbove = WorldRenderer.getLightmapCoordinates(
                    blockEntity.getWorld(),
                    blockEntity.getPos().up()
            );
            matrices.scale((float) 3 /16, (float) 3 /16, (float) 3 /16);
            MinecraftClient.getInstance().getItemRenderer().renderItem(
                    stack,
                    ModelTransformationMode.GUI,
                    lightAbove,
                    OverlayTexture.DEFAULT_UV,
                    matrices,
                    vertexConsumers,
                    blockEntity.getWorld(),
                    0
            );

            matrices.pop();
        }
    }
}



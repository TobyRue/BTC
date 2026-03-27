package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.block.entities.ObsidianChestBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class ObsidianChestRenderer implements BlockEntityRenderer<ObsidianChestBlockEntity> {
    private static final Identifier TEXTURE = BTC.identifierOf("textures/entity/obsidian_chest.png");
    private static final Identifier GLOW_TEXTURE = BTC.identifierOf("textures/entity/obsidian_chest_glow.png");
    private static final Identifier LOOTED_TEXTURE = BTC.identifierOf("textures/entity/obsidian_chest_looted.png");
    public static final int MAX_LIGHT = 15728880;
    private static final float MIN_BRIGHTNESS = 0.75f;
    private static final float MAX_BRIGHTNESS = 1.0f;
    private static final float CORE_BRIGHTNESS_MULTIPLIER = 0.75f;
    private static final float PULSE_SPEED = 0.1f;     // lower = slower pulse
    private final ModelPart lid;
    private final ModelPart base;
    private final ModelPart latch;

    public ObsidianChestRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart root = ctx.getLayerModelPart(EntityModelLayers.CHEST);
        this.base = root.getChild("bottom");
        this.lid = root.getChild("lid");
        this.latch = root.getChild("lock");
    }

    @Override
    public void render(ObsidianChestBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        BlockState state = entity.getCachedState();
        float rotation = state.get(ChestBlock.FACING).asRotation();
        float time = (Objects.requireNonNull(entity.getWorld()).getTime() + tickDelta);
        float wave = (float)((Math.cos(time * PULSE_SPEED) + 1.0) / 2.0);
        float pulse = MIN_BRIGHTNESS + (MAX_BRIGHTNESS - MIN_BRIGHTNESS) * wave;
        MinecraftClient client = MinecraftClient.getInstance();
        assert client.player != null;
        UUID playerUUID = client.player.getUuid();
        boolean opened = playerUUID != null && entity.hasPlayerLooted(playerUUID);
        int baseColor = getColor(pulse * CORE_BRIGHTNESS_MULTIPLIER);

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        float openFactor = entity.getAnimationProgress(tickDelta);
        openFactor = 1.0f - openFactor;
        openFactor = 1.0f - openFactor * openFactor * openFactor;

        lid.pitch = -(openFactor * 1.5707964f);
        latch.pitch = lid.pitch;
        VertexConsumer baseConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE));
        this.renderParts(matrices, baseConsumer, light, overlay, baseColor);



        int color = getColor(pulse);
        VertexConsumer glowConsumer;
        if (opened) {
            glowConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(LOOTED_TEXTURE));
        } else {
            glowConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(GLOW_TEXTURE));
        }

        renderParts(matrices, glowConsumer, MAX_LIGHT, overlay, color);
        matrices.pop();
    }

    private static int getColor(float pulse) {
        int r = 255;
        int g = 255;
        int b = 255;

        r = (int)(r * pulse);
        g = (int)(g * pulse);
        b = (int)(b * pulse);

        r = Math.min(255, r);
        g = Math.min(255, g);
        b = Math.min(255, b);

        return (255 << 24) | (r << 16) | (g << 8) | b;
    }

    private void renderParts(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        lid.render(matrices, vertices, light, overlay);
        latch.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }
    private void renderParts(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        lid.render(matrices, vertices, light, overlay, color);
        latch.render(matrices, vertices, light, overlay, color);
        base.render(matrices, vertices, light, overlay, color);
    }
}
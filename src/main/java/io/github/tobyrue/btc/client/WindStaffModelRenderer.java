package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.loader.impl.game.minecraft.launchwrapper.FabricClientTweaker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShieldDecorationRecipe;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {



    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        /* var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        //double offset = Math.sin((MinecraftClient.getInstance().world.getTime() + tickDelta) / 8.0) / 8.0;

        // Move the item
        matrices.translate(0.5, 1.3, 0.5);

        // Rotate the item
        //matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((MinecraftClient.getInstance().world.getTime() + tickDelta) * 4));
        int lightAbove = WorldRenderer.getLightmapCoordinates(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getBlockPos().up());

        // Render the Wind Charge entity
        WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, MinecraftClient.getInstance().world);
        MinecraftClient.getInstance().getEntityRenderDispatcher().render(windChargeEntity, 0, 0, 0, 0.0f, tickDelta, matrices, vertexConsumers, lightAbove);
        // Mandatory call after GL calls
        */
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);

        int lightAbove = WorldRenderer.getLightmapCoordinates(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getBlockPos().up());

        matrices.pop();
    }
}
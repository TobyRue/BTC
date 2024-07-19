package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);


    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
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

        String name = stack.getName().getLiteralString();
        float x = 0, y = 0, z = 0;
        try {
            if(name.startsWith("@")) {
                var t = name.substring(1).split(",");
                x = Float.parseFloat(t[0].trim());
                y = Float.parseFloat(t[1].trim());
                z = Float.parseFloat(t[2].trim());
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }


        matrices.translate(x, y, z);
        minecraft.getItemRenderer().renderItem(HANDLE, mode, light, overlay, matrices, vertexConsumers, minecraft.world, 0);

        matrices.pop();
    }
}
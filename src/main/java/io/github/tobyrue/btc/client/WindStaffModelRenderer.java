package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.TextureManager;
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
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    private static WindChargeEntity windChargeEntity = null;

    public void renderWind(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        //double offset = Math.sin((MinecraftClient.getInstance().world.getTime() + tickDelta) / 8.0) / 8.0;
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
        // Move the item
        matrices.translate(x, y, z);

        // Rotate the item
        int lightAbove = WorldRenderer.getLightmapCoordinates(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getBlockPos().up());

        var dummy = new DummyWindCharge();

        new WindChargeEntityRenderer().render(dummy, 0, tickDelta, matrices, vertexConsumers, light);

        // Render the Wind Charge entity
        //WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, minecraft.world);
        //minecraft.getEntityRenderDispatcher().render(windChargeEntity, 0, 0, 0, 0.0f, tickDelta, matrices, vertexConsumers, lightAbove);

        // Mandatory call after GL calls
        matrices.pop();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        //double offset = Math.sin((MinecraftClient.getInstance().world.getTime() + tickDelta) / 8.0) / 8.0;

        renderWind(stack, mode, matrices, vertexConsumers, light, overlay);

        // Move the item
        matrices.translate(0.5, 1.3, 0.5);

        // Rotate the item
        int lightAbove = WorldRenderer.getLightmapCoordinates(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getBlockPos().up());

        // Render the Wind Charge entity
        WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, minecraft.world);
        minecraft.getEntityRenderDispatcher().render(windChargeEntity, 0, 0, 0, 0.0f, tickDelta, matrices, vertexConsumers, lightAbove);

        // Mandatory call after GL calls

        /*String name = stack.getName().getLiteralString();
        float x = 0, y = 0, z = 0, ry = 0, rz = 0, rx = 0;
        try {
            if(name.startsWith("@")) {
                var t = name.substring(1).split(",");
                x = Float.parseFloat(t[0].trim());
                y = Float.parseFloat(t[1].trim());
                z = Float.parseFloat(t[2].trim());
                rx = Float.parseFloat(t[3].trim());
                ry = Float.parseFloat(t[4].trim());
                rz = Float.parseFloat(t[5].trim());
            }
        } catch(Throwable e) {
            e.printStackTrace();
        }*/

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20));

        matrices.translate(0, -0.8, 0.15);
        minecraft.getItemRenderer().renderItem(HANDLE, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, light, overlay, matrices, vertexConsumers, minecraft.world, 0);
        matrices.pop();
    }
}
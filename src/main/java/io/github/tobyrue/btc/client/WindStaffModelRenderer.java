package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.WindChargeEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

import static io.github.tobyrue.btc.client.BTCClient.WIND_STAFF_LAYER;

@Environment(EnvType.CLIENT)
public class WindStaffModelRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    public static final ItemStack HANDLE = new ItemStack(ModItems.STAFF, 1);
    private static final DummyWindCharge dummy = new DummyWindCharge(); // Static variable
    private static int renderCounter = 0; // Counter to slow down age update
    public static final Identifier TEXTURE = Identifier.of("textures/entity/breeze_rods.png");
    private final ModelPart element1;
    private final ModelPart element2;
    private final ModelPart element3;
    private final ModelPart element4;
    private final ModelPart root;

    public WindStaffModelRenderer(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
        this.element1 = root.getChild("element1");
        this.element2 = root.getChild("element2");
        this.element3 = root.getChild("element3");
        this.element4 = root.getChild("element4");
    }
    private void updateDummy() {
        // Increment the age of the dummy entity every 10 render calls
        if (renderCounter % 10 == 0) {
            dummy.age++;
        }
        renderCounter++;
    }


    public void renderWind(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        var tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);
        EntityRendererFactory.Context context = new EntityRendererFactory.Context(minecraft.getEntityRenderDispatcher(), minecraft.getItemRenderer(), minecraft.getBlockRenderManager(), null, minecraft.getResourceManager(), minecraft.getEntityModelLoader(), minecraft.textRenderer
        );

        /*String name = stack.getName().getLiteralString();
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
        }*/
        // Move the item
        matrices.translate(0.5, 1.5, 0.2);

        // Rotate the item

        updateDummy();
        new WindChargeEntityRenderer(context).render(dummy, 0, tickDelta, matrices, vertexConsumers, light);

        // Render the Wind Charge entity
        //WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, minecraft.world);
        //minecraft.getEntityRenderDispatcher().render(windChargeEntity, 0, 0, 0, 0.0f, tickDelta, matrices, vertexConsumers, lightAbove);

        // Mandatory call after GL calls
        matrices.pop();
    }

//    public void renderRods(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//        matrices.push();
//
//        matrices.pop();
//    }

        @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        var minecraft = MinecraftClient.getInstance();
        //double offset = Math.sin((MinecraftClient.getInstance().world.getTime() + tickDelta) / 8.0) / 8.0;

        renderWind(stack, mode, matrices, vertexConsumers, light, overlay);

        // Move the item
        matrices.translate(0.5, 1.3, 0.5);

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
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        modelPartData.addChild("part1", ModelPartBuilder.create().uv(9, 7).cuboid(5.0F, 17.0F, 7.0F, 6.0F, 1.0F, 2.0F), ModelTransform.NONE);
        modelPartData.addChild("part2", ModelPartBuilder.create().uv(10, 2).cuboid(1.0F, 20.0F, 7.0F, 2.0F, 2.0F, 2.0F), ModelTransform.NONE);
        modelPartData.addChild("part3", ModelPartBuilder.create().uv(9, 9).cuboid(0.0F, 22.0F, 7.0F, 2.0F, 4.0F, 2.0F), ModelTransform.NONE);
        // Add remaining parts similarly...
        modelPartData.addChild("part28", ModelPartBuilder.create().uv(9, 0).cuboid(7.0F, 18.0F, 8.0F, 2.0F, 2.0F, 1.0F), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 64);
    }
}
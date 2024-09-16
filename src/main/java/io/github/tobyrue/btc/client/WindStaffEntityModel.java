package io.github.tobyrue.btc.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class WindStaffEntityModel extends Model {
    //texture here
    public static final Identifier TEXTURE = Identifier.of("textures/entity/breeze_rods.png");
    private final ModelPart root;

    public WindStaffEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // First Element: From [6.5, -5, 7.5] to [7.5, 17, 8.5]
        modelPartData.addChild("element1", ModelPartBuilder.create()
                .uv(11, 16)
                .cuboid(6.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F), ModelTransform.NONE);

        // Second Element: From [7.5, -5, 6.5] to [8.5, 17, 7.5]
        modelPartData.addChild("element2", ModelPartBuilder.create()
                .uv(8, 16)
                .cuboid(7.5F, -5F, 6.5F, 1.0F, 22.0F, 1.0F), ModelTransform.NONE);

        // Third Element: From [7.5, -5, 8.5] to [8.5, 17, 9.5]
        modelPartData.addChild("element3", ModelPartBuilder.create()
                .uv(18, 25)
                .cuboid(7.5F, -5F, 8.5F, 1.0F, 22.0F, 1.0F), ModelTransform.NONE);

        // Fourth Element: From [8.5, -5, 7.5] to [9.5, 17, 8.5]
        modelPartData.addChild("element4", ModelPartBuilder.create()
                .uv(24, 12)
                .cuboid(8.5F, -5F, 7.5F, 1.0F, 22.0F, 1.0F), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 16, 16); // Adjust texture size if needed
    }
    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.root.render(matrices, vertices, light, overlay, color);
    }
}

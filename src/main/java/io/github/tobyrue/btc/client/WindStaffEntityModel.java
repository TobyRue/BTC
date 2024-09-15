package io.github.tobyrue.btc.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class WindStaffEntityModel extends Model {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/trident.png");
    private final ModelPart root;

    public WindStaffEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        
    }
}

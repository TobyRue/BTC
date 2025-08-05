package io.github.tobyrue.btc.util;

import io.github.tobyrue.btc.BTC;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class UnlockScrollItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Identifier texture = Objects.requireNonNull(stack.get(BTC.SPELL_TEXTURE_COMPONENT));
        System.out.println(texture);
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
    }
}
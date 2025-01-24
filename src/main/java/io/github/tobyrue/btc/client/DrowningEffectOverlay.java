package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;

public class DrowningEffectOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register(DrowningEffectOverlay::renderOverlay);
    }


    private static void renderOverlay(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        LivingEntity player = client.player;

        // Check if the player has the DrowningEffect
        StatusEffectInstance drowningEffect = player.getStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.DROWNING));
        if (drowningEffect != null) {
            renderBlueScreen(drawContext);
        }
    }

    private static void renderBlueScreen(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Fill the screen with a transparent blue color (ARGB format: 0xAARRGGBB)
        context.fill(0, 0, screenWidth, screenHeight, 0xB2125184); // Semi-transparent blue
    }
}

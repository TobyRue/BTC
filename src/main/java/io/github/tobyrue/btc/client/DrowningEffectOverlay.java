package io.github.tobyrue.btc.client;

import io.github.tobyrue.btc.BTC;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
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
    static double lerp(double a, double b, double f) {
        return (a * (1.0 - f) + (b * f));
    }
    public static double inverseLerp(double a, double b, double v) {
        return (v - a) / (b - a);
    }
    static double clamp(double min, double max, double value) {
        return Math.max(Math.min(value, max), min);
    }
    static int color(double r, double g, double b, double a) {
        return ((int) (clamp(0, 1, r)*255) << 16) | ((int) (clamp(0, 1, g)*255) << 8) | ((int) (clamp(0, 1, b)*255) << 0) | ((int) (clamp(0, 1, a)*255) << 24);
    }

    private static void renderBlueScreen(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getWindow() == null) return;

        int effectTicks = MinecraftClient.getInstance().player.getStatusEffect(Registries.STATUS_EFFECT.getEntry(BTC.DROWNING)).getDuration();
        double alpha = lerp(0, 0.5, inverseLerp(0, 5*20, clamp(0, 5*20, effectTicks)));

        int color = color(94d/255, 198d/255, 255d/255, alpha);
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Fill the screen with a transparent blue color (ARGB format: 0xAARRGGBB)
        context.fill(0, 0, screenWidth, screenHeight, color); 
    }
}

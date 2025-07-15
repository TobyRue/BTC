package io.github.tobyrue.btc.client.screen.codex;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public record RenderHelper(
        DrawContext context,
        TextRenderer textRenderer,
        int screenWidth,
        int screenHeight,
        int imageWidth,
        int imageHeight,
        float scale
) {


    public Vec2i getBackgroundOrigin() {
        return new Vec2i((this.screenWidth - scaled(imageWidth)) / 2, (this.screenHeight - scaled(imageHeight)) / 2);
    }
    public Vec2i getCenter() {
        return new Vec2i(this.screenWidth / 2, this.screenHeight / 2);
    }

    private static final int CYAN = solidRGB(0x00FFFF);
    private static final int LIME = solidRGB(0x4CFF00);
    private static final int MAGENTA = solidRGB(0xFF00DC);

    public void renderDebugBackground() {
        if (MinecraftClient.getInstance().getDebugHud().shouldShowDebugHud()) {
            var c = getCenter();
            var b = getBackgroundOrigin();
            context.fill(b.x, b.y, (int) (b.x + imageWidth * scale), (int) (b.y + imageHeight * scale), LIME);
            context.drawHorizontalLine(0, screenWidth, c.y, CYAN);
            context.drawVerticalLine(c.x, 0, screenHeight, CYAN);
        }
    }

    public static int solidRGB(int rgb) {
        return 0xFF000000 | rgb;
    }

    public int scaled(int texturePx) {
        return (int) (texturePx * scale);
    }

    public enum Page {
        LEFT,
        RIGHT
    }

    //TODO
//    void f() {
//        context.drawText()
//    }
}

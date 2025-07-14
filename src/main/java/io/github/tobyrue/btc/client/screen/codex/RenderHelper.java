package io.github.tobyrue.btc.client.screen.codex;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec2f;

public record RenderHelper(
        DrawContext context,
        TextRenderer textRenderer,
        int screenWidth,
        int screenHeight,
        int imageWidth,
        int imageHeight,
        float scale
) {
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 160;

    public Vec2i getBackgroundOrigin() {
        return new Vec2i((int) ((this.screenWidth - (imageWidth * scale)) / 2), (int) ((this.screenHeight - (imageHeight * scale)) / 2));
    }
    public Vec2i getCenter() {
        return new Vec2i(this.screenWidth / 2, this.screenHeight / 2);
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

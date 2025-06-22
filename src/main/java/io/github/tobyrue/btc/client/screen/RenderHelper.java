package io.github.tobyrue.btc.client.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class RenderHelper {
    private final DrawContext context;
    private final TextRenderer textRenderer;

    public RenderHelper(final DrawContext context, final TextRenderer textRenderer) {
        this.context = context;
        this.textRenderer = textRenderer;
    }

    void f() {
        context.drawText()
    }
}

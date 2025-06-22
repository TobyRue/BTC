package io.github.tobyrue.btc.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class TextLine {
    public final Text text;
    public final int x;
    public final int y;
    public final Runnable onClick;  // can be null if not clickable
    public final TextRenderer textRenderer;

    public TextLine(Text text, int x, int y, Runnable onClick, TextRenderer textRenderer1) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.onClick = onClick;
        this.textRenderer = textRenderer1;
    }

    public boolean isMouseOver(double mouseX, double mouseY, Screen screen) {
        int width = this.textRenderer.getWidth(text);
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 9;
    }
}


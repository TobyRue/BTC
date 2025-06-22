package io.github.tobyrue.btc.client.screen.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ClickableTextWidget extends ClickableWidget {
    private final Text text;
    private final TextRenderer textRenderer;
    private final Consumer<ClickableTextWidget> onClick;

    public ClickableTextWidget(int x, int y, Text text, TextRenderer textRenderer, Consumer<ClickableTextWidget> onClick) {
        super(x, y, textRenderer.getWidth(text), textRenderer.fontHeight, text);
        this.text = text;
        this.textRenderer = textRenderer;
        this.onClick = onClick;
    }
    public final Text getText() {
        return this.text;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = this.isMouseOver(mouseX, mouseY) ? 0xA7B0E5 : 0x262626;
        context.drawText(textRenderer, text, this.getX(), this.getY(), color, false);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        onClick.accept(this);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}

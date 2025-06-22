package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.client.screen.widget.ClickableTextWidget;
import io.github.tobyrue.btc.client.screen.widget.TextLine;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.ArrayList;
import java.util.List;

public class Page {
    public final List<ButtonWidget> buttons = new ArrayList<>();
    public final List<ClickableTextWidget> texts = new ArrayList<>();
    public final List<TextLine> lines = new ArrayList<>();

    public final String title;

    public Page(String title) {
        this.title = title;
    }

    public void addButton(ButtonWidget button) {
        buttons.add(button);
    }

    public void addText(ClickableTextWidget widget) {
        texts.add(widget);
    }

    public void addLine(TextLine line, TextRenderer textRenderer) {
        lines.add(line);
    }
}

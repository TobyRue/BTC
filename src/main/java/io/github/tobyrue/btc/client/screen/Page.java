package io.github.tobyrue.btc.client.screen;

import net.minecraft.client.gui.widget.ButtonWidget;
import java.util.ArrayList;
import java.util.List;

public class Page {
    public final List<ButtonWidget> buttons = new ArrayList<>();
    public final String title;

    public Page(String title) {
        this.title = title;
    }

    public void addButton(ButtonWidget button) {
        buttons.add(button);
    }
}

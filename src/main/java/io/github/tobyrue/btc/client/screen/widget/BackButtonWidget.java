package io.github.tobyrue.btc.client.screen.widget;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class BackButtonWidget extends TexturedButtonWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(BTC.identifierOf("widget/back_button"), BTC.identifierOf("widget/back_button_highlighted"));


    protected BackButtonWidget(int x, int y, ButtonWidget.PressAction onPress) {
        super(x, y, 23, 13, TEXTURES, onPress);
    }
}
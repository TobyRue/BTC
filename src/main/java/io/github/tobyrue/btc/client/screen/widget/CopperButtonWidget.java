package io.github.tobyrue.btc.client.screen.widget;

import io.github.tobyrue.btc.BTC;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class CopperButtonWidget extends TexturedButtonWidget {
    private static final ButtonTextures TEXTURES = new ButtonTextures(BTC.identifierOf("widget/copper_button"), BTC.identifierOf("widget/copper_button_highlighted"));


    private final Text message;

    public CopperButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, TEXTURES, onPress);
        this.message = message;
    }
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public Text getMessage() {
        return message;
    }
    public static CopperButtonWidget.Builder builderCopper(Text message, PressAction onPress) {
        return new CopperButtonWidget.Builder(message, onPress);
    }
    @Override
    public void onPress() {

    }
    public static class Builder {
        private final Text message;
        private final PressAction onPress;
        private int x, y, width = 200, height = 20;

        public Builder(Text message, PressAction onPress) {
            this.message = message;
            this.onPress = onPress;
        }

        public Builder dimensions(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public CopperButtonWidget build() {
            return new CopperButtonWidget(x, y, width, height, message, onPress);
        }
    }

}

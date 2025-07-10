package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.AdvancementParser;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.btc.item.ScreenTestItem;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class SpellScreenTest extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book.png");

    public static String string = "";  // set externally via chat command etc.


    public SpellScreenTest() {
        super(Text.literal("Codex Test Screen"));
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int x = this.width / 2 - 110;
        int yStart = this.height / 2 - 85;

        Style hoveredStyle = null;

        if (string != null && !string.isEmpty()) {
            try {
                XMLParser<Codex.Text> parser = new XMLParser<>(Codex.Text.class);
                var parsedText = parser.parse(string).toText();

                var lines = this.textRenderer.wrapLines(parsedText, 180);

                int lineY = yStart;
                int lineHeight = this.textRenderer.fontHeight + 2;

                for (OrderedText line : lines) {
                    context.drawTextWithShadow(this.textRenderer, line, x, lineY, 0x000000);

                    // Hover detection on this line
                    int relativeMouseX = (int) (mouseX - x);
                    if (mouseY >= lineY && mouseY < lineY + lineHeight && relativeMouseX >= 0) {
                        Style styleAtMouse = this.textRenderer.getTextHandler().getStyleAt(line, relativeMouseX);
                        if (styleAtMouse != null) {
                            hoveredStyle = styleAtMouse;
                        }
                    }

                    lineY += lineHeight;
                }

            } catch (XMLException e) {
                e.printStackTrace();
            }
        }

        // Now render hover tooltip if any
        if (hoveredStyle != null && hoveredStyle.getHoverEvent() != null) {
            context.drawHoverEvent(this.textRenderer, hoveredStyle, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && string != null && !string.isEmpty()) {
            int xStart = this.width / 2 - 110;
            int yStart = this.height / 2 - 85;

            try {
                XMLParser<Codex.Text> parser = new XMLParser<>(Codex.Text.class);
                var parsedText = parser.parse(string).toText();

                var lines = this.textRenderer.wrapLines(parsedText, 180);

                int lineHeight = this.textRenderer.fontHeight + 2;
                int clickedLine = (int)((mouseY - yStart) / lineHeight);

                if (clickedLine >= 0 && clickedLine < lines.size()) {
                    OrderedText lineText = lines.get(clickedLine);

                    // Relative x inside text line
                    int relativeX = (int)(mouseX - xStart);
                    if (relativeX < 0) return super.mouseClicked(mouseX, mouseY, button);

                    Style style = this.textRenderer.getTextHandler().getStyleAt(lineText, relativeX);
                    if (style != null) {
                        boolean handled = this.handleTextClick(style);
                        if (handled) return true;
                    }
                }
            } catch (XMLException e) {
                e.printStackTrace();
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean handleTextClick(@Nullable Style style) {
        if (style == null) return false;

        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent == null) return false;

        MinecraftClient client = MinecraftClient.getInstance();

        switch (clickEvent.getAction()) {
            case OPEN_URL -> {
                try {
                    Util.getOperatingSystem().open(clickEvent.getValue());
                } catch (Exception e) {
                    client.player.sendMessage(Text.literal("Invalid URL: " + clickEvent.getValue()).formatted(Formatting.RED), false);
                }
                return true;
            }
            case RUN_COMMAND -> {
                client.player.networkHandler.sendCommand(clickEvent.getValue());
                client.setScreen(null);
                return true;
            }
            case SUGGEST_COMMAND -> {
                client.setScreen(new ChatScreen(clickEvent.getValue()));
                client.setScreen(null);
                return true;
            }
            case COPY_TO_CLIPBOARD -> {
                client.keyboard.setClipboard(clickEvent.getValue());
                client.player.sendMessage(Text.literal("Copied to clipboard!"), false);
                return true;
            }
            case CHANGE_PAGE -> {
                //TODO
            }
            default -> {
                return super.handleTextClick(style);
            }
        }
        return super.handleTextClick(style);
        }


    protected void renderTextHoverEffect(DrawContext context, HoverEvent hoverEvent, int mouseX, int mouseY) {
        if (hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            Text text = hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT);
            if (text != null) {
                context.drawTooltip(this.textRenderer, text, mouseX, mouseY);
            }
        }
        // You can add more hover event types here if you want
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);

        int imageWidth = 256;
        int imageHeight = 160;
        float scale = 1.5f;

        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        context.drawTexture(
                BOOK_TEXTURE,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );

        context.getMatrices().pop();
    }
}

package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Codex;
import io.github.tobyrue.btc.packets.SetElementPayload;
import io.github.tobyrue.xml.XMLException;
import io.github.tobyrue.xml.XMLParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@Environment(EnvType.CLIENT)
public class SpellScreenTest extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book.png");

    public static String string = "";  // set externally via chat commandHover etc.


    public SpellScreenTest() {
        super(Text.literal("Codex Test Screen"));
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        boolean pageSide = true;
        int x = this.width / 2 - 110;
        int yStart = this.height / 2 - 85;

        Style hoveredStyle = null;

        if (string != null && !string.isEmpty()) {
            try {
                XMLParser<Codex.Text> parser = new XMLParser<>(Codex.Text.class);
                var parsedText = parser.parse(string).toText();
                var parsed = parser.parse(string);

                var lines = this.textRenderer.wrapLines(parsedText, 140);

                int lineY = yStart;
                int lineHeight = this.textRenderer.fontHeight + 2;
                String align = parsed.align().toLowerCase(Locale.ROOT);
                Integer alignInt = parsed.alignInt();
                Boolean page = parsed.page();


                for (OrderedText line : lines) {
                    int lineWidth = this.textRenderer.getWidth(line);

                    if (page) {
                        switch (align) {
                            case "middle" -> x = ((this.width - lineWidth) / 2) + 90;
                            case "right"  -> x = this.width / 2 - lineWidth + 160;
                            default -> x = this.width / 2 + 20;
                        }
                    } else {
                        switch (align) {
                            case "middle" -> x = ((this.width - lineWidth) / 2) - 90;
                            case "right"  -> x = this.width / 2 - lineWidth - 20;
                            default -> x = this.width / 2 - 160;
                        }
                    }
                    x += alignInt;
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
            int yStart = this.height / 2 - 85;

            try {
                XMLParser<Codex.Text> parser = new XMLParser<>(Codex.Text.class);
                var parsedText = parser.parse(string).toText();
                var parsed = parser.parse(string);

                var lines = this.textRenderer.wrapLines(parsedText, 140);

                int lineY = yStart;
                int lineHeight = this.textRenderer.fontHeight + 2;
                String align = parsed.align().toLowerCase(Locale.ROOT);
                Integer alignInt = parsed.alignInt();
                Boolean page = parsed.page();

                for (OrderedText lineText : lines) {
                    int lineWidth = this.textRenderer.getWidth(lineText);

                    int x;

                    if (page) {
                        switch (align) {
                            case "middle" -> x = ((this.width - lineWidth) / 2) + 90;
                            case "right"  -> x = this.width / 2 - lineWidth + 160;
                            default -> x = this.width / 2 + 20;
                        }
                    } else {
                        switch (align) {
                            case "middle" -> x = ((this.width - lineWidth) / 2) - 90;
                            case "right"  -> x = this.width / 2 - lineWidth - 20;
                            default -> x = this.width / 2 - 160;
                        }
                    }
                    x += alignInt;
                    // check if mouse is inside this line
                    if (mouseY >= lineY && mouseY < lineY + lineHeight) {
                        int relativeX = (int) (mouseX - x);
                        if (relativeX >= 0) {
                            Style style = this.textRenderer.getTextHandler().getStyleAt(lineText, relativeX);
                            if (style != null) {
                                boolean handled = this.handleTextClick(style);
                                if (handled) return true;
                            }
                        }
                    }

                    lineY += lineHeight;
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
        ClickEvent.Action action = clickEvent.getAction();

        switch (action) {
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
                client.setScreen(null);
                client.setScreen(new ChatScreen(clickEvent.getValue()));
                return true;
            }
            case COPY_TO_CLIPBOARD -> {
                client.keyboard.setClipboard(clickEvent.getValue());
                client.player.sendMessage(Text.literal("Copied to clipboard!"), false);
                return true;
            }
            case CHANGE_PAGE -> {
                if (clickEvent.getValue().startsWith("page:")) {
                    String page = clickEvent.getValue().strip().substring(5);
                    try {
                        try {
                            int pageInt = Integer.parseInt(page);
                            System.out.println("Page Int: " + pageInt);
                        } catch (Exception e) {
                            System.out.println("Page Name: " + page);
                        }
                    } catch (RuntimeException r) {
                        throw new RuntimeException(r);
                    }
                } else if (clickEvent.getValue().startsWith("value:")) {
                    String value = clickEvent.getValue().strip().substring(6);
                    ClientPlayNetworking.send(new SetElementPayload(value, client.player.getUuid()));
                    return true;
                }
            }
//            case CODEX_VALUE -> {
//                try {
//
//                    String[] values = clickEvent.getValue().substring(6).strip().split("\\|");
//                    System.out.println("Changing value: " + values[0] + ", to " + values[1]);
//                } catch (IllegalArgumentException ex) {
//                    throw new IllegalArgumentException(String.format("Custom ClickEvent.Action 'CODEX_VALUE' not found: %s", ex));
//                }
//                client.player.sendMessage(
//                        Text.literal("CODEX_VALUE triggered: " + clickEvent.getValue()).formatted(Formatting.LIGHT_PURPLE),
//                        false
//                );
//                return true;
//            }
            default -> {
                return super.handleTextClick(style);
            }
        }
        return super.handleTextClick(style);
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

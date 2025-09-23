package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.mixin.KeyBindingAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class HexagonRadialMenu extends Screen {
    private static final Identifier BACKGROUND = BTC.identifierOf("textures/gui/honeycomb.png");
    private static final Identifier BACKGROUND_STONE = BTC.identifierOf("textures/gui/honeycomb_stone.png");

    private static final int TEX_WIDTH = 603;
    private static final int TEX_HEIGHT = 582;

    private int centerX;
    private int centerY;

    private final List<Value> spells; // list of spell values provided

    private final int start;
    private final int end;
    private final KeyBinding key;


    private DoubleInt mouse;

    public record Value(Text display, String commandHover, String commandClick) {}
    protected record DoubleInt(int mouseX, int mouseY) {}

    public HexagonRadialMenu(Text title, List<Value> spells, int start, int end, KeyBinding key) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = start;
        this.end = end; // clamp to size
        this.key = key;
    }

    @Override
    protected void init() {
        super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        ScreenMouseEvents.afterMouseScroll(this).register((screen, mouseX, mouseY, horiz, vert) -> {
            if (spells.isEmpty()) return;

            if (vert > 0 && start > 0) {
                int newStart = Math.max(0, start - 6);
                int newEnd = Math.min(newStart + 6, spells.size());
                close();
                client.setScreen(new HexagonRadialMenu(Text.of("radial"), spells, newStart, newEnd, key));
            }
            if (vert < 0 && end < spells.size()) {
                int newStart = start + 6;
                int newEnd = Math.min(newStart + 6, spells.size());
                close();
                client.setScreen(new HexagonRadialMenu(Text.of("radial"), spells, newStart, newEnd, key));
            }
        });
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.client == null || this.client.player == null) return false;

        if (keyCode == ((KeyBindingAccessor) key).getBoundKey().getCode()) {
            int hovered = getHoveredHex(mouse.mouseX, mouse.mouseY);
            if (hovered >= 0 && hovered + start < spells.size()) {
                Value value = spells.get(start + hovered);
                client.player.networkHandler.sendCommand(value.commandHover());
                System.out.println("Key release hover command: " + value.commandHover());
            }
            close();
            return true;
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        mouse = new DoubleInt(mouseX, mouseY);
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * @return -1 if center, 0-5 for surrounding hexagons clockwise starting at top
     */
    private int getHoveredHex(int mouseX, int mouseY) {
        if ((Math.pow((mouseX-centerX), 2) + Math.pow((mouseY-centerY), 2)) > 900) {
            int angle = (int) Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
            angle += 90;
            if (angle < 0) angle += 360;
            int sector = (int) Math.floor(angle / 60);
            return sector;
        }
        return -1;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sector = getHoveredHex((int) mouseX, (int) mouseY);
        if (sector >= 0 && sector + start < spells.size()) {
            Value value = spells.get(sector + start);
            System.out.println("Clicked: " + value.commandClick());
            client.player.networkHandler.sendCommand(value.commandClick());
            this.close();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        int sector = getHoveredHex(mouseX, mouseY);

        int imageWidth = TEX_WIDTH;
        int imageHeight = TEX_HEIGHT;
        float scale = 0.3f;

        int scaledWidth = (int) (imageWidth * scale);
        int scaledHeight = (int) (imageHeight * scale);

        int x = (this.width - scaledWidth) / 2;
        int y = (this.height - scaledHeight) / 2;

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        context.getMatrices().scale(scale, scale, 1.0f);

        // Draw base background
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        context.drawTexture(
                BACKGROUND,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );

        // Draw overlay with transparency
        RenderSystem.setShaderColor(1f, 1f, 1f, 200f / 255f); // ~70% opacity
        context.drawTexture(
                BACKGROUND_STONE,
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, 150f / 255f); // ~70% opacity
        context.drawTexture(
                BTC.identifierOf("textures/gui/honeycomb_sector_" + (sector + 1) + ".png"),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        // Reset state
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();

        context.getMatrices().pop();

        // Draw text for spells[start..end)
        int radius = 60;
        for (int i = 0; i < (end - start); i++) {
            Value spell = spells.get(start + i);
            double angleRad = Math.toRadians(i * 60 - 60);
            int hexCenterX = centerX + (int) (radius * Math.cos(angleRad));
            int hexCenterY = centerY + (int) (radius * Math.sin(angleRad));

            String text = spell.display().getString();

            // Simple word wrap
            int maxWidth = 40;
            String[] words = text.split(" ");
            List<String> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = (currentLine.length() == 0 ? "" : currentLine + " ") + word;
                if (this.textRenderer.getWidth(testLine) <= maxWidth) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) lines.add(currentLine.toString());

            boolean shrink = lines.size() > 3;

            context.getMatrices().push();
            if (shrink) {
                context.getMatrices().translate(hexCenterX, hexCenterY, 0);
                context.getMatrices().scale(0.75f, 0.75f, 1f);
                hexCenterX = 0;
                hexCenterY = 0;
            }

            int totalHeight = lines.size() * this.textRenderer.fontHeight;
            int startY = hexCenterY - totalHeight / 2;

            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                int lineWidth = this.textRenderer.getWidth(line);
                int lineX = hexCenterX - lineWidth / 2;
                int lineY = startY + lineIndex * this.textRenderer.fontHeight;
                context.drawText(this.textRenderer, line, lineX, lineY, 0xFFFFFF, true);
            }

            context.getMatrices().pop();
        }
    }
}




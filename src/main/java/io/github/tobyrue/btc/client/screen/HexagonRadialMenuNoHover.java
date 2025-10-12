package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.HexagonValues.*;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import io.github.tobyrue.btc.client.screen.HexagonNoHoverValues.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexagonRadialMenuNoHover extends Screen {
    private int centerX;
    private int centerY;

    private final List<ValueNoHover> spells; // list of spell values provided

    private final int start;
    private final int end;
    private final RadialIdentifiers radialIdentifiers;

    public HexagonRadialMenuNoHover(Text title, List<ValueNoHover> spells, int start, int end, RadialIdentifiers radialIdentifiers) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = start;
        this.end = end; // clamp to size
        this.radialIdentifiers = radialIdentifiers;
    }

    public HexagonRadialMenuNoHover(Text title, List<ValueNoHover> spells, RadialIdentifiers radialIdentifiers) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.radialIdentifiers = radialIdentifiers;
        this.start = 0;
        this.end = 6; // clamp to size
    }

    public HexagonRadialMenuNoHover(Text title, List<ValueNoHover> spells, int start, int end) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = start;
        this.end = end; // clamp to size
        this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60, 30, 40, 6, true, true, 582, 603, 0.3f);
    }
    public HexagonRadialMenuNoHover(Text title, List<ValueNoHover> spells) {
        super(title);
        this.spells = spells;
        this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60, 30, 40, 6, true, true, 582, 603, 0.3f);
        this.start = 0;
        this.end = 6;
    }


    @Override
    protected void init() {
        super.init();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        ScreenMouseEvents.afterMouseScroll(this).register((screen, mouseX, mouseY, horiz, vert) -> {
            if (spells.isEmpty()) return;

            if (vert > 0 && start > 0) {
                int newStart = Math.max(0, start - radialIdentifiers.sectors());
                int newEnd = Math.min(newStart + radialIdentifiers.sectors(), spells.size());
                close();
                client.setScreen(new HexagonRadialMenuNoHover(title, spells, newStart, newEnd, radialIdentifiers));
            }
            if (vert < 0 && end < spells.size()) {
                int newStart = start + radialIdentifiers.sectors();
                int newEnd = Math.min(newStart + radialIdentifiers.sectors(), spells.size());
                close();
                client.setScreen(new HexagonRadialMenuNoHover(title, spells, newStart, newEnd, radialIdentifiers));
            }
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    /**
     * @return -1 if center, 0-5 for surrounding hexagons clockwise starting at top
     */
    private int getHoveredHex(int mouseX, int mouseY) {
        if ((Math.pow((mouseX-centerX), 2) + Math.pow((mouseY-centerY), 2)) > Math.pow((radialIdentifiers.textRadius() / 2), 2)) {
            int angle = (int) Math.toDegrees(Math.atan2(mouseY - centerY, mouseX - centerX));
            angle += 90;
            if (angle < 0) angle += 360;
            int sector = (int) Math.floor((double) angle / ((double) 360 / radialIdentifiers.sectors()));
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
            ValueNoHover value = spells.get(sector + start);
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

        int imageWidth = radialIdentifiers.textureWidth();
        int imageHeight = radialIdentifiers.textureHeight();

        float scale = radialIdentifiers.imageScale();

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
        // Draw overlay with transparency
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.backgroundTransparency() / 255f); // ~70% opacity
        context.drawTexture(
                radialIdentifiers.backgroundTexture(),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.highlightedShapeTransparency() / 255f); // ~70% opacity
        context.drawTexture(
                Identifier.of(radialIdentifiers.highlightedShapeTexture().getNamespace(),radialIdentifiers.highlightedShapeTexture().getPath() + (sector + 1) + ".png"),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.backgroundOutlineTransparency() / 255f);
        context.drawTexture(
                radialIdentifiers.backgroundOutlineTexture(),
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
        int radius = this.radialIdentifiers.textRadius();
        for (int i = 0; i < (end - start); i++) {
            ValueNoHover spell = spells.get(start + i);
            double angleStep = 360.0 / radialIdentifiers.sectors();
            double angleRad = Math.toRadians(i * angleStep - angleStep);
            int hexCenterX = centerX + (int) (radius * Math.cos(angleRad));
            int hexCenterY = centerY + (int) (radius * Math.sin(angleRad));

            Text displayText = spell.display();

            String[] words = displayText.getString().split(" ");
            List<Text> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                String testLine = (currentLine.length() == 0 ? "" : currentLine + " ") + word;
                if (this.textRenderer.getWidth(testLine) <= this.radialIdentifiers.maxTextWidth()) {
                    currentLine = new StringBuilder(testLine);
                } else {
                    lines.add(Text.literal(currentLine.toString()));
                    currentLine = new StringBuilder(word);
                }
            }
            if (currentLine.length() > 0) lines.add(Text.literal(currentLine.toString()));

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
                Text line = lines.get(lineIndex);
                int lineWidth = this.textRenderer.getWidth(line);
                int lineX = hexCenterX - lineWidth / 2;
                int lineY = startY + lineIndex * this.textRenderer.fontHeight;
                context.drawText(this.textRenderer, line, lineX, lineY, displayText.getStyle().getColor() != null ? displayText.getStyle().getColor().getRgb() : 0xFFFFFF, radialIdentifiers.textShadow());
            }

            context.getMatrices().pop();
        }
        context.drawText(this.textRenderer, this.title, (this.width / 2) - (this.textRenderer.getWidth(this.title) / 2), this.height - (this.textRenderer.fontHeight * 2), this.title.getStyle().getColor() != null ? this.title.getStyle().getColor().getRgb() : 0xFFFFFF, radialIdentifiers.titleShadow());
    }
}




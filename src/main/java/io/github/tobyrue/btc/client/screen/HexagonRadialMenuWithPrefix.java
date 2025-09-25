package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.mixin.KeyBindingAccessor;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import io.github.tobyrue.btc.client.screen.HexagonValues.*;

import java.util.ArrayList;
import java.util.List;

public class HexagonRadialMenuWithPrefix extends Screen {

    private static final int TEX_WIDTH = 603;
    private static final int TEX_HEIGHT = 582;

    private int centerX;
    private int centerY;

    private final List<PrefixValue> spells; // list of spell values provided

    private final int start;
    private final int end;
    private final RadialIdentifiers radialIdentifiers;

    private final KeyBinding key;

    private DoubleInt mouse;
    
    public HexagonRadialMenuWithPrefix(Text title, List<PrefixValue> spells, int start, int end, KeyBinding key, RadialIdentifiers radialIdentifiers) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = start;
        this.end = end; // clamp to size
        this.radialIdentifiers = radialIdentifiers;
        this.key = key;
    }

    public HexagonRadialMenuWithPrefix(Text title, List<PrefixValue> spells, KeyBinding key, RadialIdentifiers radialIdentifiers) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = 0;
        this.end = 6; // clamp to size
        this.radialIdentifiers = radialIdentifiers;
        this.key = key;
    }

    public HexagonRadialMenuWithPrefix(Text title, List<PrefixValue> spells, int start, int end, KeyBinding key) {
        super(title);
        // only keep first 6 if longer
        this.spells = spells;
        this.start = start;
        this.end = end; // clamp to size
        this.key = key;
        this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60);
    }

    public HexagonRadialMenuWithPrefix(Text title, List<PrefixValue> spells, KeyBinding key) {
        super(title);
        this.spells = spells;
        this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60);
        this.start = 0;
        this.end = 6;
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
                client.setScreen(new HexagonRadialMenuWithPrefix(Text.of("radial"), spells, newStart, newEnd, key, radialIdentifiers));
            }
            if (vert < 0 && end < spells.size()) {
                int newStart = start + 6;
                int newEnd = Math.min(newStart + 6, spells.size());
                close();
                client.setScreen(new HexagonRadialMenuWithPrefix(Text.of("radial"), spells, newStart, newEnd, key, radialIdentifiers));
            }
        });
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (this.client == null || this.client.player == null) return false;

        if (keyCode == ((KeyBindingAccessor) key).getBoundKey().getCode()) {
            int hovered = getHoveredHex(mouse.mouseX(), mouse.mouseY());
            if (hovered >= 0 && hovered + start < spells.size()) {
                PrefixValue value = spells.get(start + hovered);

                close();

                client.setScreen(new HexagonRadialMenuWithSuffix(
                        Text.of("radial"),
                        value.commandHover(),
                        spells.stream()
                                .flatMap(inst -> inst.suffixValues().stream()
                                        .map(instTwo -> new SuffixValue(
                                                instTwo.display(),
                                                instTwo.suffixHover(),
                                                instTwo.suffixClick()
                                        ))
                                )
                                .toList(),
                        0,
                        value.suffixValues().size(),
                        key,
                        radialIdentifiers
                ));

                return true;
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
            PrefixValue value = spells.get(sector + start);

            this.close();

            client.setScreen(new HexagonRadialMenuWithSuffix(
                    Text.of("radial"),
                    value.commandClick(),
                    spells.stream()
                            .flatMap(inst -> inst.suffixValues().stream()
                                    .map(instTwo -> new SuffixValue(
                                            instTwo.display(),
                                            instTwo.suffixHover(),
                                            instTwo.suffixClick()
                                    ))
                            )
                            .toList(),
                    0,
                    value.suffixValues().size(),
                    key,
                    radialIdentifiers
            ));

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
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.backgroundOutlineTransparency() / 255f);
        context.drawTexture(
                radialIdentifiers.backgroundOutlineTexture(),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );

        // Draw overlay with transparency
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.backgroundTransparency() / 255f);
        context.drawTexture(
                radialIdentifiers.backgroundTexture(),
                0, 0,
                0, 0,
                imageWidth, imageHeight,
                imageWidth, imageHeight
        );
        RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.highlightedShapeTransparency() / 255f);
        context.drawTexture(
                Identifier.of(radialIdentifiers.highlightedShapeTexture().getNamespace(),radialIdentifiers.highlightedShapeTexture().getPath() + (sector + 1) + ".png"),
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
        int radius = radialIdentifiers.radius();
        for (int i = 0; i < (end - start); i++) {
            PrefixValue spell = spells.get(start + i);
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


    public static class HexagonRadialMenuWithSuffix extends Screen {

        private static final int TEX_WIDTH = 603;
        private static final int TEX_HEIGHT = 582;

        private int centerX;
        private int centerY;


        private final String prefixCommand;
        private final List<SuffixValue> spells; // list of spell values provided
        private final int start;
        private final int end;
        private final KeyBinding key;
        private final RadialIdentifiers radialIdentifiers;

        private DoubleInt mouse;
        
        public HexagonRadialMenuWithSuffix(Text title, String prefixCommand, List<SuffixValue> spells, int start, int end, KeyBinding key, RadialIdentifiers radialIdentifiers) {
            super(title);

            this.prefixCommand = prefixCommand;
            this.spells = spells;
            this.start = start;
            this.end = end; // clamp to size
            this.key = key;
            this.radialIdentifiers = radialIdentifiers;
        }

        public HexagonRadialMenuWithSuffix(Text title, String prefixCommand, List<SuffixValue> spells, KeyBinding key, RadialIdentifiers radialIdentifiers) {
            super(title);

            this.prefixCommand = prefixCommand;
            this.spells = spells;
            this.start = 0;
            this.end = 6; // clamp to size
            this.key = key;
            this.radialIdentifiers = radialIdentifiers;
        }

        public HexagonRadialMenuWithSuffix(Text title, String prefixCommand, List<SuffixValue> spells, int start, int end, KeyBinding key) {
            super(title);
            this.prefixCommand = prefixCommand;
            // only keep first 6 if longer
            this.spells = spells;
            this.start = start;
            this.end = end; // clamp to size
            this.key = key;
            this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60);
        }

        public HexagonRadialMenuWithSuffix(Text title, String prefixCommand, List<SuffixValue> spells, KeyBinding key) {
            super(title);
            this.prefixCommand = prefixCommand;
            this.spells = spells;
            this.radialIdentifiers = new RadialIdentifiers(BTC.identifierOf("textures/gui/honeycomb.png"), 255f, BTC.identifierOf("textures/gui/honeycomb_stone.png"), 200f, BTC.identifierOf("textures/gui/honeycomb_sector_"), 150f, 60);
            this.start = 0;
            this.end = 6;
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
                    client.setScreen(new HexagonRadialMenuWithSuffix(Text.of("radial"), prefixCommand, spells, newStart, newEnd, key, radialIdentifiers));
                }
                if (vert < 0 && end < spells.size()) {
                    int newStart = start + 6;
                    int newEnd = Math.min(newStart + 6, spells.size());
                    close();
                    client.setScreen(new HexagonRadialMenuWithSuffix(Text.of("radial"), prefixCommand, spells, newStart, newEnd, key, radialIdentifiers));
                }
            });
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            if (this.client == null || this.client.player == null) return false;

            if (keyCode == ((KeyBindingAccessor) key).getBoundKey().getCode()) {
                int hovered = getHoveredHex(mouse.mouseX(), mouse.mouseY());
                if (hovered >= 0 && hovered + start < spells.size()) {
                    SuffixValue value = spells.get(start + hovered);
                    client.player.networkHandler.sendCommand(prefixCommand + value.suffixHover());
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
                SuffixValue value = spells.get(sector + start);
                client.player.networkHandler.sendCommand(prefixCommand + value.suffixClick());
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
            RenderSystem.setShaderColor(1f, 1f, 1f, radialIdentifiers.backgroundOutlineTransparency() / 255f);
            context.drawTexture(
                    radialIdentifiers.backgroundOutlineTexture(),
                    0, 0,
                    0, 0,
                    imageWidth, imageHeight,
                    imageWidth, imageHeight
            );

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
            // Reset state
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();

            context.getMatrices().pop();

            // Draw text for spells[start..end)
            int radius = radialIdentifiers.radius();
            for (int i = 0; i < (end - start); i++) {
                SuffixValue spell = spells.get(start + i);
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
}




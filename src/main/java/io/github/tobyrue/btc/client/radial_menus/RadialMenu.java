package io.github.tobyrue.btc.client.radial_menus;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.mixin.KeyBindingAccessor;
import io.github.tobyrue.btc.util.NinePatchHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RadialMenu extends Screen {
    private int lastHovered = -2;

    public static final int WHITE = 0xFFFFFFFF;
    public static final int LIGHT_GRAY = 0xFFC6C6C6;
    public static final int GRAY = 0xFF808080;
    public static final int DARK_GRAY = 0xFF373737;
    public static final int BLACK = 0xFF000000;

    public static final int COPPER = 0xFFD67B5B;
    public static final int GOLD = 0xFFFFE16B;
    public static final int BRASS = 0xFFC5A059;
    public static final int PEACH = 0xFFFFB52E;

    public static final int CYAN = 0xFF43D5CB;
    public static final int LIGHT_BLUE = 0xFF00A3FF;
    public static final int PURPLE = 0xFFA000FF;

    public static final int RED = 0xFFFF3131;
    public static final int GREEN = 0xFF55FF55;
    public static final int DARK_RED = 0xFF8B0000;


    public enum TriggerType {
        CLICK, KEY_RELEASE;
    }

    @FunctionalInterface
    public interface RadialAction {
        void execute(RadialMenu menu, TriggerType type);
    }

    public static class RadialValue {
        public Text display;
        public RadialAction action;
        public boolean useHoverEffects = false;
        public @Nullable Identifier icon_hover;
        public float hover_icon_scale = 1.2f;
        public int hover_color = 0xFFFFF500; // Gold ish
        public Formatting[] formattingHover = { Formatting.BOLD };
        public boolean shadow_hover = true;

        public @Nullable Identifier icon;
        public float icon_scale = 1.0f;
        public int color = 0xFFFFFFFF;
        public Formatting[] formatting = {};
        public boolean shadow = true;

        public int maxWidth = 50;
        public @Nullable Text tooltip;
        private boolean manuallyStyled = false;


        public RadialValue(Text display, RadialAction action) {
            this.display = display;
            this.action = action;
        }



        public RadialValue enableHoverEffects(boolean enable) {
            this.useHoverEffects = enable;
            return this;
        }

        public RadialValue withHoverIcon(Identifier iconHover) {
            this.icon_hover = iconHover;
            return this;
        }

        public RadialValue withHoverIconScale(float scale) {
            this.hover_icon_scale = scale;
            return this;
        }

        public RadialValue withHoverColor(int hexColor) {
            if ((hexColor & 0xFF000000) == 0) {
                this.hover_color = 0xFF000000 | hexColor;
            } else {
                this.hover_color = hexColor;
            }
            this.manuallyStyled = true;
            return this;
        }

        public RadialValue withHoverFormatting(Formatting... formattingHover) {
            this.formattingHover = formattingHover;
            this.manuallyStyled = true;
            return this;
        }

        public RadialValue withShadowHover(boolean shadowHover) {
            this.shadow_hover = shadowHover;
            this.manuallyStyled = true;
            return this;
        }


        public RadialValue withHoverEffectsImage(Identifier iconHover, float scale) {
            withHoverIcon(iconHover);
            withHoverIconScale(scale);
            return this;
        }

        public RadialValue withHoverEffectsText(int hex, Formatting... formattingHover) {
            withHoverColor(hex);
            withHoverFormatting(formattingHover);
            this.manuallyStyled = true;
            return this;
        }

        public RadialValue withIcon(Identifier icon) {
            this.icon = icon;
            return this;
        }

        public RadialValue withIconScale(float scale) {
            this.icon_scale = scale;
            return this;
        }

        public RadialValue withColor(int hexColor) {
            if ((hexColor & 0xFF000000) == 0) {
                this.color = 0xFF000000 | hexColor;
            } else {
                this.color = hexColor;
            }
            this.manuallyStyled = true;
            return this;
        }

        public RadialValue withFormatting(Formatting... formatting) {
            this.formatting = formatting;
            this.manuallyStyled = true;
            return this;
        }

        public RadialValue withShadow(boolean shadow) {
            this.shadow = shadow;
            this.manuallyStyled = true;
            return this;
        }



        public RadialValue withMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public static RadialValue nested(Text display, List<RadialValue> subValues) {
            return new RadialValue(display, (menu, type) -> menu.openSubMenu(display, subValues));
        }
        public static List<RadialValue> styleAll(List<RadialValue> list, int color, net.minecraft.util.Formatting... formatting) {
            for (RadialValue val : list) {
                val.withColor(color).withFormatting(formatting);
            }
            return list;
        }
        public static List<RadialValue> styleAllWithHover(List<RadialValue> list, int hoverColor, net.minecraft.util.Formatting... hoverFormatting) {
            for (RadialValue val : list) {
                val.enableHoverEffects(true).withHoverColor(hoverColor).withHoverFormatting(hoverFormatting);
            }
            return list;
        }

        public RadialValue withTooltip(Text tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public RadialValue setManuallyStyled(boolean manuallyStyled) {
            this.manuallyStyled = manuallyStyled;
            return this;
        }
    }

    public record Config(
            Identifier outline, Identifier bg, Identifier highlight,
            @Nullable Identifier titlePlate,
            float bgAlpha, float outlineAlpha, float highlightAlpha,
            int textRadius, int centerRadius, int sectors,
            float angleOffset, int texW, int texH, float scale,
            int titleColor, int maxTitleWidth, boolean titleShadow
    ) {}


    public record DataPoints(
            Object... data
    ) {}

    private final List<RadialValue> values;
    public final Config config;
    private final int start, end;
    private final Object context;
    @Nullable public final KeyBinding key;
    private double mouseX, mouseY;
    private final int layer;
    private final RadialMenu parent;
    private final boolean renderCenter;


    /**
     * Easily opens a new radial menu using the current menu's configuration and context.
     */
    public void openSubMenu(Text newTitle, List<RadialValue> subValues) {
        if (this.client == null) return;

        int hovered = getHoveredSector();
        if (hovered >= 0 && (hovered + start) < values.size()) {
            RadialValue parent = values.get(hovered + start);

            for (RadialValue sub : subValues) {
                if (!sub.manuallyStyled) {
                    sub.color = parent.color;
                    sub.formatting = parent.formatting;
                    sub.shadow = parent.shadow;
                    sub.maxWidth = parent.maxWidth;

                    sub.useHoverEffects = parent.useHoverEffects;
                    sub.hover_color = parent.hover_color;
                    sub.formattingHover = parent.formattingHover;
                    sub.shadow_hover = parent.shadow_hover;
                    sub.hover_icon_scale = parent.hover_icon_scale;
                }
            }
        }

        this.client.setScreen(new RadialMenu(newTitle, subValues, this.context, this.key, this.config, this.renderCenter, this.layer + 1, this, 0));
    }

    public void goBack() {
        if (this.parent != null && this.client != null) {
            this.client.setScreen(this.parent);
        } else {
            this.close();
        }
    }

    public int getLayer() {
        return layer;
    }

    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, Config config, boolean renderCenter, int layer, @Nullable RadialMenu parent, int start) {
        super(title);
        this.values = values;
        this.context = context;
        this.config = config;
        this.key = key;
        this.start = start;
        this.layer = layer;
        this.parent = parent;
        this.renderCenter = renderCenter;
        this.end = Math.min(values.size(), start + config.sectors());
    }

    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, boolean renderCenter, int start) {
        this(title, values, context, key, new Config(
                BTC.identifierOf("textures/gui/honeycomb.png"),
                renderCenter ? BTC.identifierOf("textures/gui/honeycomb_stone.png") : BTC.identifierOf("textures/gui/honeycomb_stone_no_center.png"),
                BTC.identifierOf("textures/gui/honeycomb_sector_"),
                BTC.identifierOf("textures/gui/title_plate.png"),
                200f / 255f, 255f / 255f, 150f / 255f,
                60, 30, 6, 0.0f, 603, 582, 0.3f,
                0xFFFFFF, 100, true
        ), renderCenter, start, null, 0);
    }
    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, int titleColor, boolean titleShadow, boolean renderCenter, int start) {
        this(title, values, context, key, new Config(
                BTC.identifierOf("textures/gui/honeycomb.png"),
                renderCenter ? BTC.identifierOf("textures/gui/honeycomb_stone.png") : BTC.identifierOf("textures/gui/honeycomb_stone_no_center.png"),
                BTC.identifierOf("textures/gui/honeycomb_sector_"),
                BTC.identifierOf("textures/gui/title_plate.png"),
                200f / 255f, 255f / 255f, 150f / 255f,
                60,  30, 6, 0.0f, 603, 582, 0.3f,
                titleColor, 100, titleShadow
        ), renderCenter, start, null, 0);
    }

    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, Config config, int layer, @Nullable RadialMenu parent, int start) {
        super(title);
        this.values = values;
        this.context = context;
        this.config = config;
        this.key = key;
        this.start = start;
        this.layer = layer;
        this.parent = parent;
        this.renderCenter = true;
        this.end = Math.min(values.size(), start + config.sectors());
    }

    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, int start) {
        this(title, values, context, key, new Config(
                BTC.identifierOf("textures/gui/honeycomb.png"),
                BTC.identifierOf("textures/gui/honeycomb_stone.png"),
                BTC.identifierOf("textures/gui/honeycomb_sector_"),
                BTC.identifierOf("textures/gui/title_plate.png"),
                200f / 255f, 255f / 255f, 150f / 255f,
                60, 30, 6, 0.0f, 603, 582, 0.3f,
                0xFFFFFF, 100, true
        ), true, start, null, 0);
    }
    public RadialMenu(Text title, List<RadialValue> values, @Nullable Object context, @Nullable KeyBinding key, int titleColor, boolean titleShadow, int start) {
        this(title, values, context, key, new Config(
                BTC.identifierOf("textures/gui/honeycomb.png"),
                BTC.identifierOf("textures/gui/honeycomb_stone.png"),
                BTC.identifierOf("textures/gui/honeycomb_sector_"),
                BTC.identifierOf("textures/gui/title_plate.png"),
                200f / 255f, 255f / 255f, 150f / 255f,
                60,  30, 6, 0.0f, 603, 582, 0.3f,
                titleColor, 100, titleShadow
        ), true, start, null, 0);
    }


    public boolean sendCommand(String command) {
        if (client == null || client.player == null) return false;
        client.player.networkHandler.sendCommand(command);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext() { return (T) context; }

    @Override
    protected void init() {
        ScreenMouseEvents.afterMouseScroll(this).register((screen, mx, my, h, vert) -> {
            if (values.size() <= config.sectors()) return;
            int next = vert > 0 ? start - config.sectors() : start + config.sectors();
            if (next >= 0 && next < values.size()) {
                MinecraftClient.getInstance().setScreen(new RadialMenu(title, values, context, key, config, layer, this.parent, next));
            }
        });
    }

    private int getHoveredSector() {
        double dx = mouseX - (width / 2);
        double dy = mouseY - (height / 2);
        if (dx * dx + dy * dy < config.centerRadius() * config.centerRadius()) return -1;
        double angle = Math.atan2(dx, -dy) - Math.toRadians(config.angleOffset());
        if (angle < 0) angle += 2 * Math.PI;
        return Math.min((int) (angle / ((2 * Math.PI) / config.sectors())), config.sectors() - 1);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        int s = getHoveredSector();
        if (s >= 0 && (s + start) < values.size()) {
            this.close();
            values.get(s + start).action.execute(this, TriggerType.CLICK);
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (key != null && keyCode == ((KeyBindingAccessor) key).getBoundKey().getCode()) {
            int s = getHoveredSector();
            this.close();
            if (s >= 0 && (s + start) < values.size()) values.get(s + start).action.execute(this, TriggerType.KEY_RELEASE);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX; this.mouseY = mouseY;
        this.renderInGameBackground(context);
        int hovered = getHoveredSector();

        if (hovered != lastHovered) {
            if (((hovered != -1 && !renderCenter) || (hovered >= -1 && renderCenter)) && client != null && client.player != null) {
                client.player.playSound(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.5f);
            }
            lastHovered = hovered;
        }

        context.getMatrices().push();
        context.getMatrices().translate((width - (config.texW() * config.scale())) / 2, (height - (config.texH() * config.scale())) / 2, 0);
        context.getMatrices().scale(config.scale(), config.scale(), 1f);
        RenderSystem.enableBlend();

        RenderSystem.setShaderColor(1, 1, 1, config.bgAlpha());
        context.drawTexture(config.bg(), 0, 0, 0, 0, config.texW(), config.texH(), config.texW(), config.texH());
        if ((hovered >= -1 && renderCenter) || (hovered > -1 && !renderCenter)) {
            RenderSystem.setShaderColor(1, 1, 1, config.highlightAlpha());
            Identifier hTex = Identifier.of(config.highlight().getNamespace(), config.highlight().getPath() + (hovered + 1) + ".png");
            context.drawTexture(hTex, 0, 0, 0, 0, config.texW(), config.texH(), config.texW(), config.texH());
        }
        RenderSystem.setShaderColor(1, 1, 1, config.outlineAlpha());
        context.drawTexture(config.outline(), 0, 0, 0, 0, config.texW(), config.texH(), config.texW(), config.texH());
        RenderSystem.setShaderColor(1, 1, 1, 1f);
        context.getMatrices().pop();

        for (int i = 0; i < (end - start); i++) {
            RadialValue val = values.get(start + i);
            boolean isHovered = (i == hovered); // Check if this specific sector is hovered

            double step = (2 * Math.PI) / config.sectors();
            double rad = (i * step) + (step / 2.0);
            int tx = (width / 2) + (int) (config.textRadius() * Math.sin(rad));
            int ty = (height / 2) - (int) (config.textRadius() * Math.cos(rad));

            int finalColor = (isHovered && val.useHoverEffects) ? val.hover_color : val.color;
            float finalScale = (isHovered && val.useHoverEffects) ? val.hover_icon_scale : val.icon_scale;
            Identifier finalIcon = (isHovered && val.useHoverEffects) ? val.icon_hover : val.icon;

            if (finalIcon != null) {
                context.getMatrices().push();
                context.getMatrices().translate(tx, ty, 0);
                context.getMatrices().scale(finalScale, finalScale, 1f);
                context.drawTexture(finalIcon, -12, -12, 0, 0, 24, 24, 24, 24);
                context.getMatrices().pop();
            } else {
                Text textToRender = (isHovered && val.useHoverEffects) ?
                        val.display.copy().formatted(val.formattingHover) : val.display.copy().formatted(val.formatting);

                renderText(context, textToRender, tx, ty, finalColor, val.maxWidth, (isHovered && val.useHoverEffects) ? val.shadow_hover : val.shadow);
            }
        }
        if (this.title != null) {
            var wrappedLines = textRenderer.getTextHandler().wrapLines(this.title, config.maxTitleWidth(), this.title.getStyle());
            int maxLineWidth = 0;
            for (var line : wrappedLines) {
                maxLineWidth = Math.max(maxLineWidth, textRenderer.getWidth(line));
            }

            int textBlockHeight = wrappedLines.size() * textRenderer.fontHeight;
            int titleX = width / 2;
            int titleY = height - (textBlockHeight / 2) - 10;

            if (config.titlePlate() != null) {
                int paddingW = 12;
                int paddingH = 8;

                int plateW = Math.min(config.maxTitleWidth() + paddingW, maxLineWidth + paddingW);
                int plateH = textBlockHeight + paddingH;

                NinePatchHelper.renderNinePatchPlate(context, config.titlePlate(), titleX - (plateW / 2), titleY - (plateH / 2), plateW, plateH, 4, 50, 20);
            }

            renderText(context, this.title, titleX, titleY, config.titleColor(), config.maxTitleWidth, config.titleShadow());
        }
        if (hovered >= 0 && (hovered + start) < values.size()) {
            RadialValue val = values.get(hovered + start);
            if (val.tooltip != null) {
                context.drawTooltip(textRenderer, val.tooltip, mouseX, mouseY);
            }
        }
    }

    private void renderText(DrawContext ctx, Text text, int x, int y, int defaultColor, int maxWidth, boolean hasShadow) {
        String textContent = text.getString();
        String[] words = textContent.split(" ");
        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = (currentLine.length() == 0 ? "" : currentLine + " ") + word;
            if (this.textRenderer.getWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if (currentLine.length() > 0) lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());

        int absoluteMaxWidth = 0;
        for (String line : lines) {
            absoluteMaxWidth = Math.max(absoluteMaxWidth, textRenderer.getWidth(Text.literal(line).setStyle(text.getStyle())));
        }

        float hScale = absoluteMaxWidth > maxWidth ? (float) maxWidth / (float) absoluteMaxWidth : 1.0f;

        int fontHeight = this.textRenderer.fontHeight;
        float totalHeight = lines.size() * fontHeight;
        float vScale = totalHeight > 35 ? 35f / totalHeight : 1.0f;

        float finalScale = Math.min(hScale, vScale);

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(finalScale, finalScale, 1.0f);

        float startY = -totalHeight / 2.0f;

        for (int i = 0; i < lines.size(); i++) {
            Text lineTextObj = Text.literal(lines.get(i)).setStyle(text.getStyle());

            OrderedText orderedLine = lineTextObj.asOrderedText();
            int lineWidth = this.textRenderer.getWidth(orderedLine);

            ctx.getMatrices().push();
            float lineY = startY + (i * fontHeight);
            ctx.getMatrices().translate(0, lineY + (fontHeight / 2.0f), 0);

            ctx.drawText(
                    this.textRenderer,
                    orderedLine,
                    -lineWidth / 2,
                    -fontHeight / 2,
                    defaultColor,
                    hasShadow
            );
            ctx.getMatrices().pop();
        }

        ctx.getMatrices().pop();
    }
    @Override public boolean shouldPause() { return false; }
}
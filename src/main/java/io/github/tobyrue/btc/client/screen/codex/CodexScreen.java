package io.github.tobyrue.btc.client.screen.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.util.Vec2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class CodexScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book_text_area.png");
    private final Codex codex;

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 160;

    private static final int PAGE_WIDTH = 91;
    private static final int PAGE_HEIGHT = 124;

    private static final int LEFT_PAGE_OFFSET_X = 22;
    private static final int LEFT_PAGE_OFFSET_Y = 16;
    private static final int RIGHT_PAGE_OFFSET_X = 142;
    private static final int RIGHT_PAGE_OFFSET_Y = 16;

    private int currentPageIndex = 0;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;

    private final List<RenderedTextSegment> clickableSegments = new ArrayList<>();

    public record RenderedTextSegment(int x, int y, int width, int height, OrderedText line) {}

    private record RenderCommand(int layer, Runnable action) {}

    public CodexScreen(Codex codex) {
        super(Text.empty());
        this.codex = codex;
    }

    @Override
    protected void init() {
        super.init();
        RenderHelper h = getRenderHelper(null);
        Vec2i origin = getOrigin();

        int buttonY = origin.y + h.scaled(138);
        this.nextPageButton = this.addDrawableChild(new PageTurnWidget(origin.x + h.scaled(200), buttonY, true, button -> goToNextPage(), true));
        this.previousPageButton = this.addDrawableChild(new PageTurnWidget(origin.x + h.scaled(30), buttonY, false, button -> goToPreviousPage(), true));

        updatePageButtons();
    }

    private PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    private void updatePageButtons() {
        if (codex == null) return;
        int maxPages = codex.getPages().size(getPlayer());
        this.nextPageButton.visible = (currentPageIndex + 2) < maxPages;
        this.previousPageButton.visible = currentPageIndex > 0;
    }

    private void goToNextPage() {
        if (codex != null && (currentPageIndex + 2) < codex.getPages().size(getPlayer())) {
            currentPageIndex += 2;
            updatePageButtons();
        }
    }

    private void goToPreviousPage() {
        if (currentPageIndex >= 2) {
            currentPageIndex -= 2;
            updatePageButtons();
        }
    }

    public void goToPageById(String pageId) {
        if (codex == null) return;
        PlayerEntity player = getPlayer();
        var pages = codex.getPages();
        int visibleCount = pages.size(player);

        for (int i = 0; i < visibleCount; i++) {
            Codex.Page p = pages.getPage(i + 1, player);
            if (p != null && pageId.equalsIgnoreCase(p.id())) {
                this.currentPageIndex = (i % 2 == 0) ? i : i - 1;
                updatePageButtons();
                break;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        clickableSegments.clear();

        if (codex == null) {
            super.render(context, mouseX, mouseY, delta);
            return;
        }

        super.render(context, mouseX, mouseY, delta);

        RenderHelper h = getRenderHelper(context);
        Vec2i origin = getOrigin();
        PlayerEntity player = getPlayer();

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);

        Codex.Page leftPage = codex.getPages().getPage(currentPageIndex + 1, player);
        if (leftPage != null) {
            int leftX = origin.x + h.scaled(LEFT_PAGE_OFFSET_X);
            int startY = origin.y + h.scaled(LEFT_PAGE_OFFSET_Y);
            int maxWidth = h.scaled(PAGE_WIDTH);
            int maxHeight = h.scaled(PAGE_HEIGHT);
            renderPageContent(context, leftPage, leftX, startY, maxWidth, maxHeight);
        }

        Codex.Page rightPage = codex.getPages().getPage(currentPageIndex + 2, player);
        if (rightPage != null) {
            int rightX = origin.x + h.scaled(RIGHT_PAGE_OFFSET_X);
            int startY = origin.y + h.scaled(RIGHT_PAGE_OFFSET_Y);
            int maxWidth = h.scaled(PAGE_WIDTH);
            int maxHeight = h.scaled(PAGE_HEIGHT);
            renderPageContent(context, rightPage, rightX, startY, maxWidth, maxHeight);
        }

        Style hoveredStyle = getStyleAt(mouseX, mouseY);
        if (hoveredStyle != null) {
            if (hoveredStyle.getHoverEvent() != null) {
                context.drawHoverEvent(this.textRenderer, hoveredStyle, mouseX, mouseY);
            } else if (hoveredStyle.getClickEvent() != null) {
                ClickEvent clickEvent = hoveredStyle.getClickEvent();
                if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
                    Text tooltip = Text.literal("Go to page: " + clickEvent.getValue());
                    context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                } else if (clickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
                    Text tooltip = Text.literal(clickEvent.getValue());
                    context.drawTooltip(this.textRenderer, tooltip, mouseX, mouseY);
                }
            }
        }

        context.getMatrices().pop();
    }

    private void renderPageContent(DrawContext context, Codex.Page page, int startX, int startY, int maxWidth, int maxHeight) {
        List<RenderCommand> renderQueue = new ArrayList<>();
        LayoutCursor cursor = new LayoutCursor(startX, startY, 0);

        renderBlockCollection(context, page.children(), startX, cursor, maxWidth, startY + maxHeight, renderQueue);

        renderQueue.sort(Comparator.comparingInt(RenderCommand::layer));

        for (RenderCommand command : renderQueue) {
            command.action().run();
        }
    }

    private static class LayoutCursor {
        int currentX;
        int currentY;
        int maxLineHeight;

        LayoutCursor(int startX, int startY, int maxLineHeight) {
            this.currentX = startX;
            this.currentY = startY;
            this.maxLineHeight = maxLineHeight;
        }

        void nextLine(int startX) {
            this.currentY += this.maxLineHeight;
            this.currentX = startX;
            this.maxLineHeight = 0;
        }
    }

    private void renderBlockCollection(DrawContext context, Iterable<Codex.BlockContent> blocks, int startX, LayoutCursor cursor, int maxWidth, int maxYLimit, List<RenderCommand> queue) {
        RenderHelper h = getRenderHelper(context);
        PlayerEntity player = getPlayer();

        for (Codex.BlockContent block : blocks) {
            if (cursor.currentY >= maxYLimit) {
                break;
            }

            if (block instanceof Codex.ConditionalContent conditional) {
                if (!conditional.isRequirementMet(player)) {
                    continue;
                }
            }

            if (block instanceof Codex.Page.Line line) {
                if (cursor.currentX != startX) {
                    cursor.nextLine(startX);
                }

                float textScale = parseScale(line.size());
                int scaledFontHeight = Math.round(this.textRenderer.fontHeight * textScale);

                if (line.getMargins() != null && line.getMargins().top() != null) {
                    cursor.currentY += line.getMargins().top().getPxDistance(maxWidth, (float) h.scale(), scaledFontHeight);
                }

                Text fullText = line.toText();
                int availableWidthUnscaled = Math.round(maxWidth / textScale);
                List<OrderedText> wrappedLines = this.textRenderer.wrapLines(fullText, availableWidthUnscaled);
                Codex.Alignment align = resolveAlignment(line.align());

                for (OrderedText wrappedLine : wrappedLines) {
                    if (cursor.currentY + scaledFontHeight > maxYLimit) {
                        break;
                    }

                    int lineUnscaledWidth = this.textRenderer.getWidth(wrappedLine);
                    int lineScaledWidth = Math.round(lineUnscaledWidth * textScale);
                    int drawX = calculateAlignedX(align, startX, maxWidth, lineScaledWidth);
                    int finalY = cursor.currentY;

                    clickableSegments.add(new RenderedTextSegment(
                            drawX, finalY, lineScaledWidth, scaledFontHeight, wrappedLine
                    ));

                    queue.add(new RenderCommand(0, () -> {
                        context.getMatrices().push();
                        context.getMatrices().translate(drawX, finalY, 0);
                        context.getMatrices().scale(textScale, textScale, 1.0f);
                        context.drawText(this.textRenderer, wrappedLine, 0, 0, 0xFF000000, false);
                        context.getMatrices().pop();
                    }));

                    cursor.currentY += scaledFontHeight + Math.round(1 * textScale);
                }

                if (line.getMargins() != null && line.getMargins().bottom() != null) {
                    cursor.currentY += line.getMargins().bottom().getPxDistance(maxWidth, (float) h.scale(), scaledFontHeight);
                }

            } else if (block instanceof Codex.Page.HorizontalLine hr) {
                if (cursor.currentX != startX) {
                    cursor.nextLine(startX);
                }
                int lineY = cursor.currentY + 4;
                if (lineY <= maxYLimit) {
                    queue.add(new RenderCommand(0, () ->
                            context.fill(startX + hr.inset(), lineY, (startX + maxWidth) - hr.inset(), lineY + 1, 0xFF000000 | hr.color().toInt())
                    ));
                }
                cursor.currentY += 9;

            } else if (block instanceof Codex.Page.BreakLine) {
                cursor.nextLine(startX);
                cursor.currentY += this.textRenderer.fontHeight;

            } else if (block instanceof Codex.Page.IfCondition ifCond) {
                renderBlockCollection(context, ifCond.children(), startX, cursor, maxWidth, maxYLimit, queue);

            } else if (block instanceof Codex.Page.UnlessCondition unlessCond) {
                renderBlockCollection(context, unlessCond.children(), startX, cursor, maxWidth, maxYLimit, queue);

            } else if (block instanceof Codex.Page.ImageContent img) {
                String src = img.getSrc();
                if (!src.isBlank()) {
                    int imgWidth = img.getWidth();
                    int imgHeight = img.getHeight();

                    if (!img.isInline() && cursor.currentX != startX) {
                        cursor.nextLine(startX);
                    }

                    if (cursor.currentX + imgWidth > startX + maxWidth && cursor.currentX > startX) {
                        cursor.nextLine(startX);
                    }

                    int renderX = calculateAlignedX(img.getAlign(), cursor.currentX, maxWidth, imgWidth) + img.getOffsetX();
                    int renderY = cursor.currentY + img.getOffsetY();

                    if (renderY + imgHeight <= maxYLimit) {
                        Identifier textureId = Identifier.of(src);

                        queue.add(new RenderCommand(img.getLayer(), () -> {
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();
                            context.drawTexture(
                                    textureId,
                                    renderX, renderY,
                                    img.getU(), img.getV(),
                                    imgWidth, imgHeight,
                                    img.getTextureWidth(), img.getTextureHeight()
                            );
                            RenderSystem.disableBlend();
                        }));

                        cursor.maxLineHeight = Math.max(cursor.maxLineHeight, imgHeight);

                        if (img.isInline()) {
                            cursor.currentX += imgWidth + 2;
                        } else {
                            cursor.currentY += imgHeight;
                            cursor.currentX = startX;
                            cursor.maxLineHeight = 0;
                        }
                    }
                }

            } else if (block instanceof Codex.Page.VideoContent vid) {
                String[] sources = vid.getParsedSources();
                if (sources.length > 0) {
                    int imgWidth = vid.getWidth();
                    int imgHeight = vid.getHeight();

                    if (!vid.isInline() && cursor.currentX != startX) {
                        cursor.nextLine(startX);
                    }

                    if (cursor.currentX + imgWidth > startX + maxWidth && cursor.currentX > startX) {
                        cursor.nextLine(startX);
                    }

                    int renderX = calculateAlignedX(vid.getAlign(), cursor.currentX, maxWidth, imgWidth) + vid.getOffsetX();
                    int renderY = cursor.currentY + vid.getOffsetY();

                    if (renderY + imgHeight <= maxYLimit) {
                        int framesPerTexture = vid.getFrames();
                        int[] customOrder = vid.getParsedFrameOrder();
                        int rawTotalFrames = customOrder.length > 0 ? customOrder.length : (framesPerTexture * sources.length);
                        int totalFrames = Math.max(1, rawTotalFrames - vid.getTrimFrames());

                        queue.add(new RenderCommand(vid.getLayer(), () -> {
                            RenderSystem.enableBlend();
                            RenderSystem.defaultBlendFunc();

                            if (totalFrames > 1) {
                                long clientTicks = MinecraftClient.getInstance().inGameHud.getTicks();
                                float renderTickCounter = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(false);

                                float timeInTicks = clientTicks + renderTickCounter;
                                int frameTicks = Math.max(1, vid.getFrameTicks());

                                float totalProgress = timeInTicks / (float) frameTicks;
                                int currentStep = ((int) Math.floor(totalProgress)) % totalFrames;
                                float frameProgress = totalProgress - (float) Math.floor(totalProgress);

                                int nextStep = (currentStep + 1) % totalFrames;

                                int globalFrameA = customOrder.length > 0 ? customOrder[currentStep] : currentStep;
                                int globalFrameB = customOrder.length > 0 ? customOrder[nextStep] : nextStep;

                                int srcIndexA = Math.min(globalFrameA / framesPerTexture, sources.length - 1);
                                int localFrameA = globalFrameA % framesPerTexture;
                                Identifier textureA = Identifier.of(sources[srcIndexA]);
                                int vA = vid.getV() + (localFrameA * imgHeight);

                                int srcIndexB = Math.min(globalFrameB / framesPerTexture, sources.length - 1);
                                int localFrameB = globalFrameB % framesPerTexture;
                                Identifier textureB = Identifier.of(sources[srcIndexB]);
                                int vB = vid.getV() + (localFrameB * imgHeight);

                                if (vid.isInterpolated()) {
                                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                                    context.drawTexture(
                                            textureA,
                                            renderX, renderY,
                                            vid.getU(), vA,
                                            imgWidth, imgHeight,
                                            vid.getTextureWidth(), vid.getTextureHeight()
                                    );

                                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, frameProgress);
                                    context.drawTexture(
                                            textureB,
                                            renderX, renderY,
                                            vid.getU(), vB,
                                            imgWidth, imgHeight,
                                            vid.getTextureWidth(), vid.getTextureHeight()
                                    );

                                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                                } else {
                                    context.drawTexture(
                                            textureA,
                                            renderX, renderY,
                                            vid.getU(), vA,
                                            imgWidth, imgHeight,
                                            vid.getTextureWidth(), vid.getTextureHeight()
                                    );
                                }
                            } else {
                                Identifier textureId = Identifier.of(sources[0]);
                                context.drawTexture(
                                        textureId,
                                        renderX, renderY,
                                        vid.getU(), vid.getV(),
                                        imgWidth, imgHeight,
                                        vid.getTextureWidth(), vid.getTextureHeight()
                                );
                            }

                            RenderSystem.disableBlend();
                        }));

                        cursor.maxLineHeight = Math.max(cursor.maxLineHeight, imgHeight);

                        if (vid.isInline()) {
                            cursor.currentX += imgWidth + 2;
                        } else {
                            cursor.currentY += imgHeight;
                            cursor.currentX = startX;
                            cursor.maxLineHeight = 0;
                        }
                    }
                }
            }
        }
    }

    private float parseScale(String size) {
        if (size == null || size.isBlank()) {
            return 1.0f;
        }
        try {
            String clean = size.toLowerCase().replace("x", "").replace("pt", "").trim();
            return Float.parseFloat(clean);
        } catch (NumberFormatException e) {
            return 1.0f;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            Style style = getStyleAt(mouseX, mouseY);
            if (style != null && style.getClickEvent() != null) {
                ClickEvent clickEvent = style.getClickEvent();
                if (clickEvent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
                    goToPageById(clickEvent.getValue());
                    return true;
                } else {
                    return this.handleTextClick(style);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Style getStyleAt(double mouseX, double mouseY) {
        for (RenderedTextSegment segment : clickableSegments) {
            if (mouseX >= segment.x && mouseX <= segment.x + segment.width &&
                    mouseY >= segment.y && mouseY <= segment.y + segment.height) {

                float textScale = (float) segment.height / (float) this.textRenderer.fontHeight;
                int relativeX = (int) ((mouseX - segment.x) / textScale);

                return this.textRenderer.getTextHandler().getStyleAt(segment.line, relativeX);
            }
        }
        return null;
    }

    private int calculateAlignedX(Codex.Alignment align, int startX, int maxWidth, int lineWidth) {
        return switch (align) {
            case CENTER -> startX + (maxWidth - lineWidth) / 2;
            case RIGHT -> startX + (maxWidth - lineWidth);
            case LEFT, TEXT_LOCALE -> startX;
        };
    }

    private Codex.Alignment resolveAlignment(Codex.Alignment align) {
        if (align == Codex.Alignment.TEXT_LOCALE) {
            return Codex.Alignment.LEFT;
        }
        return align;
    }

    public RenderHelper getRenderHelper(DrawContext context) {
        return new RenderHelper(context, textRenderer, width, height, IMAGE_WIDTH, IMAGE_HEIGHT, 1.5f);
    }

    private Vec2i getOrigin() {
        RenderHelper h = getRenderHelper(null);
        return h.getBackgroundOrigin();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderInGameBackground(context);
        RenderHelper h = getRenderHelper(context);
        Vec2i b = h.getBackgroundOrigin();

        context.drawTexture(
                BOOK_TEXTURE,
                b.x, b.y,
                0, 0,
                h.scaled(h.imageWidth()), h.scaled(h.imageHeight()),
                h.scaled(h.imageWidth()), h.scaled(h.imageHeight())
        );
    }
}
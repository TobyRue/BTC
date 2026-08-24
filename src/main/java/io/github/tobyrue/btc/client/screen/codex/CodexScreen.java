package io.github.tobyrue.btc.client.screen.codex;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.screen.codex.style.Margins;
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
        this.setFocused(null);
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

        List<Codex.BlockContent> blockList = new ArrayList<>();
        for (Codex.BlockContent b : blocks) {
            blockList.add(b);
        }

        int i = 0;
        while (i < blockList.size()) {
            if (cursor.currentY >= maxYLimit) {
                break;
            }

            Codex.BlockContent block = blockList.get(i);

            if (block instanceof Codex.ConditionalContent conditional) {
                if (!conditional.isRequirementMet(player)) {
                    i++;
                    continue;
                }
            }

            if (isEligibleForGroup(block)) {
                List<Codex.BlockContent> group = new ArrayList<>();

                while (i < blockList.size()) {
                    Codex.BlockContent candidate = blockList.get(i);
                    if (candidate instanceof Codex.ConditionalContent cond && !cond.isRequirementMet(player)) {
                        i++;
                        continue;
                    }

                    if (isEligibleForGroup(candidate)) {
                        group.add(candidate);
                        i++;
                    } else {
                        break;
                    }
                }

                layoutMediaGroup(context, group, startX, cursor, maxWidth, maxYLimit, queue, h);
                continue;
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
                renderSingleImage(context, img, startX, cursor, maxWidth, maxYLimit, queue);

            } else if (block instanceof Codex.Page.VideoContent vid) {
                renderSingleVideo(context, vid, startX, cursor, maxWidth, maxYLimit, queue);
            }

            i++;
        }
    }

    private boolean isEligibleForGroup(Codex.BlockContent block) {
        if (block instanceof Codex.Page.ImageContent img) {
            return img.isInline() && img.getOffsetX() == 0 && img.getOffsetY() == 0;
        } else if (block instanceof Codex.Page.VideoContent vid) {
            return vid.isInline() && vid.getOffsetX() == 0 && vid.getOffsetY() == 0;
        }
        return false;
    }

    private Codex.Alignment getMediaAlignment(Codex.BlockContent block) {
        if (block instanceof Codex.Page.ImageContent img) return img.getAlign();
        if (block instanceof Codex.Page.VideoContent vid) return vid.getAlign();
        return Codex.Alignment.LEFT;
    }

    private void layoutMediaGroup(DrawContext context, List<Codex.BlockContent> group, int startX, LayoutCursor cursor, int maxWidth, int maxYLimit, List<RenderCommand> queue, RenderHelper h) {
        if (cursor.currentX != startX) {
            cursor.nextLine(startX);
        }

        List<List<Codex.BlockContent>> lines = new ArrayList<>();
        List<Codex.BlockContent> currentLine = new ArrayList<>();
        int currentLineWidth = 0;

        for (Codex.BlockContent item : group) {
            int itemW = getItemWidthWithMargins(item, maxWidth, h);

            if (!currentLine.isEmpty() && currentLineWidth + itemW > maxWidth) {
                lines.add(currentLine);
                currentLine = new ArrayList<>();
                currentLineWidth = 0;
            }

            currentLine.add(item);
            currentLineWidth += itemW;
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine);
        }

        for (List<Codex.BlockContent> line : lines) {
            if (cursor.currentY >= maxYLimit) break;

            int maxLineHeight = 0;
            boolean allCentered = true;
            boolean allRight = true;
            int totalLineWidth = 0;

            for (Codex.BlockContent item : line) {
                maxLineHeight = Math.max(maxLineHeight, getItemHeightWithMargins(item, maxWidth, h));
                Codex.Alignment align = resolveAlignment(getMediaAlignment(item));
                if (align != Codex.Alignment.CENTER) allCentered = false;
                if (align != Codex.Alignment.RIGHT) allRight = false;
                totalLineWidth += getItemWidthWithMargins(item, maxWidth, h);
            }

            if (allCentered) {
                int startOffset = startX + (maxWidth - totalLineWidth) / 2;
                int currentX = startOffset;
                for (Codex.BlockContent item : line) {
                    int marginLeft = getMarginLeft(item, maxWidth, h);
                    int marginTop = getMarginTop(item, maxWidth, h);
                    int itemX = currentX + marginLeft;
                    int itemY = cursor.currentY + marginTop;

                    if (item instanceof Codex.Page.ImageContent img) {
                        enqueueImage(context, img, itemX, itemY, maxYLimit, queue);
                    } else if (item instanceof Codex.Page.VideoContent vid) {
                        enqueueVideo(context, vid, itemX, itemY, maxYLimit, queue);
                    }
                    currentX += getItemWidthWithMargins(item, maxWidth, h);
                }
            } else if (allRight) {
                int currentX = startX + maxWidth - totalLineWidth;
                for (Codex.BlockContent item : line) {
                    int marginLeft = getMarginLeft(item, maxWidth, h);
                    int marginTop = getMarginTop(item, maxWidth, h);
                    int itemX = currentX + marginLeft;
                    int itemY = cursor.currentY + marginTop;

                    if (item instanceof Codex.Page.ImageContent img) {
                        enqueueImage(context, img, itemX, itemY, maxYLimit, queue);
                    } else if (item instanceof Codex.Page.VideoContent vid) {
                        enqueueVideo(context, vid, itemX, itemY, maxYLimit, queue);
                    }
                    currentX += getItemWidthWithMargins(item, maxWidth, h);
                }
            } else {
                int leftOccupied = 0;
                int rightOccupied = 0;

                for (Codex.BlockContent item : line) {
                    Codex.Alignment itemAlign = resolveAlignment(getMediaAlignment(item));
                    if (itemAlign == Codex.Alignment.LEFT) {
                        leftOccupied += getItemWidthWithMargins(item, maxWidth, h);
                    } else if (itemAlign == Codex.Alignment.RIGHT) {
                        rightOccupied += getItemWidthWithMargins(item, maxWidth, h);
                    }
                }

                int currentLeftX = startX;
                int currentRightX = startX + maxWidth - rightOccupied;

                for (Codex.BlockContent item : line) {
                    Codex.Alignment itemAlign = resolveAlignment(getMediaAlignment(item));
                    int itemW = getItemWidthWithMargins(item, maxWidth, h);

                    int marginTop = getMarginTop(item, maxWidth, h);
                    int marginLeft = getMarginLeft(item, maxWidth, h);

                    int itemX;
                    if (itemAlign == Codex.Alignment.RIGHT) {
                        itemX = currentRightX + marginLeft;
                        currentRightX += itemW;
                    } else if (itemAlign == Codex.Alignment.CENTER) {
                        itemX = startX + (maxWidth - itemW) / 2 + marginLeft;
                    } else {
                        itemX = currentLeftX + marginLeft;
                        currentLeftX += itemW;
                    }

                    int itemY = cursor.currentY + marginTop;

                    if (item instanceof Codex.Page.ImageContent img) {
                        enqueueImage(context, img, itemX, itemY, maxYLimit, queue);
                    } else if (item instanceof Codex.Page.VideoContent vid) {
                        enqueueVideo(context, vid, itemX, itemY, maxYLimit, queue);
                    }
                }
            }

            cursor.currentY += maxLineHeight;
            cursor.currentX = startX;
            cursor.maxLineHeight = 0;
        }
    }

    private int getItemWidthWithMargins(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        if (item instanceof Codex.Page.ImageContent img) {
            return img.getWidth() + getMarginLeft(img, maxWidth, h) + getMarginRight(img, maxWidth, h);
        } else if (item instanceof Codex.Page.VideoContent vid) {
            return vid.getWidth() + getMarginLeft(vid, maxWidth, h) + getMarginRight(vid, maxWidth, h);
        }
        return 0;
    }

    private int getItemHeightWithMargins(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        if (item instanceof Codex.Page.ImageContent img) {
            return img.getHeight() + getMarginTop(img, maxWidth, h) + getMarginBottom(img, maxWidth, h);
        } else if (item instanceof Codex.Page.VideoContent vid) {
            return vid.getHeight() + getMarginTop(vid, maxWidth, h) + getMarginBottom(vid, maxWidth, h);
        }
        return 0;
    }

    private int getMarginTop(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        Margins m = (item instanceof Codex.Page.ImageContent img) ? img.getMargins() : ((Codex.Page.VideoContent) item).getMargins();
        return (m != null && m.top() != null) ? m.top().getPxDistance(maxWidth, (float) h.scale(), 0) : 0;
    }

    private int getMarginBottom(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        Margins m = (item instanceof Codex.Page.ImageContent img) ? img.getMargins() : ((Codex.Page.VideoContent) item).getMargins();
        return (m != null && m.bottom() != null) ? m.bottom().getPxDistance(maxWidth, (float) h.scale(), 0) : 0;
    }

    private int getMarginLeft(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        Margins m = (item instanceof Codex.Page.ImageContent img) ? img.getMargins() : ((Codex.Page.VideoContent) item).getMargins();
        return (m != null && m.left() != null) ? m.left().getPxDistance(maxWidth, (float) h.scale(), 0) : 0;
    }

    private int getMarginRight(Codex.BlockContent item, int maxWidth, RenderHelper h) {
        Margins m = (item instanceof Codex.Page.ImageContent img) ? img.getMargins() : ((Codex.Page.VideoContent) item).getMargins();
        return (m != null && m.right() != null) ? m.right().getPxDistance(maxWidth, (float) h.scale(), 0) : 0;
    }

    private void renderSingleImage(DrawContext context, Codex.Page.ImageContent img, int startX, LayoutCursor cursor, int maxWidth, int maxYLimit, List<RenderCommand> queue) {
        RenderHelper h = getRenderHelper(context);
        int marginTop = getMarginTop(img, maxWidth, h);
        int marginBottom = getMarginBottom(img, maxWidth, h);
        int marginLeft = getMarginLeft(img, maxWidth, h);
        int marginRight = getMarginRight(img, maxWidth, h);

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

        if (!img.isInline() && cursor.currentX != startX) {
            cursor.nextLine(startX);
        }

        int renderX = calculateAlignedX(img.getAlign(), startX, maxWidth, imgWidth + marginLeft + marginRight) + marginLeft + img.getOffsetX();
        int renderY = cursor.currentY + marginTop + img.getOffsetY();

        enqueueImage(context, img, renderX, renderY, maxYLimit, queue);

        int totalHeight = imgHeight + marginTop + marginBottom;
        cursor.maxLineHeight = Math.max(cursor.maxLineHeight, totalHeight);

        if (img.isInline()) {
            cursor.currentX += imgWidth + marginLeft + marginRight + 2;
        } else {
            cursor.currentY += totalHeight;
            cursor.currentX = startX;
            cursor.maxLineHeight = 0;
        }
    }

    private void enqueueImage(DrawContext context, Codex.Page.ImageContent img, int renderX, int renderY, int maxYLimit, List<RenderCommand> queue) {
        String src = img.getSrc();
        if (src.isBlank()) return;

        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();

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
        }
    }

    private void renderSingleVideo(DrawContext context, Codex.Page.VideoContent vid, int startX, LayoutCursor cursor, int maxWidth, int maxYLimit, List<RenderCommand> queue) {
        RenderHelper h = getRenderHelper(context);
        int marginTop = getMarginTop(vid, maxWidth, h);
        int marginBottom = getMarginBottom(vid, maxWidth, h);
        int marginLeft = getMarginLeft(vid, maxWidth, h);
        int marginRight = getMarginRight(vid, maxWidth, h);

        int imgWidth = vid.getWidth();
        int imgHeight = vid.getHeight();

        if (!vid.isInline() && cursor.currentX != startX) {
            cursor.nextLine(startX);
        }

        int renderX = calculateAlignedX(vid.getAlign(), startX, maxWidth, imgWidth + marginLeft + marginRight) + marginLeft + vid.getOffsetX();
        int renderY = cursor.currentY + marginTop + vid.getOffsetY();

        enqueueVideo(context, vid, renderX, renderY, maxYLimit, queue);

        int totalHeight = imgHeight + marginTop + marginBottom;
        cursor.maxLineHeight = Math.max(cursor.maxLineHeight, totalHeight);

        if (vid.isInline()) {
            cursor.currentX += imgWidth + marginLeft + marginRight + 2;
        } else {
            cursor.currentY += totalHeight;
            cursor.currentX = startX;
            cursor.maxLineHeight = 0;
        }
    }

    private void enqueueVideo(DrawContext context, Codex.Page.VideoContent vid, int renderX, int renderY, int maxYLimit, List<RenderCommand> queue) {
        String[] sources = vid.getParsedSources();
        if (sources.length == 0) return;

        int imgWidth = vid.getWidth();
        int imgHeight = vid.getHeight();

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
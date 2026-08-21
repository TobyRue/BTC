package io.github.tobyrue.btc.client.screen.codex;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.util.Vec2i;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class CodexScreen extends Screen {
    public static final Identifier BOOK_TEXTURE = BTC.identifierOf("textures/gui/book_text_area.png");
    private final Codex codex;

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 160;

    private static final int PAGE_WIDTH = 92;
    private static final int PAGE_HEIGHT = 118;
    private static final int LEFT_PAGE_X = 20;
    private static final int RIGHT_PAGE_X = 144;
    private static final int PAGE_Y = 16;

    private int currentPageIndex = 0;
    private PageTurnWidget nextPageButton;
    private PageTurnWidget previousPageButton;

    private final List<RenderedTextSegment> clickableSegments = new ArrayList<>();

    public record RenderedTextSegment(int x, int y, int width, int height, OrderedText line) {}

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

    private void updatePageButtons() {
        if (codex == null) return;
        int maxPages = codex.getPages().size();
        this.nextPageButton.visible = (currentPageIndex + 2) < maxPages;
        this.previousPageButton.visible = currentPageIndex > 0;
    }

    private void goToNextPage() {
        if (codex != null && (currentPageIndex + 2) < codex.getPages().size()) {
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
        var pages = codex.getPages();
        for (int i = 0; i < pages.size(); i++) {
            Codex.Page p = pages.getPage(i + 1);
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

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);

        Codex.Page leftPage = codex.getPages().getPage(currentPageIndex + 1);
        if (leftPage != null) {
            int leftX = origin.x + h.scaled(LEFT_PAGE_X);
            int startY = origin.y + h.scaled(PAGE_Y);
            int maxWidth = h.scaled(PAGE_WIDTH);
            renderPageContent(context, leftPage, leftX, startY, maxWidth);
        }

        Codex.Page rightPage = codex.getPages().getPage(currentPageIndex + 2);
        if (rightPage != null) {
            int rightX = origin.x + h.scaled(RIGHT_PAGE_X);
            int startY = origin.y + h.scaled(PAGE_Y);
            int maxWidth = h.scaled(PAGE_WIDTH);
            renderPageContent(context, rightPage, rightX, startY, maxWidth);
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

    private void renderPageContent(DrawContext context, Codex.Page page, int startX, int startY, int maxWidth) {
        renderBlockCollection(context, page.children(), startX, startY, maxWidth);
    }

    private int renderBlockCollection(DrawContext context, Iterable<Codex.BlockContent> blocks, int startX, int currentY, int maxWidth) {
        RenderHelper h = getRenderHelper(context);

        for (Codex.BlockContent block : blocks) {
            if (block instanceof Codex.ConditionalContent conditional) {
                if (!conditional.isRequirementMet(null)) {
                    continue;
                }
            }

            if (block instanceof Codex.Page.Line line) {
                Text fullText = line.toText();
                List<OrderedText> wrappedLines = this.textRenderer.wrapLines(fullText, maxWidth);
                Codex.Alignment align = resolveAlignment(line.align());

                for (OrderedText wrappedLine : wrappedLines) {
                    int lineWidth = this.textRenderer.getWidth(wrappedLine);
                    int drawX = calculateAlignedX(align, startX, maxWidth, lineWidth);

                    clickableSegments.add(new RenderedTextSegment(
                            drawX, currentY, lineWidth, this.textRenderer.fontHeight, wrappedLine
                    ));

                    context.drawText(this.textRenderer, wrappedLine, drawX, currentY, 0xFF000000, false);
                    currentY += this.textRenderer.fontHeight + 1;
                }

                if (line.getMargins() != null && line.getMargins().bottom() != null) {
                    int fontHeight = this.textRenderer.fontHeight;
                    currentY += line.getMargins().bottom().getPxDistance(maxWidth, (float) h.scale(), fontHeight);
                }

            } else if (block instanceof Codex.Page.HorizontalLine) {
                int lineY = currentY + 4;
                context.fill(startX, lineY, startX + maxWidth, lineY + 1, 0xFF888888);
                currentY += 9;

            } else if (block instanceof Codex.Page.BreakLine) {
                currentY += this.textRenderer.fontHeight;

            } else if (block instanceof Codex.Page.IfCondition ifCond) {
                currentY = renderBlockCollection(context, ifCond.children(), startX, currentY, maxWidth);

            } else if (block instanceof Codex.Page.UnlessCondition unlessCond) {
                currentY = renderBlockCollection(context, unlessCond.children(), startX, currentY, maxWidth);
            }
        }

        return currentY;
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
                int relativeX = (int) (mouseX - segment.x);
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
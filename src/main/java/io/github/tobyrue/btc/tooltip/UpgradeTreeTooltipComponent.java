package io.github.tobyrue.btc.tooltip;

import io.github.tobyrue.btc.tooltip.UpgradeTreeTooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.Locale;

public class UpgradeTreeTooltipComponent implements TooltipComponent {
    private final List<Pair<Identifier, Text>> upgrades;

    private static final int DISPLAY_COUNT = 2;
    private static final long CYCLE_TIME_MS = 4000L;
    private static final long TRANSITION_TIME_MS = 600L;
    private static final int ITEM_HEIGHT = 10;
    private static final int HEADER_HEIGHT = 10;

    public UpgradeTreeTooltipComponent(UpgradeTreeTooltipData data) {
        this.upgrades = data.upgrades();
    }

    private int getVisibleCount() {
        return Math.min(upgrades.size(), DISPLAY_COUNT);
    }

    @Override
    public int getHeight() {
        return HEADER_HEIGHT + (getVisibleCount() * ITEM_HEIGHT);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        Text headerText = Text.translatable("scroll_upgrade.btc.description.available_upgrades");
        int maxWidth = textRenderer.getWidth(headerText);

        for (Pair<Identifier, Text> entry : upgrades) {

            Text text = buildText(entry);
            maxWidth = Math.max(maxWidth, textRenderer.getWidth(text));
        }
        return maxWidth + 10;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if (upgrades.isEmpty()) return;

        Text headerText = Text.translatable("scroll_upgrade.btc.description.available_upgrades");
        context.drawText(textRenderer, headerText, x, y + 1, 0xFFFFFFFF, true);

        int treeY = y + HEADER_HEIGHT;
        int visibleCount = getVisibleCount();
        int totalHeight = visibleCount * ITEM_HEIGHT;
        int lineX = x + 2;

        context.fill(lineX, treeY, lineX + 1, treeY + totalHeight, 0xFF888888);

        if (upgrades.size() <= DISPLAY_COUNT) {
            for (int i = 0; i < upgrades.size(); i++) {
                int itemY = treeY + (i * ITEM_HEIGHT);
                Text text = buildText(upgrades.get(i));
                context.drawText(textRenderer, text, x + 8, itemY + 1, 0xFFFFFFFF, true);
            }
            return;
        }

        long time = System.currentTimeMillis();
        long currentCycleTime = time % CYCLE_TIME_MS;

        int totalWindows = (int) Math.ceil((double) upgrades.size() / DISPLAY_COUNT);
        int currentWindowIndex = (int) ((time / CYCLE_TIME_MS) % totalWindows);
        int nextWindowIndex = (currentWindowIndex + 1) % totalWindows;

        int currentStartIndex = currentWindowIndex * DISPLAY_COUNT;
        int nextStartIndex = nextWindowIndex * DISPLAY_COUNT;

        context.enableScissor(x, treeY, x + getWidth(textRenderer), treeY + totalHeight);

        if (currentCycleTime >= (CYCLE_TIME_MS - TRANSITION_TIME_MS)) {
            long transitionProgressMs = currentCycleTime - (CYCLE_TIME_MS - TRANSITION_TIME_MS);
            float progress = (float) transitionProgressMs / TRANSITION_TIME_MS;
            float easedProgress = (float) (1.0 - Math.cos(progress * Math.PI)) / 2.0f;

            int currentOffset = (int) (-easedProgress * totalHeight);
            int nextOffset = (int) ((1.0f - easedProgress) * totalHeight);

            drawBatch(textRenderer, x, treeY + currentOffset, context, currentStartIndex);
            drawBatch(textRenderer, x, treeY + nextOffset, context, nextStartIndex);

        } else {
            drawBatch(textRenderer, x, treeY, context, currentStartIndex);
        }

        context.disableScissor();
    }

    private void drawBatch(TextRenderer textRenderer, int x, int baseY, DrawContext context, int startIndex) {
        for (int i = 0; i < DISPLAY_COUNT; i++) {
            int itemIndex = startIndex + i;
            if (itemIndex >= upgrades.size()) break;

            Text text = buildText(upgrades.get(itemIndex));
            int itemY = baseY + (i * ITEM_HEIGHT);
            context.drawText(textRenderer, text, x + 8, itemY + 1, 0xFFFFFFFF, true);
        }
    }

    private Text buildText(Pair<Identifier, Text> entry) {
        Identifier name = entry.getLeft();
        return Text.translatable("scroll_upgrade." + name.getNamespace().toLowerCase(Locale.ROOT) + "." + name.getPath().toLowerCase(Locale.ROOT))
                .append(" - ")
                .append(entry.getRight());
    }
}
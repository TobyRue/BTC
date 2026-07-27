package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.AbstractFurnaceRecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ScrollTableScreen extends HandledScreen<ScrollTableScreenHandler> implements RecipeBookProvider {
    private static final Identifier TEXTURE = Identifier.of("btc", "textures/gui/scroll_table.png");

    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");
    public final AbstractFurnaceRecipeBookScreen recipeBook;

    public ScrollTableScreen(ScrollTableScreenHandler handler, AbstractFurnaceRecipeBookScreen recipeBook, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 200;
        this.recipeBook = recipeBook;
        this.playerInventoryTitleY = (this.backgroundHeight / 2) + 4;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.client != null && this.client.player != null) {
            float progress = this.client.player.experienceProgress;
            int xpLevel = this.client.player.experienceLevel;

            int barWidth = 162;
            int barHeight = 5;

            int xpBarX = x + 7;
            int xpBarY = y + 95;

            RenderSystem.enableBlend();

            context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, xpBarX, xpBarY, barWidth, barHeight);

            int progressWidth = (int) (progress * (barWidth + 1));
            if (progressWidth > 0) {
                context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE, barWidth, barHeight, 0, 0, xpBarX, xpBarY, progressWidth, barHeight);
            }

            RenderSystem.disableBlend();

            if (xpLevel > 0) {
                String levelStr = String.valueOf(xpLevel);

                int textX = x + (this.backgroundWidth - this.textRenderer.getWidth(levelStr)) / 2;
                int textY = y + 105;

                context.drawText(this.textRenderer, levelStr, textX + 1, textY, 0, false);
                context.drawText(this.textRenderer, levelStr, textX - 1, textY, 0, false);
                context.drawText(this.textRenderer, levelStr, textX, textY + 1, 0, false);
                context.drawText(this.textRenderer, levelStr, textX, textY - 1, 0, false);

                context.drawText(this.textRenderer, levelStr, textX, textY, 8453920, false);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        this.recipeBook.slotClicked(slot);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
    }

    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    public boolean charTyped(char chr, int modifiers) {
        return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
    }

    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}
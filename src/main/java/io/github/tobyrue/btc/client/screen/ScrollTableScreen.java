package io.github.tobyrue.btc.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeBookWidget;
import io.github.tobyrue.btc.client.screen.recipe_book.ScrollTableRecipeInput;
import io.github.tobyrue.btc.recipes.ScrollTableRecipe;
import io.github.tobyrue.btc.regestries.ModRecipes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ScrollTableScreen extends HandledScreen<ScrollTableScreenHandler> implements RecipeBookProvider {
    private static final Identifier TEXTURE = Identifier.of("btc", "textures/gui/scroll_table.png");

    private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
    private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");

    private final ScrollTableRecipeBookWidget recipeBook = new ScrollTableRecipeBookWidget();
    private boolean narrow;

    private float displayedProgress = 0.0F;
    private int displayedLevel = 0;

    public ScrollTableScreen(ScrollTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 200;
        this.playerInventoryTitleY = (this.backgroundHeight / 2) + 4;
    }

    @Override
    protected void init() {
        super.init();
        this.narrow = this.width < 379;

        this.recipeBook.initialize(this.width, this.height, this.client, this.narrow, this.handler);
        this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);

        this.addDrawableChild(new TexturedButtonWidget(this.x + 5, this.height / 2 - 32, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, (button) -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 5, this.height / 2 - 32);
        }));

        this.addSelectableChild(this.recipeBook);
        this.setInitialFocus(this.recipeBook);

        if (this.client != null && this.client.player != null) {
            this.displayedProgress = this.client.player.experienceProgress;
            this.displayedLevel = this.client.player.experienceLevel;
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        this.recipeBook.update();

        if (this.client != null && this.client.player != null) {
            float targetProgress = this.client.player.experienceProgress;
            int targetLevel = this.client.player.experienceLevel;

            if (this.displayedLevel != targetLevel || Math.abs(this.displayedProgress - targetProgress) > 0.001f) {
                if (this.displayedLevel < targetLevel) {
                    this.displayedProgress += 0.08f;
                    if (this.displayedProgress >= 1.0f) {
                        this.displayedProgress = 0.0f;
                        this.displayedLevel++;
                        playTickSound();
                    }
                } else if (this.displayedLevel > targetLevel) {
                    this.displayedProgress -= 0.08f;
                    if (this.displayedProgress < 0.0f) {
                        this.displayedProgress = 0.99f;
                        this.displayedLevel--;
                        playTickSound();
                    }
                } else {
                    float diff = targetProgress - this.displayedProgress;
                    if (Math.abs(diff) > 0.005f) {
                        this.displayedProgress += diff * 0.25f;
                    } else {
                        this.displayedProgress = targetProgress;
                    }
                }
            }
        }
    }

    private void playTickSound() {
        if (this.client != null && this.client.player != null) {
            this.client.world.playSound(
                    this.client.player,
                    this.client.player.getBlockPos(),
                    net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),
                    net.minecraft.sound.SoundCategory.PLAYERS,
                    0.3f,
                    1.2f
            );
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.x;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        if (this.client != null && this.client.player != null) {
            int barWidth = 162;
            int barHeight = 5;

            int xpBarX = x + 7;
            int xpBarY = y + 95;

            RenderSystem.enableBlend();

            context.drawGuiTexture(EXPERIENCE_BAR_BACKGROUND_TEXTURE, xpBarX, xpBarY, barWidth, barHeight);

            ScrollTableRecipe activeRecipe = getActiveRecipe();
            boolean hasEnough = true;
            if (activeRecipe != null && activeRecipe.getExperience() != null) {
                hasEnough = hasEnoughExperience(this.client.player, activeRecipe.getExperience());
            }

            if (!hasEnough) {
                RenderSystem.setShaderColor((float) 255/255, (float) 40/255, (float) 40/255, 1.0F);
                context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE, barWidth, barHeight, 0, 0, xpBarX, xpBarY, barWidth, barHeight);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                int progressWidth = (int) (this.displayedProgress * (barWidth + 1));
                if (progressWidth > 0) {
                    context.drawGuiTexture(EXPERIENCE_BAR_PROGRESS_TEXTURE, barWidth, barHeight, 0, 0, xpBarX, xpBarY, progressWidth, barHeight);
                }
            }

            RenderSystem.disableBlend();

            String levelStr = String.valueOf(this.displayedLevel);


            if (activeRecipe != null && !this.client.player.isCreativeLevelTwoOp()) {
                var expReq = activeRecipe.getExperience();
                int textY = y + 105;

                String costStr = String.valueOf(expReq.amount());

                int textX = x + this.backgroundWidth/2 - this.textRenderer.getWidth(levelStr) - this.textRenderer.getWidth("-")/2 - 2;
                int minusX = x + (this.backgroundWidth - this.textRenderer.getWidth("-")) / 2;
                int costX = x + this.backgroundWidth/2 + this.textRenderer.getWidth("-")/2 + 2;


                context.drawText(this.textRenderer, levelStr, textX + 1, textY, 0, false);
                context.drawText(this.textRenderer, levelStr, textX - 1, textY, 0, false);
                context.drawText(this.textRenderer, levelStr, textX, textY + 1, 0, false);
                context.drawText(this.textRenderer, levelStr, textX, textY - 1, 0, false);
                context.drawText(this.textRenderer, levelStr, textX, textY, 8453920, false);


                context.drawText(this.textRenderer, "-", minusX + 1, textY, 0, false);
                context.drawText(this.textRenderer, "-", minusX - 1, textY, 0, false);
                context.drawText(this.textRenderer, "-", minusX, textY + 1, 0, false);
                context.drawText(this.textRenderer, "-", minusX, textY - 1, 0, false);
                context.drawText(this.textRenderer, "-", minusX, textY, 0xFF5555, false);


                context.drawText(this.textRenderer, costStr, costX + 1, textY, 0, false);
                context.drawText(this.textRenderer, costStr, costX - 1, textY, 0, false);
                context.drawText(this.textRenderer, costStr, costX, textY + 1, 0, false);
                context.drawText(this.textRenderer, costStr, costX, textY - 1, 0, false);
                context.drawText(this.textRenderer, costStr, costX, textY, 0xFF5555, false);
            } else {
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

    private ScrollTableRecipe getActiveRecipe() {
        if (this.client == null || this.client.world == null) return null;
        ItemStack[] inputs = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            inputs[i] = this.handler.getSlot(i).getStack();
        }
        ScrollTableRecipeInput input = new ScrollTableRecipeInput(inputs);
        Optional<RecipeEntry<Recipe<ScrollTableRecipeInput>>> match =
                this.client.world.getRecipeManager().getFirstMatch(ModRecipes.SCROLL_TABLE_RECIPE_TYPE, input, this.client.world);

        if (match.isPresent() && match.get().value() instanceof ScrollTableRecipe recipe) {
            return recipe;
        }
        return null;
    }

    private boolean hasEnoughExperience(net.minecraft.entity.player.PlayerEntity player, ScrollTableRecipe.ExperienceRequirement req) {
        if (player.isCreativeLevelTwoOp()) return true;
        if (req.type() == ScrollTableRecipe.ExperienceType.LEVELS) {
            return player.experienceLevel >= req.amount();
        } else {
            return player.totalExperience >= req.amount();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.recipeBook.isOpen() && this.narrow) {
            this.renderBackground(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
        } else {
            super.render(context, mouseX, mouseY, delta);
            this.recipeBook.render(context, mouseX, mouseY, delta);
            this.recipeBook.drawGhostSlots(context, this.x, this.y, true, delta);
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);
        this.recipeBook.drawTooltip(context, this.x, this.y, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBook.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(this.recipeBook);
            return true;
        } else {
            return this.narrow && this.recipeBook.isOpen() ? true : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
        if (slot != null && slot.getIndex() < 11) {
            this.recipeBook.slotClicked(slot);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.recipeBook.keyPressed(keyCode, scanCode, modifiers) ? true : super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return this.recipeBook.charTyped(chr, modifiers) ? true : super.charTyped(chr, modifiers);
    }

    @Override
    protected boolean isPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY) {
        return (!this.narrow || !this.recipeBook.isOpen()) && super.isPointWithinBounds(x, y, width, height, pointX, pointY);
    }

    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        return this.recipeBook.isClickOutsideBounds(mouseX, mouseY, this.x, this.y, this.backgroundWidth, this.backgroundHeight, button) && bl;
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }
}
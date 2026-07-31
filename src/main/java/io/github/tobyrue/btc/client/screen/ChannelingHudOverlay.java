package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.spell.MinimalPredefinedSpellsItem;
import io.github.tobyrue.btc.spell.Spell;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.List;

public class ChannelingHudOverlay implements HudRenderCallback {

    private static final Identifier XP_BAR_BG_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
    private static final Identifier XP_BAR_FG_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");

    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (!ChannelHudState.isActive()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        ItemStack spellStack = null;
        if (isSpellItem(mainHand, ChannelHudState.getActiveSourceItemId())) {
            spellStack = mainHand;
        } else if (isSpellItem(offHand, ChannelHudState.getActiveSourceItemId())) {
            spellStack = offHand;
        }

        if (spellStack == null || spellStack.isEmpty()) {
            return;
        }

        int selectedSpellIndex = getSelectedSpellIndex(spellStack, player);
        int activeSpellIndex = ChannelHudState.getActiveSpellIndex();

        boolean shouldRenderActive = (selectedSpellIndex == activeSpellIndex) || !isSpellIndexChanneling(spellStack, selectedSpellIndex);

        if (!shouldRenderActive) {
            return;
        }

        int screenWidth = drawContext.getScaledWindowWidth();
        int screenHeight = drawContext.getScaledWindowHeight();
        TextRenderer font = client.textRenderer;

        int barWidth = 140;
        int barHeight = 5;
        int x = (screenWidth - barWidth) / 2;
        int y = 10;

        float spellProgress = ChannelHudState.getSpellProgress();
        float cancelProgress = ChannelHudState.getCancelProgress();

        int filledSpellWidth = (int) (barWidth * spellProgress);
        int filledCancelWidth = (int) (barWidth * cancelProgress);

        int rawColor = ChannelHudState.getSpellColor();
        float r = ((rawColor >> 16) & 0xFF) / 255.0f;
        float g = ((rawColor >> 8) & 0xFF) / 255.0f;
        float b = (rawColor & 0xFF) / 255.0f;

        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        drawContext.drawGuiTexture(XP_BAR_BG_TEXTURE, x, y, barWidth, barHeight);

        if (filledSpellWidth > 0) {
            drawContext.setShaderColor(r, g, b, 1.0f);
            drawContext.drawGuiTexture(XP_BAR_FG_TEXTURE, barWidth, barHeight, 0, 0, x, y, filledSpellWidth, barHeight);
        }

        if (filledCancelWidth > 0) {
            Color invColor = invertColorInt(rawColor);
            drawContext.setShaderColor(invColor.getRed() / 255.0f, invColor.getGreen() / 255.0f, invColor.getBlue() / 255.0f, 1.0f);
            drawContext.drawGuiTexture(XP_BAR_FG_TEXTURE, barWidth, barHeight, 0, 0, x, y, filledCancelWidth, barHeight);
        }

        drawContext.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        Text displayText = ChannelHudState.getDisplayText();
        int textWidth = font.getWidth(displayText);
        drawContext.drawTextWithShadow(font, displayText, (screenWidth - textWidth) / 2, y + 11, 0xFFFFFFFF);
    }

    private boolean isSpellItem(ItemStack stack, String requiredItemId) {
        if (stack.isEmpty()) return false;
        Identifier id = Registries.ITEM.getId(stack.getItem());
        return id.toString().equals(requiredItemId);
    }

    /**
     * Resolves the selected spell index on the client side matching the server logic.
     */
    private int getSelectedSpellIndex(ItemStack stack, PlayerEntity player) {
        if (stack.getItem() instanceof MinimalPredefinedSpellsItem minimalItem) {
            List<Spell.InstancedSpell> availableSpells = minimalItem.getAvailableSpells(stack, player.getWorld(), player);

            for (int i = 0; i < availableSpells.size(); i++) {
                Spell.InstancedSpell instancedSpell = availableSpells.get(i);
                if (instancedSpell.spell().getTranslationKey().equals(ChannelHudState.getSpellNameKey())) {
                    return i;
                }
            }
        }
        return 0;
    }

    /**
     * Helper to verify if a specific spell index on an item is actively channeling.
     */
    private boolean isSpellIndexChanneling(ItemStack stack, int spellIndex) {
        return ChannelHudState.isActive()
                && ChannelHudState.getActiveSpellIndex() == spellIndex;
    }

    public static Color invertColorInt(int color) {
        int colorInt = color ^ 0x00FFFFFF;
        int red   = (colorInt >> 16) & 0xFF;
        int green = (colorInt >> 8) & 0xFF;
        int blue  = colorInt & 0xFF;

        return new Color(red, green, blue);
    }
}
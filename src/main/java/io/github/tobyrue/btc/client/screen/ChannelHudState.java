package io.github.tobyrue.btc.client.screen;

import io.github.tobyrue.btc.spell.ChanneledSpell.InterruptReason;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ChannelHudState {
    private static boolean active = false;
    private static String spellNameKey = "";
    private static int currentTick = 0;
    private static int maxTicks = 100;
    private static int cancelMask = 0;
    private static int spellColor = 0xFF00E5FF;
    private static int currentHoldTicks = 0;
    private static int requiredHoldTicks = 1;

    private static String activeSourceItemId = "";
    private static int activeSpellIndex = -1;

    public static void update(boolean isActive, String nameKey, int tick, int max, int mask, int color,
                              int holdTicks, int reqHold, String sourceItemId, int spellIndex) {
        active = isActive;
        spellNameKey = nameKey;
        currentTick = tick;
        maxTicks = max;
        cancelMask = mask;
        spellColor = color;
        currentHoldTicks = holdTicks;
        requiredHoldTicks = reqHold;
        activeSourceItemId = sourceItemId;
        activeSpellIndex = spellIndex;
    }

    public static void clear() {
        active = false;
        activeSourceItemId = "";
        activeSpellIndex = -1;
    }

    public static boolean isActive() { return active; }
    public static int getSpellColor() { return spellColor; }
    public static String getActiveSourceItemId() { return activeSourceItemId; }
    public static int getActiveSpellIndex() { return activeSpellIndex; }
    public static String getSpellNameKey() { return spellNameKey; }

    public static float getSpellProgress() {
        return Math.min(1.0f, (float) currentTick / Math.max(1, maxTicks));
    }

    public static float getCancelProgress() {
        if (!hasPurposefulCancel() || requiredHoldTicks <= 0 || currentHoldTicks <= 0) return 0.0f;
        return Math.min(1.0f, (float) currentHoldTicks / requiredHoldTicks);
    }

    public static boolean hasPurposefulCancel() {
        return cancelMask != 0;
    }

    public static Text getDisplayText() {
        Text spellTitle = Text.translatable(spellNameKey);
        if (!hasPurposefulCancel()) {
            return spellTitle;
        }

        List<Text> actions = new ArrayList<>();

        if ((cancelMask & (1 << InterruptReason.CROUCHED.ordinal())) != 0) {
            actions.add(Text.translatable("hud.btc.cancel.crouch"));
        }
        if ((cancelMask & (1 << InterruptReason.CLICK.ordinal())) != 0) {
            actions.add(Text.translatable("hud.btc.cancel.click"));
        }

        if (actions.isEmpty()) {
            return spellTitle;
        }

        Text combinedActions = actions.get(0);
        for (int i = 1; i < actions.size(); i++) {
            combinedActions = Text.translatable("hud.btc.cancel.separator", combinedActions, actions.get(i));
        }

        return Text.translatable("hud.btc.cancel.prompt", spellTitle, combinedActions);
    }
}
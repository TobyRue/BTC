package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public interface UpgradableSpell {
    List<Pair<Identifier, Text>> getUpgradeDescriptions();
    HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(final GrabBag args);

    static void withIntegerUpgrade(final GrabBag args, final HashMap<Identifier, Pair<String, ?>> upgrades, final String name, final int defaultVal, final int min, final int max, final int delta, final Identifier id) {
        int currentVal = args.getInt(name, defaultVal);
        int newVal = currentVal + delta;

        if (newVal >= min && newVal <= max) {
            upgrades.put(id, new Pair<>(name, newVal));
        }
    }

    static void withDoubleUpgrade(final GrabBag args, final HashMap<Identifier, Pair<String, ?>> upgrades, final String name, final double defaultVal, final double min, final double max, final double delta, final Identifier id) {
        double currentVal = args.getDouble(name, defaultVal);
        double newVal = currentVal + delta;

        if (newVal >= min && newVal <= max) {
            upgrades.put(id, new Pair<>(name, newVal));
        }
    }

    static void withFloatUpgrade(final GrabBag args, final HashMap<Identifier, Pair<String, ?>> upgrades, final String name, final float defaultVal, final float min, final float max, final float delta, final Identifier id) {
        float currentVal = args.getFloat(name, defaultVal);
        float newVal = currentVal + delta;

        if (newVal >= min && newVal <= max) {
            upgrades.put(id, new Pair<>(name, newVal));
        }
    }
}

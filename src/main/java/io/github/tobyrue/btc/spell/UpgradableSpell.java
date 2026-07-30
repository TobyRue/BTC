package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.function.Function;

public interface UpgradableSpell {
    HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(final GrabBag args);

    static void withIntegerUpgrade(final GrabBag args, final HashMap<Identifier, Pair<String, ?>> upgrades, final String name, final int defaultVal, final int min, final int max, final int delta, final Identifier id) {
        int currentVal = args.getInt(name, defaultVal);
        int newVal = currentVal + delta;

        if (newVal >= min && newVal <= max) {
            upgrades.put(id, new Pair<>(name, newVal));
        }
    }
}

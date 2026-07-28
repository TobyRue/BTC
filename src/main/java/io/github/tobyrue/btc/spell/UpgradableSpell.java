package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.function.Function;

public interface UpgradableSpell {
    HashMap<ItemStack, Pair<String, ?>> getUpgradeOptions(final GrabBag args);

    static void withIntegerUpgrade(final GrabBag args, final HashMap<ItemStack, Pair<String, ?>> upgrades, final String name, final int min, final int max, final int delta, final ItemStack stack) {
        if (Integer.class.equals(args.getType(name)) && (delta <= 0 || args.getInt(name) + delta <= max) && (delta >= 0 || args.getInt(name) + delta >= min)) upgrades.put(stack, new Pair<>(name, args.getInt(name) + delta));
    }
}

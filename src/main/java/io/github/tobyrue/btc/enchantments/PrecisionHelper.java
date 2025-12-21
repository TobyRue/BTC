package io.github.tobyrue.btc.enchantments;

import io.github.tobyrue.btc.regestries.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public final class PrecisionHelper {

    private PrecisionHelper() {}

    public static float applyPrecision(ItemStack stack, float divergence) {
        int level = ModEnchantments.getLevel(stack, ModEnchantments.PRECISION);

        if (level <= 0) return divergence;

        float multiplier = 1.0f - (level * 0.1f);

        return divergence * Math.max(multiplier, 0.5f);
    }
}

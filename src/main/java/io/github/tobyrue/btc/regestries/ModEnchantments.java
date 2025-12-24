package io.github.tobyrue.btc.regestries;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> INFUSION = of("infusion");

    public static final RegistryKey<Enchantment> PRECISION = of("precision");
    public static final RegistryKey<Enchantment> VELOCITY = of("velocity");

    private static RegistryKey<Enchantment> of(String path) {
        Identifier id = BTC.identifierOf(path);
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, BTC.identifierOf(id), codec);
    }

    public static int getLevel(ItemStack item, RegistryKey<Enchantment> key) {
        return Optional.ofNullable(item.get(DataComponentTypes.ENCHANTMENTS))
                .flatMap(component -> component.getEnchantments().stream()
                        .filter(holder -> holder.matchesKey(key))
                        .findFirst()
                        .map(component::getLevel))
                .orElse(-1);
    }

    public static void registerModEnchantmentEffects() {
    }
}

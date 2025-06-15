package io.github.tobyrue.btc.regestries;

import com.mojang.serialization.MapCodec;
import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enchantments.InfusionEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEnchantments {
    public static final RegistryKey<Enchantment> INFUSION = of("infusion");
//    public static MapCodec<InfusionEnchantment> INFUSION_ENCHANTMENT = register("infusion", InfusionEnchantment.CODEC);

    private static RegistryKey<Enchantment> of(String path) {
        Identifier id = BTC.identifierOf(path);
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, id);
    }

    private static <T extends EnchantmentEntityEffect> MapCodec<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, BTC.identifierOf(id), codec);
    }

    public static void registerModEnchantmentEffects() {
    }
}

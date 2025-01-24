package io.github.tobyrue.btc;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;

public class DrowningEffect extends StatusEffect {

    protected DrowningEffect() {
        super(StatusEffectCategory.HARMFUL, 0x125184);
    }


//TODO


    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Determine the interval for applying the effect based on the amplifier
        int interval = Math.max(40 - (amplifier * 5), 5); // Minimum interval of 5 ticks
        return duration % interval == 0;
    }
//    public static int getTotalRespiration(PlayerEntity player) {
//        int totalRespiration = 0;
//    Iterable<ItemStack> armorItem = player.getArmorItems();
//        // Loop through the player's armor items
//        for (ItemStack armorItem : player.getArmorItems()) {
//            if (!armorItem.isEmpty()) { // Check if the armor slot has an item
//                var enchantmentId = armorItem.getEnchantments().equals(Enchantments.RESPIRATION.getValue());            }
//        }
//
//        return totalRespiration;
//    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.hasStatusEffect(StatusEffects.WATER_BREATHING)) {

            entity.damage(entity.getWorld().getDamageSources().drown(), 4);
            return true;
        }
        return true;
    }
}

package io.github.tobyrue.btc;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Hand;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public class DrowningEffect extends StatusEffect {

    protected DrowningEffect() {
        super(StatusEffectCategory.HARMFUL, 0x125184);
    }
    

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // Determine the interval for applying the effect based on the amplifier
        int interval = Math.max(40 - (amplifier * 5), 5); // Minimum interval of 5 ticks
        return duration % interval == 0;
    }
    private static final Random RANDOM = new Random();

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        int oxygenBonus = (int) entity.getAttributeInstance(EntityAttributes.GENERIC_OXYGEN_BONUS).getValue();
        if (!entity.hasStatusEffect(StatusEffects.WATER_BREATHING) && RANDOM.nextInt(oxygenBonus + 1) == 0) {
            entity.damage(entity.getWorld().getDamageSources().drown(), 4);
//            ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.HEAD);
//            int level = itemStack.getEnchantments().getEnchantmentEntries().stream()
//                    .filter(e->e.getKey() == Enchantments.RESPIRATION)
//                    .map(e->EnchantmentHelper.getLevel(e.getKey(), itemStack)).findFirst().orElseGet(()->54);
            return true;
        }
        return true;
    }
}

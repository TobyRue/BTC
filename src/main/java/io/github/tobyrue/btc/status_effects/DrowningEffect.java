package io.github.tobyrue.btc.status_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Random;

public class DrowningEffect extends StatusEffect {

    public DrowningEffect() {
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
            return true;
        }
        return true;
    }
}

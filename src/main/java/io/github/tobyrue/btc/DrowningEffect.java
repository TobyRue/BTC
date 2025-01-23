package io.github.tobyrue.btc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class DrowningEffect extends StatusEffect {

    protected DrowningEffect() {
        super(StatusEffectCategory.HARMFUL, 0x125184);
    }

    @Override
    public void onApplied(AttributeContainer attributeContainer, int amplifier) {
        super.onApplied(attributeContainer, amplifier);
    }
//TODO
    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        super.onRemoved(attributeContainer);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // You can control the frequency of the effect. Returning true means it updates regularly.
        return true;
    }
}

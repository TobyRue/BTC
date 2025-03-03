package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.BTC;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class MinerMishapEffect extends StatusEffect {
    public MinerMishapEffect() {
        super(StatusEffectCategory.HARMFUL, 0x822700);
        addAttributeModifier(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, Identifier.of(BTC.MOD_ID, "effect.miner_mishap"), -1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}

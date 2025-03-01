package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.BTC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class AntiMineEffect extends StatusEffect {
    public AntiMineEffect() {
        super(StatusEffectCategory.HARMFUL, 0x822700);
        addAttributeModifier(EntityAttributes.PLAYER_BLOCK_BREAK_SPEED, Identifier.of(BTC.MOD_ID, "effect.anti_mine"), -1, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}

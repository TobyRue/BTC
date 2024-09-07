package io.github.tobyrue.btc;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.util.List;

import static io.github.tobyrue.btc.BTC.ANTI_PLACE;

public class AntiPlaceEffect extends StatusEffect {
    protected AntiPlaceEffect() {
        // category: StatusEffectCategory - describes if the effect is helpful (BENEFICIAL), harmful (HARMFUL) or useless (NEUTRAL)
        // color: int - Color is the color assigned to the effect (in RGB)
        super(StatusEffectCategory.HARMFUL, 0xe9b8b3);
    }

    // Called every tick to check if the effect can be applied or not
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        // In our case, we just make it return true so that it applies the effect every tick
        return true;
    }

    // Called when the effect is applied.
    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity && !((PlayerEntity) entity).isCreative()) {
            ((PlayerEntity) entity).addExperience(1 << amplifier); // Higher amplifier gives you experience faster
            UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
                if(player instanceof ServerPlayerEntity) {
                    System.out.println("Block placement prevented due to effect");
                    return ActionResult.FAIL;
                }
                return ActionResult.SUCCESS;
            });
        }
        return super.applyUpdateEffect(entity, amplifier);
    }

}

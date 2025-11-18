package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModMaps;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;

public class SpellOfDissolution extends ChanneledSpell {

    public SpellOfDissolution() {
        super(SpellTypes.GENERIC, 200, 1, new Disturb(DistributionLevels.DAMAGE_CROUCH_AND_MOVE, 200, 12), true, ParticleTypes.REVERSE_PORTAL, ParticleAnimation.SPIRAL);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0x8833FF;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick) {
        LivingEntity user = ctx.user();

        // Gather CURRENT harmful effects only
        List<StatusEffectInstance> harmful = new ArrayList<>();

        for (StatusEffectInstance eff : user.getStatusEffects()) {
            if (eff.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
                harmful.add(eff);
            }
        }

        // No harmful effects left → nothing to do
        if (harmful.isEmpty()) return;

        // Calculate interval based on *current* harmful effect amount
        int interval = Math.max(1, castTime / harmful.size());

        // Only run removal at interval ticks
        if (tick % interval != 0 || tick == 0) return;

        // Choose random harmful effect to remove
        StatusEffectInstance chosen = harmful.get(user.getRandom().nextInt(harmful.size()));
        RegistryEntry<StatusEffect> type = chosen.getEffectType();

        boolean removed = user.removeStatusEffect(type);
        System.out.println("Removed? " + removed + " -> " + type.getIdAsString());

        // Add opposite if applicable
        RegistryEntry<StatusEffect> opposite = ModMaps.EFFECT_OPPOSITES.get(type);
        if (opposite != null && opposite.value().isBeneficial()) {
            System.out.println("Effect Added: " + opposite.getIdAsString());
            user.addStatusEffect(new StatusEffectInstance(
                    opposite,
                    Math.max(40, (int)(chosen.getDuration() * 0.8)),  // optional scaling
                    Math.max(0, chosen.getAmplifier() / 2)             // half strength
            ));
        }
    }

}

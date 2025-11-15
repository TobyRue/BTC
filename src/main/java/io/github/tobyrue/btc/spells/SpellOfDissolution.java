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
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.WeakHashMap;

public class SpellOfDissolution extends ChanneledSpell {
    private static final WeakHashMap<LivingEntity, List<RegistryEntry<StatusEffect>>> STORED_EFFECTS = new WeakHashMap<>();

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

        STORED_EFFECTS.putIfAbsent(user, new ArrayList<>());
        List<RegistryEntry<StatusEffect>> stored = STORED_EFFECTS.get(user);

        if (tick == 0) {
            stored.clear();

            for (StatusEffectInstance eff : user.getStatusEffects()) {
                RegistryEntry<StatusEffect> type = eff.getEffectType();
                if (type.value().getCategory() == StatusEffectCategory.HARMFUL) {
                    stored.add(type);
                }
            }
        }

        if (stored.isEmpty()) return;

        int interval = Math.max(1, castTime / stored.size());

        if (tick % interval == 0 && tick != 0) {

            RegistryEntry<StatusEffect> type = stored.remove(0);

            boolean removed = user.removeStatusEffect(type);
            System.out.println("Removed? " + removed + " -> " + type.getIdAsString());

            RegistryEntry<StatusEffect> opposite = ModMaps.EFFECT_OPPOSITES.get(type);
            System.out.println("Effect Removed: " + type);

            if (opposite != null && opposite.value().isBeneficial()) {
                System.out.println("Effect Added: " + opposite.getIdAsString());
                user.addStatusEffect(
                        new StatusEffectInstance(opposite, 200, 0)
                );
            }
        }
    }
}

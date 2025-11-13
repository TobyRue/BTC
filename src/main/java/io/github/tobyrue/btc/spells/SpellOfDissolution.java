package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.WeakHashMap;

public class SpellOfDissolution extends ChanneledSpell {
    private static final WeakHashMap<LivingEntity, List<StatusEffectInstance>> STORED_EFFECTS = new WeakHashMap<>();

    public SpellOfDissolution() {
        super(SpellTypes.GENERIC, 200, 1, DistributionLevels.DAMAGE_CROUCH_AND_MOVE, true, ParticleTypes.REVERSE_PORTAL, ParticleAnimation.SPIRAL);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0x8833FF;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick) {
        var user = ctx.user();
        var world = ctx.world();
        var pos = ctx.pos();

        Collection<StatusEffectInstance> active = user.getStatusEffects();

        // Filter to only negative effects
        for (StatusEffectInstance eff : active) {
            if (eff.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) continue;
            List<StatusEffectInstance> s = List.of();
            if (tick <= 1) {
                STORED_EFFECTS.put(user, List.of());
            }

            STORED_EFFECTS.get(user).add(eff);
        }

        int interval = Math.max(1, STORED_EFFECTS.isEmpty() ? 1 : castTime / STORED_EFFECTS.get(user).size());

        if (tick % interval == 0) {
            StatusEffectInstance toRemove = STORED_EFFECTS.get(user).getFirst();
            user.removeStatusEffect(toRemove.getEffectType());
            System.out.println("Removed Effect: " + toRemove);
            STORED_EFFECTS.get(user).removeFirst();
//            if (user.getWorld() instanceof ServerWorld server) {
//                server.spawnParticles(
//                        net.minecraft.particle.ParticleTypes.REVERSE_PORTAL,
//                        user.getX(), user.getY() + user.getHeight() / 2.0, user.getZ(),
//                        10, 0.3, 0.5, 0.3, 0.05
//                );
//            }
        }
    }
}

package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.Comparator;
import java.util.List;

public class LuminaryEmpowerSpell extends Spell {

    public LuminaryEmpowerSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFFB86CFF;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {

        if (!(ctx.world() instanceof ServerWorld serverWorld)) {
            return;
        }

        if (!(ctx.user() instanceof EldritchLuminaryEntity caster)) {
            return;
        }

        double radius =
                args.getDouble("radius", 18.0D);

        int duration =
                args.getInt("duration", 140);

        int amplifier =
                Math.max(
                        0,
                        args.getInt("amplifier", 0)
                );

        int maxTargets =
                Math.max(
                        1,
                        args.getInt("maxTargets", 4)
                );

        Box box =
                caster.getBoundingBox()
                        .expand(radius);

        List<EldritchLuminaryEntity> allies =
                serverWorld.getEntitiesByClass(
                                EldritchLuminaryEntity.class,
                                box,
                                entity ->
                                        entity != caster
                                                && entity.isAlive()
                        )
                        .stream()
                        .sorted(
                                Comparator.comparingDouble(caster::squaredDistanceTo)
                        )
                        .limit(maxTargets)
                        .toList();

        if (allies.isEmpty()) {
            return;
        }

        for (EldritchLuminaryEntity ally : allies) {


            ally.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.STRENGTH,
                            duration,
                            amplifier
                    )
            );

            ally.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.RESISTANCE,
                            duration,
                            amplifier
                    )
            );


            ally.addStatusEffect(
                    new StatusEffectInstance(
                            StatusEffects.SPEED,
                            duration,
                            amplifier
                    )
            );


            if (ally.getHealth()
                    < ally.getMaxHealth() * 0.65F) {

                ally.addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.REGENERATION,
                                Math.min(duration, 100),
                                0
                        )
                );
            }

            serverWorld.spawnParticles(
                    ParticleTypes.ENCHANTED_HIT,
                    ally.getX(),
                    ally.getY() + ally.getHeight() * 0.5D,
                    ally.getZ(),
                    10,
                    0.35D,
                    0.5D,
                    0.35D,
                    0.02D
            );

            serverWorld.spawnParticles(
                    ParticleTypes.HAPPY_VILLAGER,
                    ally.getX(),
                    ally.getY() + ally.getHeight(),
                    ally.getZ(),
                    6,
                    0.3D,
                    0.25D,
                    0.3D,
                    0.01D
            );
        }

        serverWorld.playSound(
                null,
                caster.getBlockPos(),
                SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                SoundCategory.HOSTILE,
                1.0F,
                0.8F + caster.getRandom().nextFloat() * 0.2F
        );
    }
}
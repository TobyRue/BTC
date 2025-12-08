package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class WaterWaveSpell extends ChanneledSpell {
    public WaterWaveSpell() {
        super(SpellTypes.WATER, 40, 1, new ChanneledSpell.Disturb(ChanneledSpell.DistributionLevels.CLICK, -1, -1, 20), true, ParticleTypes.ENCHANTED_HIT, ChanneledSpell.ParticleAnimation.SPIRAL, 0, false);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, Start start) {
        double maxRadius = args.getDouble("maxRadius", 8d);
        int amplifier = args.getInt("amplifier", 1);
        int maxDuration = args.getInt("maxDuration", 600);
        int duration = args.getInt("castTime", this.castTime);

        var storedPos = start.pos();
        if (ctx.world() instanceof ServerWorld serverWorld) {
            double progress = tick / (double) (duration);
            double radius = maxRadius * progress;

            int count = (int) (maxRadius / 64d * 4096d);
            for (int i = 0; i < count; i++) {

                double angle = (2 * Math.PI / count) * i;

                double x = storedPos.getX() + Math.sin(angle) * radius;
                double z = storedPos.getZ() + Math.cos(angle) * radius;

                double yOffset = 0.2;
                double y = storedPos.getY() + yOffset;
                y += Math.sin(radius) * 0.7;

                double xSpeed = Math.sin(angle) * 0.2;
                double zSpeed = Math.cos(angle) * 0.2;

                serverWorld.spawnParticles(BTC.WATER_DROP, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
            }

            for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                double dist = target.getPos().distanceTo(storedPos);

                double stepSize = maxRadius / duration;
                if (dist <= radius && dist > (radius - stepSize)) {
                    target.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DROWNING, maxDuration, amplifier));
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("water_wave"));
    }
}

package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class WaterWaveSpell extends Spell {
    public WaterWaveSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
    }

    @Override
    public void use(final Spell.SpellContext ctx, final GrabBag args) {
        int duration = args.getInt("duration", 2);
        double maxRadius = args.getDouble("maxRadius", 8D);
        int amplifier = args.getInt("amplifier", 1);
        int maxDuration = args.getInt("maxDuration", 600);

        Vec3d storedPos = ctx.user().getPos();
        ((Ticker.TickerTarget) ctx.user()).add(Ticker.forSeconds((ticks) -> {
            if (ctx.world() instanceof ServerWorld serverWorld) {
                double progress = ticks / (double) (duration * 20);
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
        }, duration));
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

package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class FireStormSpell extends Spell {
    protected final int duration;
    protected final double maxRadius;
    protected final int cooldown;

    public FireStormSpell(int duration, double maxRadius, int cooldown) {
        super(0x0, SpellTypes.FIRE);
        this.duration = duration;
        this.maxRadius = maxRadius;
        this.cooldown = cooldown;
    }

    @Override
    public void use(final Spell.SpellContext ctx) {
        Vec3d storedPos = ctx.user().getPos();
        ((Ticker.TickerTarget) ctx.user()).add(Ticker.forSeconds((ticks) -> {
            if (ctx.world() instanceof ServerWorld serverWorld) {
                double progress = ticks / (double) (duration * 20);
                double radius = maxRadius * progress;


                int count = (int) (maxRadius / 64d * 1280d);
                for (int i = 0; i < count; i++) {

                    double angle = (2 * Math.PI / count) * i;

                    double x = storedPos.getX() + Math.sin(angle) * radius;
                    double z = storedPos.getZ() + Math.cos(angle) * radius;

                    double yOffset = 0.2;
                    double y = storedPos.getY() + yOffset;

                    double xSpeed = Math.sin(angle) * 0.2;
                    double zSpeed = Math.cos(angle) * 0.2;

                    serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
                }

                for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                    double dist = target.getPos().distanceTo(storedPos);

                    double stepSize = maxRadius / duration;
                    if (dist <= radius && dist > (radius - stepSize)) {
                        target.setOnFireFor((float) ((radius * -1) + maxRadius));
                        target.damage(ctx.user().getDamageSources().inFire(), Math.min(8, (float) ((radius * -1) + maxRadius)));
                    }
                }
            }
        }, duration));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx) {
        return ctx.user() != null && super.canUse(ctx);
    }

    @Override
    public Spell.SpellCooldown getCooldown() {
        return new Spell.SpellCooldown(cooldown, BTC.identifierOf("fire_storm"));
    }
}

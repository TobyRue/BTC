package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

public abstract class ChanneledSpell extends Spell {
    private final int castTime;
    private final int intervalTicks;
    private final boolean canBeDisturbed;
    private final boolean showParticles;
    private final ParticleEffect particleType;

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, boolean canBeDisturbed, boolean showParticles, ParticleEffect particleType) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.canBeDisturbed = canBeDisturbed;
        this.showParticles = showParticles;
        this.particleType = particleType;
    }

    @Override
    public abstract int getColor(GrabBag args);

    @Override
    protected final void use(SpellContext ctx, GrabBag args) {
        var startHealth = ctx.user().getHealth();
        ((Ticker.TickerTarget) (ctx.user())).add(
                Ticker.forTicks(tick -> {
                    if (tick % intervalTicks == 0) {
                        if (!canBeDisturbed) {
                            useChanneled(ctx, args, tick);
                        } else {
                            if (startHealth == ctx.user().getHealth()) {
                                useChanneled(ctx, args, tick);
                            } else {
                                return true;
                            }
                        }
                    }
                    if (showParticles) {
                        spawnChannelParticles(ctx.user(), tick, castTime, args);
                    }
                    return false;
                }, castTime)
        );
    }

    protected abstract void useChanneled(final SpellContext ctx, final GrabBag args, final int tick);

    protected final void spawnChannelParticles(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (world.isClient) return;

        double height = entity.getHeight();
        double width = entity.getWidth();
        double radius = width * 0.75;
        int steps = args.getInt("particleCount", 20);


        double progress = (double) tick / totalDuration;
        double yOffset = progress * height;

        double angle = (tick * 0.3) % (Math.PI * 2);

        for (int i = 0; i < steps; i++) {
            double theta = angle + (2 * Math.PI * i / steps);
            double x = entity.getX() + Math.cos(theta) * radius;
            double y = entity.getY() + yOffset;
            double z = entity.getZ() + Math.sin(theta) * radius;

            float r = ((getColor(args) >> 16) & 0xFF) / 255.0F;
            float g = ((getColor(args) >> 8) & 0xFF) / 255.0F;
            float b = (getColor(args) & 0xFF) / 255.0F;

            if (particleType == null) {
                if (!world.isClient) {
                    ((ServerWorld) world).spawnParticles(
                            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), // particle type
                            x, y + 0.1, z, // position
                            1,                          // count
                            0, 0, 0,                    // offset (spread)
                            0                           // speed
                    );
                } else {
                    world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b),
                            x, y, z, 0, 0, 0);
                }
            } else {
                if (!world.isClient) {
                    ((ServerWorld) world).spawnParticles(
                            particleType, // particle type
                            x, y + 0.1, z, // position
                            1,                          // count
                            0, 0, 0,                    // offset (spread)
                            0                           // speed
                    );
                } else {
                    world.addParticle(particleType,
                            x, y, z, 0, 0, 0);
                }
            }
        }
    }

    @Override
    protected boolean canUse(SpellContext ctx, GrabBag args) {
        return ctx.user() instanceof LivingEntity && super.canUse(ctx, args);
    }
}

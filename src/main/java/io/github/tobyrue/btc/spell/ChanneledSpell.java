package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public abstract class ChanneledSpell extends Spell {
    protected final int castTime;
    protected final int intervalTicks;
    protected final boolean canBeDisturbed;
    protected final boolean showParticles;
    protected final boolean runsAtEnd;
    protected final ParticleEffect particleType;
    protected final ParticleAnimation animation;

    public enum ParticleAnimation {
        SPIRAL,
        CYLINDER
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, boolean canBeDisturbed, boolean showParticles, ParticleEffect particleType, ParticleAnimation animation, boolean runsAtEnd) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.canBeDisturbed = canBeDisturbed;
        this.showParticles = showParticles;
        this.particleType = particleType;
        this.animation = animation;
        this.runsAtEnd = runsAtEnd;
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, boolean canBeDisturbed, boolean runsAtEnd) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.canBeDisturbed = canBeDisturbed;
        this.showParticles = false;
        this.particleType = ParticleTypes.ENCHANTED_HIT;
        this.animation = ParticleAnimation.CYLINDER;
        this.runsAtEnd = runsAtEnd;
    }

    @Override
    public abstract int getColor(GrabBag args);

    @Override
    protected final void use(SpellContext ctx, GrabBag args) {
        var startHealth = ctx.user().getHealth();
        boolean canBeDisturbed = args.getBoolean("canBeDisturbed", this.canBeDisturbed);
        int castTime = args.getInt("castTime", this.castTime);
        int intervalTicks = args.getInt("intervalTicks", this.intervalTicks);

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
                        if (animation == ParticleAnimation.CYLINDER) {
                            spawnChannelParticlesCylinder(ctx.user(), tick, castTime, args);
                        } else {
                            spawnChannelParticlesSpiral(ctx.user(), tick, castTime, args);
                        }
                    }
                    if (runsAtEnd && tick % castTime == 0 && tick != 0) {
                        runEnd(ctx, args, tick);
                    }
                    return false;
                }, castTime + 1)
        );
    }

    protected abstract void useChanneled(final SpellContext ctx, final GrabBag args, final int tick);

    protected void runEnd(final SpellContext ctx, final GrabBag args, final int tick) {

    }

    protected final void spawnChannelParticlesCylinder(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
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
    protected final void spawnChannelParticlesSpiral(LivingEntity entity, int tick, int totalDuration, GrabBag args) {
        World world = entity.getWorld();
        if (world.isClient) return;

        double height = entity.getHeight();
        double width = entity.getWidth();
        double radius = width * 0.75;

        // config from args
        int pointsPerRotation = args.getInt("pointsPerRotation", 20);
        int rotations = args.getInt("spiralRotations", 3);
        int visibleRings = args.getInt("maxVisibleRings", 10); // how many rings are visible at once

        // total spiral points
        int totalSteps = pointsPerRotation * rotations;

        // overall progress (0 → 1)
        double progress = (double) tick / totalDuration;
        double yBase = entity.getY();
        double yTop = yBase + height;

        // fade offset — only show the top section of the spiral
        int startRing = Math.max(0, (int)(progress * totalSteps) - visibleRings);
        int endRing = Math.min(totalSteps, (int)(progress * totalSteps));

        // color from getColor
        int color = getColor(args);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;

        // height and angle per step
        double heightStep = height / totalSteps;
        double angleStep = (2 * Math.PI * rotations) / totalSteps;

        // fadeout effect: top ring is brightest, bottom fades away
        for (int i = startRing; i < endRing; i++) {
            double fade = 1.0 - ((double)(endRing - i) / visibleRings); // 0 → 1
            fade = Math.max(0.0, Math.min(1.0, fade));

            double theta = i * angleStep + (tick * 0.25);
            double y = yBase + (i * heightStep);
            double x = entity.getX() + Math.cos(theta) * radius;
            double z = entity.getZ() + Math.sin(theta) * radius;

            float fr = r * (float)fade;
            float fg = g * (float)fade;
            float fb = b * (float)fade;

            if (world instanceof ServerWorld server) {
                if (particleType == null) {
                    server.spawnParticles(
                            EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, fr, fg, fb),
                            x, y, z, 1, 0, 0, 0, 0
                    );
                } else {
                    server.spawnParticles(particleType, x, y, z, 1, 0, 0, 0, 0);
                }
            } else {
                if (particleType == null) {
                    world.addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, fr, fg, fb), x, y, z, 0, 0, 0);
                } else {
                    world.addParticle(particleType, x, y, z, 0, 0, 0);
                }
            }
        }
    }


    @Override
    protected boolean canUse(SpellContext ctx, GrabBag args) {
        return ctx.user() instanceof LivingEntity && super.canUse(ctx, args);
    }
}

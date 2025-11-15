package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ChanneledSpell extends Spell {
    protected final int castTime;
    protected final int intervalTicks;
    protected final int waitForFirst;

    protected final boolean runsOnlyOnce;
    protected final boolean showParticles;
    protected final ParticleEffect particleType;
    protected final ParticleAnimation animation;

    protected final Disturb disturb;

    public enum ParticleAnimation {
        SPIRAL,
        CYLINDER
    }
    public enum DistributionLevels {
        NONE,
        DAMAGE,
        CROUCH,
        MOVE,
        MOVE_AND_DAMAGE,
        MOVE_AND_CROUCH,
        DAMAGE_AND_CROUCH,
        DAMAGE_CROUCH_AND_MOVE
    }

    public record Disturb(DistributionLevels distributionLevel, int disturbableTill, double moveableDistance) {}

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, Disturb disturb, boolean showParticles, ParticleEffect particleType, ParticleAnimation animation, int waitForFirst) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.showParticles = showParticles;
        this.particleType = particleType;
        this.animation = animation;
        this.waitForFirst = waitForFirst;
        this.runsOnlyOnce = false;
        this.disturb = disturb;
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, Disturb disturb, boolean showParticles, ParticleEffect particleType, ParticleAnimation animation, Integer waitForFirst) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.showParticles = showParticles;
        this.particleType = particleType;
        this.animation = animation;
        this.waitForFirst = waitForFirst;
        this.runsOnlyOnce = false;
        this.disturb = disturb;
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, Disturb disturb, boolean showParticles, ParticleEffect particleType, ParticleAnimation animation, int waitForFirst, boolean runsOnlyOnce) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.showParticles = showParticles;
        this.particleType = particleType;
        this.animation = animation;
        this.waitForFirst = waitForFirst;
        this.runsOnlyOnce = runsOnlyOnce;
        this.disturb = disturb;
    }


    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, Disturb disturb, boolean showParticles, ParticleEffect particleType, ParticleAnimation animation) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.showParticles = showParticles;
        this.particleType = particleType;
        this.animation = animation;
        this.waitForFirst = 0;
        this.runsOnlyOnce = false;
        this.disturb = disturb;
    }

    public ChanneledSpell(SpellTypes type, int castTime, int intervalTicks, Disturb disturb) {
        super(type);
        this.castTime = castTime;
        this.intervalTicks = intervalTicks;
        this.showParticles = false;
        this.particleType = ParticleTypes.ENCHANTED_HIT;
        this.animation = ParticleAnimation.CYLINDER;
        this.waitForFirst = 0;
        this.runsOnlyOnce = false;
        this.disturb = disturb;
    }


    @Override
    public abstract int getColor(GrabBag args);

    @Override
    protected final void use(SpellContext ctx, GrabBag args) {
        var startHealth = ctx.user().getHealth();
        var startPos = ctx.user().getPos();
        var user = ctx.user();
        int castTime = this.castTime;
        int intervalTicks = this.intervalTicks;
        double moveableDistance = args.getDouble("particleCount", disturb.moveableDistance);

        AtomicBoolean ranOnce = new AtomicBoolean(false);

        ((Ticker.TickerTarget) (ctx.user())).add(
                Ticker.forTicks(tick -> {
                     if (tick >= waitForFirst) {
                        switch (disturb.distributionLevel) {
                            case NONE -> {
                                if (tick % intervalTicks == 0) {
                                    if (runsOnlyOnce) {
                                        if (!ranOnce.get()) {
                                            ranOnce.set(true);
                                            useChanneled(ctx, args, tick);
                                        }
                                    } else {
                                        useChanneled(ctx, args, tick);
                                    }
                                }
                            }
                            case DAMAGE -> {
                                if (startHealth == ctx.user().getHealth()) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case CROUCH -> {
                                if (!user.isSneaking()) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case MOVE -> {
                                if (user.getPos().distanceTo(startPos) <= moveableDistance) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case MOVE_AND_DAMAGE -> {
                                if (user.getPos().distanceTo(startPos) <= moveableDistance && startHealth == ctx.user().getHealth()) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case MOVE_AND_CROUCH -> {
                                if (user.getPos().distanceTo(startPos) <= moveableDistance && !user.isSneaking()) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case DAMAGE_AND_CROUCH -> {
                                if (startHealth == ctx.user().getHealth() && !user.isSneaking()) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
                            }
                            case DAMAGE_CROUCH_AND_MOVE -> {
                                if (startHealth == ctx.user().getHealth() && !user.isSneaking() && user.getPos().distanceTo(startPos) <= moveableDistance) {
                                    if (tick % intervalTicks == 0) {
                                        if (runsOnlyOnce) {
                                            if (!ranOnce.get()) {
                                                ranOnce.set(true);
                                                useChanneled(ctx, args, tick);
                                            }
                                        } else {
                                            useChanneled(ctx, args, tick);
                                        }
                                    }
                                } else {
                                    if (tick <= disturb.disturbableTill) {
                                        return true;
                                    }
                                }
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
                    if (tick % castTime == 0 && tick != 0) {
                        runEnd(ctx, args, tick);
                    }
                    return false;
                }, castTime + 1)
        );
    }

    protected abstract void useChanneled(final SpellContext ctx, final GrabBag args, final int tick);

    protected void runEnd(final SpellContext ctx, final GrabBag args, final int tick) {}

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

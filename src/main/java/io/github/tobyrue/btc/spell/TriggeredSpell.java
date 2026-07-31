package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.enums.SpellTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TriggeredSpell extends ChanneledSpell {

    protected int activeTicks = 1200;

    public TriggeredSpell(SpellTypes type, int activeTicks, int intervalTicks, DisturbConfig disturbConfig,
                          boolean showParticles, net.minecraft.particle.ParticleEffect particleType, ParticleAnimation animation) {
        super(type, activeTicks, intervalTicks, disturbConfig, showParticles, particleType, animation);
        this.activeTicks = activeTicks;
    }

    public TriggeredSpell(SpellTypes id, int activeTicks, DisturbConfig disturbConfig) {
        this(id, activeTicks, 1, disturbConfig, true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.AURA);
    }

    public TriggeredSpell(SpellTypes id, DisturbConfig disturbConfig) {
        this(id, 1200, disturbConfig);
    }

    @Override
    protected void onChannelStart(SpellContext ctx, GrabBag args, Start start) {
        onStart(ctx);
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, Start start) {
        LivingEntity user = ctx.user();
        if (user == null) return;

        if (isDisturbed(ctx, tick, user)) {
            onChannelInterrupt(ctx, args, tick, InterruptReason.DISSPELLED);
            return;
        }

        if (shouldTrigger(ctx, tick, user)) {
            if (user.getWorld() instanceof ServerWorld serverWorld) {
                onTrigger(ctx, serverWorld, tick, user);
            }
            runEnd(ctx, args, tick);
            return;
        }

        tick(ctx, user);
    }

    @Override
    protected void runEnd(SpellContext ctx, GrabBag args, int tick) {
        LivingEntity user = ctx.user();
        if (user != null) {
            if (tick >= getCastTime(args)) {
                onTimeout(ctx, tick, user);
            } else {
                onEnd(ctx, tick, user);
            }
        }
        super.runEnd(ctx, args, tick);
    }

    @Override
    protected void onPurposefulCancel(SpellContext ctx, GrabBag args, int tick, InterruptReason reason) {
        if (ctx.user() != null) {
            onEnd(ctx.user() != null ? ctx : null, tick, ctx.user());
        }
        super.onPurposefulCancel(ctx, args, tick, reason);
    }

    @Override
    protected boolean canInterrupt(SpellContext ctx, InterruptReason reason, int currentTick) {
        if (reason.isPurposeful()) {
            return true;
        }
        return super.canInterrupt(ctx, reason, currentTick);
    }

    /**
     * Define the ready condition for triggering (e.g., entity entering range)
     */
    protected abstract boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current);

    protected abstract void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current);

    protected void onStart(SpellContext ctx) {}

    protected void onEnd(SpellContext ctx, int tick, LivingEntity current) {}

    protected void onTimeout(SpellContext ctx, int tick, LivingEntity current) {
        onEnd(ctx, tick, current);
    }

    protected void tick(SpellContext ctx, LivingEntity current) {}

    protected abstract boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current);
}
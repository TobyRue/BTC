package io.github.tobyrue.btc.spell;

import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TriggeredSpell extends Spell {

    protected int activeTicks = 1200;
    protected boolean showParticles = true;

    public TriggeredSpell(SpellTypes id) {
        super(id);
    }

    @Override
    protected final void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var startHealth = user.getHealth();
        var startPos = user.getPos();

        int duration = args.getInt("activeTicks", this.activeTicks);

        AtomicBoolean triggered = new AtomicBoolean(false);

        ((Ticker.TickerTarget) (user)).bTC$add(
                Ticker.forTicks(tick -> {

                    if (isDisturbed(ctx, tick, user)) {
                        return true;
                    }

                    if (!triggered.get() && shouldTrigger(ctx, tick, user)) {
                        triggered.set(true);

                        if (user.getWorld() instanceof ServerWorld serverWorld) {
                            onTrigger(ctx, serverWorld, tick);
                        }

                        onEnd(ctx, tick, user);
                        return true;
                    }

                    if (showParticles) {
                        spawnArmedParticles(ctx, tick, duration, user);
                    }

                    if (tick >= duration) {
                        onTimeout(ctx, tick, user);
                        return true;
                    }
                    tick(ctx, user);
                    return false;
                }, duration + 1)
        );

        onStart(ctx);
    }

    /**
     * Override this to define the Ready condition (e.g. searching for entities in range)
     */
    protected abstract boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current);


    protected abstract void onTrigger(SpellContext ctx, ServerWorld world, int tick);


    protected void onStart(SpellContext ctx) {}

    protected void onEnd(SpellContext ctx, int tick, LivingEntity current) {}

    protected void onTimeout(SpellContext ctx, int tick, LivingEntity current) {
        onEnd(ctx, tick, current);
    }

    protected void tick(SpellContext ctx, LivingEntity current) {}

    protected abstract boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current);

    protected void spawnArmedParticles(SpellContext ctx, int tick, int duration, LivingEntity current ) {
    }
}
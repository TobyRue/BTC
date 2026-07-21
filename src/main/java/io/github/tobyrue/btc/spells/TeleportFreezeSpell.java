package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class TeleportFreezeSpell extends TriggeredSpell {
    private LivingEntity lockedTarget;

    public TeleportFreezeSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    protected void onStart(SpellContext ctx) {
        Entity target = isTargetInRange(ctx.user(), ctx.target(), ctx.data().getArgs().getDouble("range", 32d));
        if (target instanceof LivingEntity living) {
            this.lockedTarget = living;
        }
    }

    @Override
    protected boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current) {
        return false;
    }

    @Override
    protected boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current) {
        if (lockedTarget == null || !lockedTarget.isAlive()) return false;

        double maxDist = ctx.data().getArgs().getDouble("max_distance", 12.0);
        return current.getPos().distanceTo(lockedTarget.getPos()) > maxDist;
    }

    @Override
    protected void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current) {
        if (lockedTarget == null) return;

        double offset = ctx.data().getArgs().getDouble("offset", 6.0);

        Vec3d destination = current.getPos().add(current.getRotationVec(1.0F).multiply(offset));
        lockedTarget.requestTeleport(destination.x, destination.y, destination.z);

        lockedTarget.setVelocity(Vec3d.ZERO);
        lockedTarget.velocityModified = true;

        int freezeDuration = ctx.data().getArgs().getInt("freeze_duration", 60);
        lockedTarget.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                freezeDuration,
                255,
                false,
                false,
                true
        ));

        world.spawnParticles(ParticleTypes.SNOWFLAKE, lockedTarget.getX(), lockedTarget.getEyeY(), lockedTarget.getZ(), 15, 0.5, 0.5, 0.5, 0.01);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, destination.x, destination.y, destination.z, 10, 0.2, 0.2, 0.2, 0.1);
    }

    @Override
    protected void tick(SpellContext ctx, LivingEntity current) {
        super.tick(ctx, current);
    }

    @Override
    protected void spawnArmedParticles(SpellContext ctx, int tick, int duration, LivingEntity current) {
        if (lockedTarget != null && tick % 5 == 0) {
        }
    }
    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("teleport_freeze"));
    }
    @Override
    public int getColor(GrabBag args) {
        return 0xFFA17CFF;
    }
}
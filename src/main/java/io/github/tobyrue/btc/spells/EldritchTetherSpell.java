package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;
import java.util.WeakHashMap;

public class EldritchTetherSpell extends ChanneledSpell {

    private final WeakHashMap<LivingEntity, Entity> activeTargets = new WeakHashMap<>();

    public EldritchTetherSpell() {
        super(SpellTypes.ENDER, 100, 1, new Disturb(DistributionLevels.NONE, -1, -1, 10), true, ParticleTypes.WITCH, ParticleAnimation.SPIRAL, 0, false);
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        LivingEntity user = ctx.user();
        if (user == null) return;

        if (tick == 0) {
            Entity found = isTargetInRange(user, ctx.target(), args.getDouble("range", 32d));
            if (found != null) {
                activeTargets.put(user, found);
            }
        }

        Entity target = activeTargets.get(user);

        if (target == null || !target.isAlive()) return;

        double maxAllowedDist = args.getDouble("tetherRadius", 5.5d);
        Vec3d anchor = user.getPos();

        if (ctx.world() instanceof ServerWorld serverWorld) {
            Vec3d targetPos = target.getPos();
            double currentDist = targetPos.distanceTo(anchor);

            spawnTetherParticles(serverWorld, anchor, targetPos);

            if (currentDist > maxAllowedDist) {
                target.requestTeleport(anchor.x, anchor.y, anchor.z);

                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.damage(user.getDamageSources().magic(), 2.0f);
                }

                serverWorld.playSound(null, anchor.x, anchor.y, anchor.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0f, 0.5f);
                serverWorld.spawnParticles(ParticleTypes.REVERSE_PORTAL, anchor.x, anchor.y + 1, anchor.z, 15, 0.2, 0.5, 0.2, 0.05);
            }

            if (tick >= args.getInt("castTime", this.castTime) - 1) {
                activeTargets.remove(user);
            }
        }
    }

    private void spawnTetherParticles(ServerWorld world, Vec3d start, Vec3d end) {
        Vec3d diff = end.subtract(start);
        double dist = diff.length();
        for (double i = 0; i < dist; i += 0.8) {
            Vec3d point = start.add(diff.multiply(i / dist));
            world.spawnParticles(ParticleTypes.SMOKE, point.x, point.y + 1, point.z, 1, 0, 0, 0, 0);
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && isTargetInRange(ctx.user(), ctx.target(), args.getDouble("range", 32d)) != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 300), BTC.identifierOf("eldritch_tether"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0x3e006e;
    }
}
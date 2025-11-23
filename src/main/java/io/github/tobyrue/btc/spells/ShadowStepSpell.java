package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ShadowStepSpell extends Spell {
    public ShadowStepSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        // Deep purple - fits teleport/invisibility theme
        return 0x6B2BA3;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double range = args.getDouble("range", 24d);
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double teleportDistance = args.getDouble("teleportDistance", 2.5D); // distance behind target
        int invisDuration = args.getInt("invisDuration", 140); // ticks (5s default)

        Entity target = getEntityLookedAt(ctx.user(), range, aimingForgiveness);
        if (target == null) return;

        // Calculate position behind target
        Vec3d backward = target.getRotationVec(1.0F).normalize().negate();
        Vec3d targetPos = target.getPos();
        Vec3d newPos = targetPos.add(backward.multiply(teleportDistance));

        // Teleport the user
        ctx.user().requestTeleport(newPos.x, newPos.y, newPos.z);

        // Apply invisibility
        ctx.user().addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY, invisDuration, 0, false, false, true
        ));
    }

    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox,
                e -> e.isAttackable() && e.canHit())) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness);
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);
            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }

    @Override
    protected boolean canUse(SpellContext ctx, GrabBag args) {
        assert ctx.user() != null;
        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24),
                args.getDouble("aimingForgiveness", 0.3D));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public SpellCooldown getCooldown(GrabBag args, @Nullable LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 300), BTC.identifierOf("shadow_step"));
    }
}

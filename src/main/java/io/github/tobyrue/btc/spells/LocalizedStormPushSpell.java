package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class LocalizedStormPushSpell extends Spell {
    public LocalizedStormPushSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double shootStrength = args.getDouble("shootStrength", 7d); // Overall velocity multiplier
        double verticalMultiplier = args.getDouble("verticalMultiplier", 2.2d); // How much extra vertical force to apply
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24);
        // Shoot mob away from the player
        var entity = getEntityLookedAt(ctx.user(), range, aimingForgiveness);
        double dx = entity.getX() - ctx.user().getX();
        double dy = entity.getY() - ctx.user().getY();
        double dz = entity.getZ() - ctx.user().getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        // Apply velocity away from the player
        if (distance != 0) {
            entity.setVelocity(dx / distance * shootStrength, (dy / distance * shootStrength), dz / distance * shootStrength);
            entity.setVelocity(entity.getVelocity().add(0, verticalMultiplier, 0));
        }


        entity.damage(ctx.world().getDamageSources().flyIntoWall(), 5);
    }

    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness); // slightly expanded hitbox
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
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        assert ctx.user() != null;
        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24), args.getDouble("aimingForgiveness", 0.3D));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("localized_storm_push"));
    }
}

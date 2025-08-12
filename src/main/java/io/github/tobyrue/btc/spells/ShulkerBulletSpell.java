package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ShulkerBulletSpell extends Spell {

    public ShulkerBulletSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24);
        double radius = args.getDouble("radius", 24);

        if (ctx.user() != null) {
            Vec3d look = ctx.user().getRotationVec(1.0F); // player’s look direction

            Direction.Axis axis;
            if (Math.abs(look.x) > Math.abs(look.y) && Math.abs(look.x) > Math.abs(look.z)) {
                axis = Direction.Axis.X;
            } else if (Math.abs(look.y) > Math.abs(look.z)) {
                axis = Direction.Axis.Y;
            } else {
                axis = Direction.Axis.Z;
            }

            ctx.world().spawnEntity(
                    new ShulkerBulletEntity(
                            ctx.world(),
                            ctx.user(),
                            getEntityLookedAt(ctx.user(), range, aimingForgiveness),
                            axis
                    )
            );
        } else {
// Dispenser or other non-living source
            List<LivingEntity> entities = ctx.world().getEntitiesByClass(
                    LivingEntity.class,
                    new Box(BlockPos.ofFloored(ctx.pos())).expand(radius),
                    e -> e.isAlive()
            );

            if (!entities.isEmpty()) {
                LivingEntity nearest = entities.stream()
                        .min(Comparator.comparingDouble(a -> a.squaredDistanceTo(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ())))
                        .orElse(null);

                // Aim axis based on relative position to source
                Vec3d dir = nearest.getPos().subtract(ctx.pos());
                Direction.Axis axis;
                if (Math.abs(dir.x) > Math.abs(dir.y) && Math.abs(dir.x) > Math.abs(dir.z)) {
                    axis = Direction.Axis.X;
                } else if (Math.abs(dir.y) > Math.abs(dir.z)) {
                    axis = Direction.Axis.Y;
                } else {
                    axis = Direction.Axis.Z;
                }
                ShulkerBulletEntity bullet = new ShulkerBulletEntity(
                        ctx.world(),
                        nearest,
                        nearest,
                        axis
                );
                bullet.refreshPositionAndAngles(ctx.pos().x + ctx.direction().x * 1.5, ctx.pos().y + ctx.direction().y * 1.5, ctx.pos().z + ctx.direction().z * 1.5, 0, 0);

                ctx.world().spawnEntity(
                    bullet
                );
            }
        }
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
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("shulker_bullet"));
    }
}

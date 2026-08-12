package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ShulkerBulletSpell extends Spell implements UpgradableSpell {

    public ShulkerBulletSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFFFFCCF8;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double range = args.getDouble("range", 24d);
        double radius = args.getDouble("radius", 24d);

        if (ctx.user() != null) {
            Vec3d look = ctx.user().getRotationVec(1.0F);

            Direction.Axis axis;
            if (Math.abs(look.x) > Math.abs(look.y) && Math.abs(look.x) > Math.abs(look.z)) {
                axis = Direction.Axis.X;
            } else if (Math.abs(look.y) > Math.abs(look.z)) {
                axis = Direction.Axis.Y;
            } else {
                axis = Direction.Axis.Z;
            }

            ShulkerBulletEntity bullet = new ShulkerBulletEntity(
                    ctx.world(),
                    ctx.user(),
                    isTargetInRange(ctx.user(), ctx.target(), range),
                    axis
            );

            bullet.refreshPositionAndAngles(
                    ctx.user().getX(),
                    ctx.user().getY() + 1.0,
                    ctx.user().getZ(),
                    ctx.user().getYaw(),
                    ctx.user().getPitch()
            );

            ctx.world().spawnEntity(bullet);
        } else {

            List<LivingEntity> entities = ctx.world().getEntitiesByClass(
                    LivingEntity.class,
                    new Box(BlockPos.ofFloored(ctx.pos())).expand(radius),
                    e -> e.isAlive()
            );

            if (!entities.isEmpty()) {
                LivingEntity nearest = entities.stream()
                        .min(Comparator.comparingDouble(a -> a.squaredDistanceTo(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ())))
                        .orElse(null);

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

        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit())) {
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
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("shulker_bullet"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 200, 80, 400, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("blaze_upgrade"));
        return upgrades;
    }
}
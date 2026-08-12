package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GeyserStepSpell extends Spell implements UpgradableSpell {
    public GeyserStepSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF4B60D8;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        var aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        var range = args.getDouble("range", 24.0d);
        var canTarget = args.getBoolean("canTarget", true);

        double targetLaunchVelocity = args.getDouble("targetLaunchVelocity", 1.7d);
        double selfLaunchVelocity = args.getDouble("selfLaunchVelocity", 2.2d);

        var target = isTargetInRange(ctx.user(), ctx.target(), range);
        var launchVelocity = (target != null && canTarget) ? targetLaunchVelocity : selfLaunchVelocity;

        var launchedEntity = (target != null && canTarget) ? target : user;

        // Apply upward velocity
        var velocity = launchedEntity.getVelocity();
        launchedEntity.setVelocity(velocity.x, launchVelocity, velocity.z);
        launchedEntity.velocityModified = true;
        Vec3d storedPos = launchedEntity.getPos();
        int count = 36;

        BlockPos pillarPos = findGroundBelowEntity(world, launchedEntity, 20);

        if (pillarPos != null && world.isClient) {
            for (double y = pillarPos.getY(); y < storedPos.getY(); y++) {
                for (int i = 0; i < count; i++) {
                    double angle = (2 * Math.PI / count) * i;
                    double x = storedPos.getX() + Math.sin(angle) * 0.2;
                    double z = storedPos.getZ() + Math.cos(angle) * 0.2;

                    world.addParticle(ParticleTypes.SPLASH,
                            x,
                            y,
                            z,
                            (world.random.nextDouble() - 0.5) * 0.2,
                            0.1,
                            (world.random.nextDouble() - 0.5) * 0.2
                    );
                }
            }
        }

        // Schedule continuous splash particles while rising
        ((Ticker.TickerTarget) ctx.user()).bTC$add(Ticker.of((tickCount) -> {
            if (world.isClient) {
                for (int i = 0; i < count; i++) {

                    double angle = (2 * Math.PI / count) * i;
                    double x = storedPos.getX() + Math.sin(angle) * 0.2;
                    double z = storedPos.getZ() + Math.cos(angle) * 0.2;

                    world.addParticle(ParticleTypes.SPLASH,
                            x,
                            launchedEntity.getY(),
                            z,
                            (world.random.nextDouble() - 0.5) * 0.2,
                            0.1,
                            (world.random.nextDouble() - 0.5) * 0.2
                    );
                }
            }
            return launchedEntity.getVelocity().y <= 0;
        }));
    }

    @org.jetbrains.annotations.Nullable
    public static BlockPos findGroundBelowEntity(World world, Entity entity, int maxSearchDistance) {
        BlockPos entityPos = entity.getBlockPos();
        int startY = entityPos.getY();
        int bottomY = Math.max(world.getBottomY(), startY - maxSearchDistance);

        for (int y = startY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(entityPos.getX(), y, entityPos.getZ());

            // Check if block at pos is solid and block above pos is air or non-solid (so pillar can reach)
            if (!world.getBlockState(pos).isAir() && world.getBlockState(pos.up()).isAir()) {
                return pos.up();  // The position above the solid block, where pillar would be visible
            }
        }

        return null; // No suitable ground found
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
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        var aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        var range = args.getDouble("range", 24.0d);
        var canTarget = args.getBoolean("canTarget", true);
        var onSelf = args.getBoolean("onSelf", true);

        if (canTarget) {
            Entity target = getEntityLookedAt(ctx.user(), range, aimingForgiveness);
            if (!onSelf) {
                return target != null && super.canUse(ctx, args);
            }
        }
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("geyser_step"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("prismarine_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_launch_velocity")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_forgiveness")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 100, 40, 200, -10, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "selfLaunchVelocity", 2.2, 1.0, 4.0, 0.3, BTC.identifierOf("prismarine_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "aimingForgiveness", 0.3, 0.1, 1.0, 0.1, BTC.identifierOf("quartz_upgrade"));
        return upgrades;
    }
}
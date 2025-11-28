package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;

public class GeyserStepSpell extends Spell {
    public GeyserStepSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        var aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        var range = args.getDouble("range", 24.0d);
        var canTarget = args.getBoolean("canTarget", true);

        var target = getEntityLookedAt(user, range, aimingForgiveness);
        var launchVelocity = (target != null && canTarget) ? 1.7 : 2.2; // less for enemies, more for player

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
        ((Ticker.TickerTarget) ctx.user()).add(Ticker.of((tickCount) -> {
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
//    @Override
//    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
//        assert ctx.user() != null;
//        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24), args.getDouble("aimingForgiveness", 0.3D));
//        return target != null && super.canUse(ctx, args);
//    }
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
}

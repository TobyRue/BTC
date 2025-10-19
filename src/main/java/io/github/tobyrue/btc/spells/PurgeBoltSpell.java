package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Optional;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.x;

public class PurgeBoltSpell extends Spell {

    public PurgeBoltSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        World world = ctx.world();
        LivingEntity user = ctx.user();
        if (user == null) return;

        double speed = args.getDouble("speed", 0.35); // ~7 blocks/sec
        int lifetime = args.getInt("lifetime", 60);   // 3 seconds
        double range = args.getDouble("range", 10.0);
        double forgiveness = args.getDouble("forgiveness", 0.3);

        Vec3d start = user.getCameraPosVec(1.0F);
        Vec3d dir = user.getRotationVec(1.0F).normalize();

        ((Ticker.TickerTarget) user).add(
                Ticker.forTicks(tick -> {
                    // Each tick, move the invisible bolt forward
                    Vec3d pos = start.add(dir.multiply(speed * tick));

                    // Client visual trail
                    if (!world.isClient) {
                        ((ServerWorld) world).spawnParticles(
                                ParticleTypes.LARGE_SMOKE, // particle type
                                pos.x, pos.y + 0.1, pos.z, // position
                                1,                          // count
                                0, 0, 0,                    // offset (spread)
                                0                           // speed
                        );
                    } else {
                        world.addParticle(ParticleTypes.LARGE_SMOKE,
                                pos.x, pos.y + 0.1, pos.z, 0, 0, 0);
                    }

                    if (!world.isClient) {
                        // Check if the bolt hits any entity along the look vector
                        var entity = getEntityLookedAt(user, range, forgiveness);
                        if (entity instanceof LivingEntity target) {
                            // Remove all positive effects
                            for (StatusEffectInstance effect : target.getStatusEffects().toArray(new StatusEffectInstance[0])) {
                                var type = effect.getEffectType();
                                if (type.value().getCategory() == StatusEffectCategory.BENEFICIAL) {
                                    target.removeStatusEffect(type);
                                }
                            }
                            return true; // Stop ticking after hit
                        }
                    }

                    return tick >= lifetime; // End after lifetime
                }, lifetime)
        );
    }
    public static @org.jetbrains.annotations.Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        int candidates = 0;
        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit())) {
            candidates++;
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness);
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            // Debug: check intersection
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
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("purge_bolt"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFAAAAAA; // soft gray
    }
}

package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DragonsBreathSpell extends Spell {

    public DragonsBreathSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
        //TODO
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        LivingEntity user = ctx.user();
        if (user == null) return;

        World world = ctx.world();

        int duration = args.getInt("duration", 200);      // ticks spell lasts
        int ticksPerShot = args.getInt("rate", 4);       // how often to “pulse” flame
        double range = args.getDouble("range", 8.0d);     // cone length
        double angle = args.getDouble("angle", 20.0d);    // cone half-angle (degrees)
        double damage = args.getDouble("damage", 4.0d);   // damage per tick

        ((Ticker.TickerTarget) user).add(
                Ticker.forTicks(tick -> {
                    Vec3d look = user.getRotationVec(1).normalize();

                    // spawn flame particles in a cone
                    for (int i = 0; i < 12; i++) {
                        Vec3d offset = look.add(
                                (world.getRandom().nextDouble() - 0.5) * 0.3,
                                (world.getRandom().nextDouble() - 0.5) * 0.3,
                                (world.getRandom().nextDouble() - 0.5) * 0.3
                        ).normalize().multiply(world.getRandom().nextDouble() * range);

                        Vec3d particlePos = user.getPos().add(0, user.getStandingEyeHeight(), 0).add(offset);
                        if (!world.isClient) {
                            // send particle to all players nearby
                            ((ServerWorld) world).spawnParticles(
                                    ParticleTypes.DRAGON_BREATH,
                                    particlePos.x, particlePos.y, particlePos.z,
                                    1,    // count
                                    0, 0, 0, // delta for random offset
                                    0     // speed
                            );
                        } else {
                            // already client-side, can just spawn directly
                            world.addParticle(
                                    ParticleTypes.DRAGON_BREATH,
                                    particlePos.x, particlePos.y, particlePos.z,
                                    0, 0, 0
                            );
                        }
                    }

                    // every few ticks, actually deal damage + ignite
                    if (tick % ticksPerShot == 0) {
                        Vec3d eyePos = user.getPos().add(0, user.getStandingEyeHeight(), 0);
                        Box area = new Box(eyePos, eyePos.add(look.multiply(range))).expand(1.5);

                        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                                e -> e != user && e.isAlive());

                        for (LivingEntity target : targets) {
                            Vec3d toTarget = target.getPos().add(0, target.getStandingEyeHeight() / 2, 0).subtract(eyePos).normalize();
                            double dot = look.dotProduct(toTarget);
                            double cos = Math.cos(Math.toRadians(angle));

                            if (dot > cos) { // inside cone
                                target.damage(world.getDamageSources().magic(), (float) damage);
                            }
                        }

                        // small chance to ignite ground blocks under cone
                        if (world.getRandom().nextFloat() < 0.3F) {
                            Vec3d firePos = eyePos.add(look.multiply(world.getRandom().nextDouble() * range));
                            var blockPos = world.getBlockState(BlockPos.ofFloored(firePos));
                            if (blockPos.isAir() && world.getBlockState(BlockPos.ofFloored(firePos).down()).isSolidBlock(world, BlockPos.ofFloored(firePos).down())) {
                                world.setBlockState(BlockPos.ofFloored(firePos), net.minecraft.block.Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }, duration)
        );
    }
    @Override
    protected boolean canUse(SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("flame_burst"));
    }
}

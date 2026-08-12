package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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

public class FlameBurstSpell extends ChanneledSpell implements UpgradableSpell {

    public FlameBurstSpell() {
        super(
                SpellTypes.FIRE, 200, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill((spell, args) -> args.getInt("castTime", 200))
                        .moveableDistance(0)
                        .hold(20)
                        .build()
        );
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFFFF4500; // fiery orange-red
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        LivingEntity user = ctx.user();

        World world = ctx.world();

        int ticksPerShot = args.getInt("rate", 1);       // how often to “pulse” flame
        double range = args.getDouble("range", 10.0d);     // cone length
        double angle = args.getDouble("angle", 25.0d);    // cone half-angle (degrees)
        double damage = args.getDouble("damage", 3.0d);   // damage per tick

        Vec3d look = user.getRotationVec(1).normalize();

        for (int i = 0; i < 12; i++) {
            Vec3d offset = look.add(
                    (world.getRandom().nextDouble() - 0.5) * 0.3,
                    (world.getRandom().nextDouble() - 0.5) * 0.3,
                    (world.getRandom().nextDouble() - 0.5) * 0.3
            ).normalize().multiply(world.getRandom().nextDouble() * range);

            Vec3d particlePos = user.getPos().add(0, user.getStandingEyeHeight(), 0).add(offset);
            if (!world.isClient) {
                ((ServerWorld) world).spawnParticles(
                        ParticleTypes.FLAME,
                        particlePos.x, particlePos.y, particlePos.z,
                        1,
                        0, 0, 0,
                        0
                );
            } else {
                world.addParticle(
                        ParticleTypes.FLAME,
                        particlePos.x, particlePos.y, particlePos.z,
                        0, 0, 0
                );
            }
        }

        if (tick % ticksPerShot == 0) {
            Vec3d eyePos = user.getPos().add(0, user.getStandingEyeHeight(), 0);
            Box area = new Box(eyePos, eyePos.add(look.multiply(range))).expand(1.5);

            List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, area,
                    e -> e != user && e.isAlive());

            for (LivingEntity target : targets) {
                Vec3d toTarget = target.getPos().add(0, target.getStandingEyeHeight() / 2, 0).subtract(eyePos).normalize();
                double dot = look.dotProduct(toTarget);
                double cos = Math.cos(Math.toRadians(angle));

                if (dot > cos) {
                    target.setOnFireFor(3);
                    target.damage(world.getDamageSources().inFire(), (float) damage);
                }
            }

            if (world.getRandom().nextFloat() < 0.3F && world instanceof ServerWorld) {
                Vec3d firePos = eyePos.add(look.multiply(world.getRandom().nextDouble() * range));
                var blockPos = world.getBlockState(BlockPos.ofFloored(firePos));
                if (blockPos.isAir() && world.getBlockState(BlockPos.ofFloored(firePos).down()).isSolidBlock(world, BlockPos.ofFloored(firePos).down())) {
                    world.setBlockState(BlockPos.ofFloored(firePos), net.minecraft.block.Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("flame_burst"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_damage")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_angle")));
        upgrades.add(new Pair<>(BTC.identifierOf("ghast_tear_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_angle")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_cast_time")));
        upgrades.add(new Pair<>(BTC.identifierOf("copper_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_moveable_distance")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 800, 400, 1200, -50, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "damage", 3.0, 1.0, 8.0, 0.5, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 10.0, 5.0, 20.0, 1.5, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "angle", 25.0, 10.0, 45.0, 5.0, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "angle", 25.0, 10.0, 45.0, -5.0, BTC.identifierOf("ghast_tear_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cast_time", 200, 100, 400, 40, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "moveableDistance", 0, 0, 10, 2, BTC.identifierOf("copper_upgrade"));
        return upgrades;
    }
}
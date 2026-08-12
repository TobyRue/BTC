package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaterWaveSpell extends ChanneledSpell implements UpgradableSpell {
    public WaterWaveSpell() {
        super(
                SpellTypes.WATER, 40, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill((spell, args) -> args.getInt("castTime", 40))
                        .moveableDistance(-1)
                        .hold(20)
                        .build(),
                true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL, 0, false
        );
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF6168E2;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, Start start) {
        double maxRadius = args.getDouble("maxRadius", 8d);
        int amplifier = args.getInt("amplifier", 1);
        int maxDuration = args.getInt("maxDuration", 600);
        int duration = args.getInt("castTime", this.castTime);

        var storedPos = start.pos();
        if (ctx.world() instanceof ServerWorld serverWorld) {
            double progress = tick / (double) (duration);
            double radius = maxRadius * progress;

            int count = (int) (maxRadius / 64d * 4096d);
            for (int i = 0; i < count; i++) {

                double angle = (2 * Math.PI / count) * i;

                double x = storedPos.getX() + Math.sin(angle) * radius;
                double z = storedPos.getZ() + Math.cos(angle) * radius;

                double yOffset = 0.2;
                double y = storedPos.getY() + yOffset;
                y += Math.sin(radius) * 0.7;

                double xSpeed = Math.sin(angle) * 0.2;
                double zSpeed = Math.cos(angle) * 0.2;

                serverWorld.spawnParticles(BTCClient.WATER_DROP, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
            }

            for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                double dist = target.getPos().distanceTo(storedPos);

                double stepSize = maxRadius / duration;
                if (dist <= radius && dist > (radius - stepSize)) {
                    target.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DROWNING, maxDuration, amplifier));
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("water_wave"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_potency")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 200, 900, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "maxRadius", 8.0, 4.0, 18.0, 1.5, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "maxDuration", 600, 200, 1200, 100, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amplifier", 1, 0, 5, 1, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}
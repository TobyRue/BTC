package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FrostReflexSpell extends TriggeredSpell implements UpgradableSpell {

    private float healthAtStart;

    public FrostReflexSpell() {
        super(SpellTypes.GENERIC, 1200, DisturbConfig.builder().hold(40).level(DistributionLevels.CLICK).build());
    }

    @Override
    protected void onStart(SpellContext ctx) {
        if (ctx.user() == null) return;
        this.healthAtStart = ctx.user().getHealth();

        ctx.user().getWorld().playSound(
                null,
                ctx.user().getBlockPos(),
                SoundEvents.BLOCK_POWDER_SNOW_PLACE,
                SoundCategory.PLAYERS,
                1.0f,
                1.5f
        );
    }

    @Override
    protected boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current) {
        return current.getHealth() < this.healthAtStart;
    }

    @Override
    protected void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current) {
        if (ctx.user() == null) return;
        LivingEntity attacker = ctx.user().getAttacker();

        int duration = ctx.data().getArgs().getInt("debuffDuration", 300);
        int slownessAmp = ctx.data().getArgs().getInt("slownessAmp", 4);
        int weaknessAmp = ctx.data().getArgs().getInt("weaknessAmp", 1);
        int fatigueAmp = ctx.data().getArgs().getInt("fatigueAmp", 3);

        if (attacker != null) {
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, duration, slownessAmp));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, duration, weaknessAmp));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, fatigueAmp));

            world.spawnParticles(
                    ParticleTypes.SNOWFLAKE,
                    attacker.getX(), attacker.getY() + 1, attacker.getZ(),
                    25, 0.5, 0.5, 0.5, 0.05
            );

            world.playSound(
                    null,
                    attacker.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE,
                    SoundCategory.PLAYERS,
                    1.0f,
                    1.0f
            );
        }
    }

    @Override
    protected boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current) {
        return false;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, Start start) {
        super.useChanneled(ctx, args, tick, start);

        LivingEntity current = ctx.user();
        if (current != null && current.getWorld() instanceof ServerWorld serverWorld) {
            double angle = tick * 0.2;
            double x = current.getX() + Math.cos(angle) * 0.8;
            double z = current.getZ() + Math.sin(angle) * 0.8;

            serverWorld.spawnParticles(
                    ParticleTypes.INSTANT_EFFECT,
                    x, current.getY() + 0.5, z,
                    1, 0.0, 0.0, 0.0, 0.0
            );
        }
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF7A97DB;
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 3000), BTC.identifierOf("frost_reflex"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_slowness_potency")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 3000, 1500, 4500, -200, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "debuffDuration", 300, 100, 600, 50, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "slownessAmp", 4, 1, 6, 1, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}
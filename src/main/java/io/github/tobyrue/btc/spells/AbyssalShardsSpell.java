package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class AbyssalShardsSpell extends ChanneledSpell implements UpgradableSpell {

    private final WeakHashMap<LivingEntity, Entity> activeTargets = new WeakHashMap<>();

    public AbyssalShardsSpell() {
        super(
                SpellTypes.ENDER, 60, 10,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill(ChanneledSpell::getCastTime)
                        .moveableDistance(-1)
                        .hold(15)
                        .build(),
                true, ParticleTypes.SOUL, ParticleAnimation.SPIRAL, 0, false
        );
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        LivingEntity user = ctx.user();
        if (user == null || !(ctx.world() instanceof ServerWorld serverWorld)) return;

        if (tick == 0) {
            Entity found = isTargetInRange(user, ctx.target(), args.getDouble("range", 32d));
            if (found != null) activeTargets.put(user, found);
        }

        Entity target = activeTargets.get(user);
        if (target == null || !target.isAlive()) return;

        Vec3d targetPos = target.getPos();
        float damage = (float) args.getDouble("damage", 2.0d);
        int blindnessDuration = args.getInt("blindness_duration", 100);

        serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, targetPos.x, targetPos.y, targetPos.z, 10, 0.2, 0.1, 0.2, 0.05);
        serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, targetPos.x, targetPos.y, targetPos.z, 5, 0.3, 0.5, 0.3, 0.1);

        serverWorld.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 1.0f, 0.5f);

        for (LivingEntity e : serverWorld.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(1.5), entity -> entity != user)) {
            if (e.getMaxHealth() / 3 < e.getHealth()) {
                e.damage(user.getDamageSources().magic(), damage);
            } else {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, blindnessDuration, 4));
            }
            e.addVelocity(0, 0.3, 0);
        }

        if (tick >= args.getInt("castTime", this.castTime) - 5) {
            activeTargets.remove(user);
        }
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 240), BTC.identifierOf("abyssal_shards"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF1a1a1a;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_damage")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_cast_time")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_blindness")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 240, 100, 400, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 32.0, 16.0, 64.0, 4.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "damage", 2.0, 1.0, 8.0, 1.0, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "castTime", 60, 40, 180, 20, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "blindness_duration", 100, 60, 300, 40, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}
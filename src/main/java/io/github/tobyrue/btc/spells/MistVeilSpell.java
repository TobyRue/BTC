package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MistVeilSpell extends Spell implements UpgradableSpell {

    public MistVeilSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF80C8FF;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var caster = ctx.user();
        var world = ctx.world();

        double radius = args.getDouble("radius", 5.0d);
        int durationTicks = args.getInt("durationTicks", 160); // 8 seconds

        Vec3d center = caster.getPos();

        ((Ticker.TickerTarget) caster).bTC$add(Ticker.forTicks((tick) -> {
            if (world instanceof ServerWorld serverWorld) {
                for (int i = 0; i < 64; i++) {
                    double offsetX = (serverWorld.random.nextDouble() - 0.5) * radius * 2;
                    double offsetY = serverWorld.random.nextDouble() * 2;
                    double offsetZ = (serverWorld.random.nextDouble() - 0.5) * radius * 2;
                    serverWorld.spawnParticles(ParticleTypes.CLOUD,
                            center.x + offsetX,
                            center.y + offsetY,
                            center.z + offsetZ,
                            1, 0, 0, 0, 0);
                }

                Box effectBox = new Box(center.x - radius, center.y - 2, center.z - radius,
                        center.x + radius, center.y + 3, center.z + radius);

                for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, effectBox, LivingEntity::isAlive)) {

                    if (isAlly(caster, entity)) {
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.REGENERATION, 40, 0, true, false, true));
                    } else {
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 40, 1, true, false, true));
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE, 40, 0, true, false, true));
                    }
                }
            }
        }, durationTicks));
    }

    private boolean isAlly(LivingEntity caster, LivingEntity other) {
        if (caster == other) return true;

        if (other instanceof Tameable tameable) {
            if (caster.getUuid().equals(tameable.getOwnerUuid())) return true;
        }
        var casterTeam = caster.getScoreboardTeam();
        if (casterTeam == null) return false;

        if (!casterTeam.isFriendlyFireAllowed()) return false;
        if (casterTeam.getPlayerList().contains(other.getUuid())) return true;

        return false;
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("mist_veil"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 300, 900, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 5.0, 2.5, 12.0, 1.0, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "durationTicks", 160, 60, 320, 20, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}
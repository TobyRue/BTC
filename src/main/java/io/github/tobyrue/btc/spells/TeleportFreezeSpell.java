package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeleportFreezeSpell extends TriggeredSpell implements UpgradableSpell {

    private LivingEntity lockedTarget;

    public TeleportFreezeSpell() {
        super(SpellTypes.GENERIC, 1200, DisturbConfig.builder().hold(40).disturbableTill(ChanneledSpell::getCastTime).level(DistributionLevels.CLICK).build());
    }

    @Override
    protected void onStart(SpellContext ctx) {
        if (ctx.user() == null) return;
        Entity target = isTargetInRange(ctx.user(), ctx.target(), ctx.data().getArgs().getDouble("range", 32.0D));
        if (target instanceof LivingEntity living) {
            this.lockedTarget = living;
        }
    }

    @Override
    protected boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current) {
        return false;
    }

    @Override
    protected boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current) {
        if (lockedTarget == null || !lockedTarget.isAlive()) return false;

        double maxDist = ctx.data().getArgs().getDouble("max_distance", 12.0D);
        return current.getPos().distanceTo(lockedTarget.getPos()) > maxDist;
    }

    @Override
    protected void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current) {
        if (lockedTarget == null) return;

        double offset = ctx.data().getArgs().getDouble("offset", 6.0D);

        Vec3d destination = current.getPos().add(current.getRotationVec(1.0F).multiply(offset));
        lockedTarget.requestTeleport(destination.x, destination.y, destination.z);

        lockedTarget.setVelocity(Vec3d.ZERO);
        lockedTarget.velocityModified = true;

        int freezeDuration = ctx.data().getArgs().getInt("freeze_duration", 60);
        lockedTarget.addStatusEffect(new StatusEffectInstance(
                StatusEffects.SLOWNESS,
                freezeDuration,
                255,
                false,
                false,
                true
        ));

        world.spawnParticles(ParticleTypes.SNOWFLAKE, lockedTarget.getX(), lockedTarget.getEyeY(), lockedTarget.getZ(), 15, 0.5, 0.5, 0.5, 0.01);
        world.spawnParticles(ParticleTypes.REVERSE_PORTAL, destination.x, destination.y, destination.z, 10, 0.2, 0.2, 0.2, 0.1);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("teleport_freeze"));
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFFA17CFF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_freeze_duration")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 200, 900, -40, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 32.0, 16.0, 64.0, 4.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "freeze_duration", 60, 20, 160, 15, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}
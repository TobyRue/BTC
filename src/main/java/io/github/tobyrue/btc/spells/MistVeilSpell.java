package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class MistVeilSpell extends Spell {

    public MistVeilSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        // Light blue mist color
        return 0x80C8FF;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var caster = ctx.user();
        var world = ctx.world();

        double radius = args.getDouble("textRadius", 5.0);
        int durationTicks = args.getInt("durationTicks", 160); // 8 seconds

        Vec3d center = caster.getPos();

        // Schedule ticking effect for duration
        ((Ticker.TickerTarget) caster).add(Ticker.forTicks((tick) -> {
            if (world instanceof ServerWorld serverWorld) {
                // Spawn mist particles around caster
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

                // Find entities in textRadius
                Box effectBox = new Box(center.x - radius, center.y - 2, center.z - radius,
                        center.x + radius, center.y + 3, center.z + radius);

                for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, effectBox, LivingEntity::isAlive)) {

                    if (isAlly(caster, entity)) {
                        // Apply regeneration buff to allies
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.REGENERATION, 40, 0, true, false, true));
                    } else {
                        // Apply slowness and mining fatigue to enemies
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.SLOWNESS, 40, 1, true, false, true));
                        entity.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE, 40, 0, true, false, true));
                    }
                }
            }
            // Stop after duration
        }, durationTicks));
    }

    private boolean isAlly(LivingEntity caster, LivingEntity other) {
        if (caster == other) return true; // Self is always ally

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
}

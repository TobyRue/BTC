package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.WeakHashMap;

public class AbyssalShardsSpell extends ChanneledSpell {

    private final WeakHashMap<LivingEntity, Entity> activeTargets = new WeakHashMap<>();

    public AbyssalShardsSpell() {
        super(SpellTypes.ENDER, 60, 10, new Disturb(DistributionLevels.NONE, -1, -1, 15), true, ParticleTypes.SOUL, ParticleAnimation.SPIRAL, 0, false);
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

        serverWorld.spawnParticles(ParticleTypes.LARGE_SMOKE, targetPos.x, targetPos.y, targetPos.z, 10, 0.2, 0.1, 0.2, 0.05);
        serverWorld.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, targetPos.x, targetPos.y, targetPos.z, 5, 0.3, 0.5, 0.3, 0.1);

        serverWorld.playSound(null, targetPos.x, targetPos.y, targetPos.z, SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.HOSTILE, 1.0f, 0.5f);

        for (LivingEntity e : serverWorld.getEntitiesByClass(LivingEntity.class, target.getBoundingBox().expand(1.5), entity -> entity != user)) {
            if (e.getMaxHealth() / 3 < e.getHealth()) {
                e.damage(user.getDamageSources().magic(), damage);
            } else {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 4));
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
}
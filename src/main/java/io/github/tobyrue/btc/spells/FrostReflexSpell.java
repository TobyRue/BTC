package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class FrostReflexSpell extends TriggeredSpell {

    private float healthAtStart;

    public FrostReflexSpell() {
        super(SpellTypes.WATER);
        this.activeTicks = 1200;
    }

    @Override
    protected void onStart(SpellContext ctx) {
        this.healthAtStart = ctx.user().getHealth();

        ctx.user().getWorld().playSound(null, ctx.user().getBlockPos(),
                SoundEvents.BLOCK_POWDER_SNOW_PLACE, SoundCategory.PLAYERS, 1.0f, 1.5f);
    }

    @Override
    protected boolean shouldTrigger(SpellContext ctx, int tick, LivingEntity current) {
        return current.getHealth() < this.healthAtStart;
    }

    @Override
    protected void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current) {
        var attacker = ctx.user().getAttacker();

        if (attacker != null) {
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 300, 4));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 300, 1));
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 300, 3));

            world.spawnParticles(ParticleTypes.SNOWFLAKE,
                    attacker.getX(), attacker.getY() + 1, attacker.getZ(),
                    25, 0.5, 0.5, 0.5, 0.05);

            world.playSound(null, attacker.getBlockPos(),
                    SoundEvents.ENTITY_PLAYER_HURT_FREEZE, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    @Override
    protected boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current) {
        return false;
    }

    @Override
    protected void spawnArmedParticles(SpellContext ctx, int tick, int duration, LivingEntity current) {
        double angle = tick * 0.2;
        double x = current.getX() + Math.cos(angle) * 0.8;
        double z = current.getZ() + Math.sin(angle) * 0.8;

        current.getWorld().addParticle(ParticleTypes.INSTANT_EFFECT,
                x, current.getY() + 0.5, z, 0.0, 0.0, 0.0);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0;
    }
    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }
    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 3000), BTC.identifierOf("frost_reflex"));
    }
}
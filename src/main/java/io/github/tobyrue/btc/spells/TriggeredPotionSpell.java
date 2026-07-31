package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class TriggeredPotionSpell extends TriggeredSpell {

    private float healthAtStart;

    public TriggeredPotionSpell() {
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
        if (this.healthAtStart <= 0.0f) return false;
        return (current.getHealth() / this.healthAtStart) < ctx.data().getArgs().getFloat("percentHealth", 0.5f);
    }

    @Override
    protected void onTrigger(SpellContext ctx, ServerWorld world, int tick, LivingEntity current) {
        if (ctx.user() != null) {
            applyPotionEffect(ctx.user(), ctx.data().getArgs());
        }
    }

    @Override
    protected boolean isDisturbed(SpellContext ctx, int tick, LivingEntity current) {
        return false;
    }

    private void applyPotionEffect(LivingEntity user, GrabBag args) {
        Identifier id = Identifier.tryParse(args.getString("effect", "minecraft:regeneration"));
        if (id == null) return;

        Optional<RegistryEntry.Reference<StatusEffect>> entry = Registries.STATUS_EFFECT.getEntry(id);
        if (entry.isEmpty()) return;

        user.addStatusEffect(new StatusEffectInstance(
                entry.get(),
                args.getInt("duration", 200),
                args.getInt("amplifier", 0)
        ));
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("triggered_potion"));
    }

    @Override
    public int getColor(final GrabBag args) {
        Identifier id = Identifier.tryParse(args.getString("effect", "minecraft:regeneration"));
        if (id == null) return 0xFFFFFFFF;

        StatusEffect effect = Registries.STATUS_EFFECT.get(id);
        return effect != null ? (0xFF000000 | effect.getColor()) : 0xFFFFFFFF;
    }
}
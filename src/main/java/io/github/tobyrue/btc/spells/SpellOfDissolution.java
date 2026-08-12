package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModMaps;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpellOfDissolution extends ChanneledSpell implements UpgradableSpell {

    public SpellOfDissolution() {
        super(
                SpellTypes.GENERIC, 200, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill(ChanneledSpell::getCastTime)
                        .moveableDistance(12)
                        .hold(20)
                        .build(),
                true, ParticleTypes.REVERSE_PORTAL, ParticleAnimation.SPIRAL
        );
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF8833FF;
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        LivingEntity user = ctx.user();

        List<StatusEffectInstance> harmful = new ArrayList<>();

        for (StatusEffectInstance eff : user.getStatusEffects()) {
            if (eff.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL) {
                harmful.add(eff);
            }
        }

        if (harmful.isEmpty()) return;

        int interval = Math.max(1, args.getInt("castTime", this.castTime) / harmful.size());

        if (tick % interval != 0 || tick == 0) return;

        StatusEffectInstance chosen = harmful.get(user.getRandom().nextInt(harmful.size()));
        RegistryEntry<StatusEffect> type = chosen.getEffectType();

        user.removeStatusEffect(type);

        RegistryEntry<StatusEffect> opposite = ModMaps.EFFECT_OPPOSITES.get(type);
        if (opposite != null && opposite.value().isBeneficial()) {
            user.addStatusEffect(new StatusEffectInstance(
                    opposite,
                    Math.max(40, (int)(chosen.getDuration() * 0.8)),
                    Math.max(0, chosen.getAmplifier() / 2)
            ));
        }
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 1800), BTC.identifierOf("dissolution"));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_cast_time")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 1800, 600, 2400, -100, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "castTime", 200, 60, 400, -20, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}
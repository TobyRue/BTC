package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.TriggeredSpell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TriggeredPotionSpell extends TriggeredSpell implements UpgradableSpell {

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

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_potency")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", args.getInt("cooldown", 600), 200, 1200, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "duration", 200, 60, 600, 30, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amplifier", 0, 0, 5, 1, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}
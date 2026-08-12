package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LifeStealSpell extends Spell implements UpgradableSpell {
    public LifeStealSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFFDD40AB;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double radius = args.getDouble("radius", 10.0d);
        float healthPercentage = (float) args.getDouble("healthPercentage", 0.10d);
        float healRatio = (float) args.getDouble("healRatio", 0.5d);

        List<LivingEntity> targets = ctx.world().getEntitiesByClass(LivingEntity.class,
                new Box(ctx.user().getBlockPos()).expand(radius),
                entity -> entity != ctx.user() && entity.isAlive());

        for (LivingEntity target : targets) {
            float targetHealth = target.getHealth();
            float damage = targetHealth * healthPercentage;

            target.damage(ctx.world().getDamageSources().magic(), damage);

            float healAmount = damage * healRatio;
            ctx.user().heal(healAmount);
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 30), BTC.identifierOf("life_steal"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_health_drain")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_heal_ratio")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 30, 10, 100, -5, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 10.0, 5.0, 20.0, 1.5, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "healthPercentage", 0.10, 0.05, 0.30, 0.02, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "healRatio", 0.5, 0.25, 1.0, 0.1, BTC.identifierOf("quartz_upgrade"));
        return upgrades;
    }
}
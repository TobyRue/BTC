package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalizedStormPushSpell extends Spell implements UpgradableSpell {
    public LocalizedStormPushSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF02C1DB;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double shootStrength = args.getDouble("shootStrength", 7d);
        double verticalMultiplier = args.getDouble("verticalMultiplier", 2.2d);
        double range = args.getDouble("range", 24d);
        float damage = (float) args.getDouble("damage", 5.0d);

        var entity = isTargetInRange(ctx.user(), ctx.target(), range);
        if (entity == null) return;

        double dx = entity.getX() - ctx.user().getX();
        double dy = entity.getY() - ctx.user().getY();
        double dz = entity.getZ() - ctx.user().getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance != 0) {
            entity.setVelocity(dx / distance * shootStrength, (dy / distance * shootStrength), dz / distance * shootStrength);
            entity.setVelocity(entity.getVelocity().add(0, verticalMultiplier, 0));
        }

        entity.damage(ctx.world().getDamageSources().flyIntoWall(), damage);
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        assert ctx.user() != null;
        Entity target = isTargetInRange(ctx.user(), ctx.target(), args.getDouble("range", 24d));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("localized_storm_push"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("prismarine_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_push_strength")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_vertical_multiplier")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_damage")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 400, 150, 600, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "shootStrength", 7.0, 3.0, 15.0, 1.0, BTC.identifierOf("prismarine_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "verticalMultiplier", 2.2, 1.0, 5.0, 0.4, BTC.identifierOf("phantom_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "damage", 5.0, 2.0, 12.0, 1.0, BTC.identifierOf("amethyst_shard_upgrade"));
        return upgrades;
    }
}
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TempestsCallSpell extends Spell implements UpgradableSpell {

    public TempestsCallSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        double pull_radius = args.getDouble("pull_radius", 25d);
        double pull_strength = args.getDouble("pull_strength", 3d);
        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(pull_radius), entity -> entity != ctx.user());

        for (LivingEntity entity : entities) {
            double dx = ctx.user().getX() - entity.getX();
            double dy = ctx.user().getY() - entity.getY();
            double dz = ctx.user().getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (distance != 0) {
                entity.setVelocity(dx / distance * pull_strength, dy / distance * pull_strength, dz / distance * pull_strength);
            }
        }
    }

    @Override
    protected boolean canUse(final Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 160), BTC.identifierOf("tempests_call"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF84A1FF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("prismarine_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_pull_strength")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 160, 60, 320, -15, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "pull_radius", 25.0, 10.0, 45.0, 2.5, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "pull_strength", 3.0, 1.0, 8.0, 0.5, BTC.identifierOf("prismarine_upgrade"));
        return upgrades;
    }
}
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

public class StormPushSpell extends Spell implements UpgradableSpell {
    public StormPushSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        double shoot_radius = args.getDouble("shoot_radius", 15d);
        double shoot_strength = args.getDouble("shoot_strength", 5d);
        float damage = (float) args.getDouble("damage", 5.0d);

        List<LivingEntity> entities = ctx.world().getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(shoot_radius), entity -> entity != ctx.user());

        for (LivingEntity entity : entities) {
            double dx = entity.getX() - ctx.user().getX();
            double dy = entity.getY() - ctx.user().getY();
            double dz = entity.getZ() - ctx.user().getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance != 0) {
                entity.setVelocity(dx / distance * shoot_strength, dy / distance * shoot_strength, dz / distance * shoot_strength);
            }

            entity.damage(ctx.world().getDamageSources().flyIntoWall(), damage);
        }
    }

    @Override
    protected boolean canUse(final Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 240), BTC.identifierOf("storm_push"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF87E3FF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("prismarine_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_push_strength")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_damage")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 240, 80, 480, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "shoot_radius", 15.0, 5.0, 30.0, 2.5, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "shoot_strength", 5.0, 2.0, 12.0, 1.0, BTC.identifierOf("prismarine_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "damage", 5.0, 1.0, 15.0, 1.0, BTC.identifierOf("amethyst_shard_upgrade"));
        return upgrades;
    }
}
package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShadowStepSpell extends Spell implements UpgradableSpell {
    public ShadowStepSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF6B2BA3;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double range = args.getDouble("range", 24d);
        double teleportDistance = args.getDouble("teleportDistance", 2.5D);
        int invisDuration = args.getInt("invisDuration", 140);

        Entity target = isTargetInRange(ctx.user(), ctx.target(), range);
        if (target == null) return;

        Vec3d backward = target.getRotationVec(1.0F).normalize().negate();
        Vec3d targetPos = target.getPos();
        Vec3d newPos = targetPos.add(backward.multiply(teleportDistance));

        ctx.user().requestTeleport(newPos.x, newPos.y, newPos.z);

        ctx.user().addStatusEffect(new StatusEffectInstance(
                StatusEffects.INVISIBILITY, invisDuration, 0, false, false, true
        ));
    }

    @Override
    protected boolean canUse(SpellContext ctx, GrabBag args) {
        assert ctx.user() != null;
        Entity target = getEntityLookedAt(ctx.user(), args.getDouble("range", 24d),
                args.getDouble("aimingForgiveness", 0.3D));
        return target != null && super.canUse(ctx, args);
    }

    @Override
    public SpellCooldown getCooldown(GrabBag args, @Nullable LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 300), BTC.identifierOf("shadow_step"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("chorus_fruit_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_teleport_distance")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_invisibility_duration")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 300, 100, 500, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 24.0, 12.0, 48.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "teleportDistance", 2.5, 1.0, 6.0, 0.5, BTC.identifierOf("chorus_fruit_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "invisDuration", 140, 40, 300, 20, BTC.identifierOf("quartz_upgrade"));
        return upgrades;
    }
}
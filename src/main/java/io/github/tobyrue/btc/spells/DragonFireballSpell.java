package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DragonFireballSpell extends Spell implements UpgradableSpell {
    public DragonFireballSpell() {
        super(SpellTypes.ENDER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF944AAD;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        double velocityMult = args.getDouble("velocity", 1.5d);

        DragonFireballEntity dragonFireball = ctx.user() == null
                ? new DragonFireballEntity(EntityType.DRAGON_FIREBALL, ctx.world())
                : new DragonFireballEntity(ctx.world(), ctx.user(), ctx.direction());

        dragonFireball.setPos(
                ctx.pos().getX() + ctx.direction().x * 1.5,
                ctx.pos().getY() + ctx.direction().y * 1.5,
                ctx.pos().getZ() + ctx.direction().z * 1.5
        );
        dragonFireball.setVelocity(ctx.direction().multiply(velocityMult));
        ctx.world().spawnEntity(dragonFireball);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("dragon_fireball"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_velocity")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 800, 400, 1200, -50, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "velocity", 1.5, 0.8, 3.5, 0.25, BTC.identifierOf("phantom_upgrade"));
        return upgrades;
    }
}
package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
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

public class WaterBlastSpell extends Spell implements UpgradableSpell {

    public WaterBlastSpell() {
        super(SpellTypes.FIRE);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        WaterBlastEntity waterBlast = new WaterBlastEntity(ModEntities.WATER_BLAST, ctx.world());
        waterBlast.setPos(ctx.pos().getX() + ctx.direction().x * 1.5, ctx.pos().getY() + ctx.direction().y * 1.5, ctx.pos().getZ() + ctx.direction().z * 1.5);
        waterBlast.setVelocity(ctx.direction().multiply(args.getDouble("speed", 1.5)));
        if (ctx.user() != null) {
            waterBlast.setOwner(ctx.user());
        }
        waterBlast.setNoGravity(args.getBoolean("noGravity", false));
        ctx.world().spawnEntity(waterBlast);
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 200), BTC.identifierOf("water_blast"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF5177FF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_speed")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 200, 80, 400, -20, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "speed", 1.5, 0.8, 3.5, 0.3, BTC.identifierOf("phantom_upgrade"));
        return upgrades;
    }
}
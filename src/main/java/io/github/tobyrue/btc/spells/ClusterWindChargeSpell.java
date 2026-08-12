package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClusterWindChargeSpell extends Spell implements UpgradableSpell {

    public ClusterWindChargeSpell() {
        super(SpellTypes.WIND);
    }

    @Override
    protected void use(final SpellContext ctx, final GrabBag args) {
        int count = args.getInt("count", 8);
        double spreadFactor = args.getDouble("spreadFactor", 0.2d);
        double velocity = args.getDouble("velocity", 2.0d);

        for (int i = 0; i < count; i++) {
            WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, ctx.world());
            if (ctx.user() != null) {
                windCharge.setOwner(ctx.user());
            }

            double randomPitch = (Math.random() - 0.5) * spreadFactor;
            double randomYaw = (Math.random() - 0.5) * spreadFactor;

            Vec3d scatterDirection = ctx.direction().add(randomYaw, randomPitch, randomYaw).normalize();

            Vec3d spawnPosition = new Vec3d(ctx.pos().getX(), ctx.pos().getY(), ctx.pos().getZ()).add(ctx.direction().multiply(1.5));
            windCharge.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            windCharge.setVelocity(scatterDirection.multiply(velocity));
            ctx.world().spawnEntity(windCharge);
        }
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 80), BTC.identifierOf("cluster_wind_charge"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFA8FFF9;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("lapis_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_spread_factor")));
        upgrades.add(new Pair<>(BTC.identifierOf("ghast_tear_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_spread_factor")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_velocity")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 80, 20, 160, -10, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 8, 4, 20, 2, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 8, 4, 20, -2, BTC.identifierOf("lapis_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "spreadFactor", 0.2, 0.05, 0.8, 0.05, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "spreadFactor", 0.2, 0.05, 0.8, -0.05, BTC.identifierOf("ghast_tear_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "velocity", 2.0, 1.0, 4.0, 0.3, BTC.identifierOf("phantom_upgrade"));
        return upgrades;
    }
}
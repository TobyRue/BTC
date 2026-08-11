package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlazeStormSpell extends ChanneledSpell implements UpgradableSpell {

    public BlazeStormSpell() {
        super(
                SpellTypes.FIRE,
                100,
                1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill(ChanneledSpell::getCastTime)
                        .moveableDistance(0)
                        .hold(20)
                        .build()
        );
    }
    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        double deviation = args.getDouble("deviation", 0.5d);
        int amount = args.getInt("amount", 20);

        World world = ctx.world();
        LivingEntity user = ctx.user();

        int ticksPerShot = Math.max(1, castTime / amount);
        Vec3d dir = ctx.user().getRotationVec(1).normalize();

        if (tick % ticksPerShot == 0) {
            Vec3d dev = new Vec3d(
                    world.getRandom().nextTriangular(dir.x, deviation),
                    world.getRandom().nextTriangular(dir.y, deviation),
                    world.getRandom().nextTriangular(dir.z, deviation)
            ).normalize();

            SmallFireballEntity fireball;
            fireball = new SmallFireballEntity(world, user, dev);
            fireball.setPosition(user.getX(), user.getBodyY(0.5) + 0.5, user.getZ());

            world.spawnEntity(fireball);
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }


    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("blaze_storm"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF5400;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ghast_tear_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_deviation")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_deviation")));
        upgrades.add(new Pair<>(BTC.identifierOf("lapis_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_amount")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_amount")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_cast_time")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 200, 600, -40, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "deviation", 0.5, 0.1, 1, -0.1, BTC.identifierOf("ghast_tear_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "deviation", 0.5, 0.1, 1, 0.1, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amount", 10, 10, 30, -2, BTC.identifierOf("lapis_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "amount", 10, 10, 30, 2, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cast_time", 100, 100, 300, 20, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}

package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RiptideSpell extends Spell implements UpgradableSpell {

    public RiptideSpell() {
        super(SpellTypes.WATER);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF3FD0FF;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        LivingEntity user = ctx.user();

        int duration = args.getInt("duration", 20);
        float damage = (float) args.getDouble("damage", 2.0d);
        double speed = args.getDouble("speed", 2.0d);

        Vec3d dir = ctx.direction().normalize();
        Vec3d velocity = dir.multiply(speed);

        user.addVelocity(velocity.x, velocity.y, velocity.z);
        user.velocityModified = true;

        var sound = List.of(SoundEvents.ITEM_TRIDENT_RIPTIDE_1, SoundEvents.ITEM_TRIDENT_RIPTIDE_2, SoundEvents.ITEM_TRIDENT_RIPTIDE_3);
        var random = ctx.world().getRandom();
        if (user instanceof PlayerEntity player) {
            player.useRiptide(duration, damage, user.getMainHandStack());

            ctx.world().playSound(
                    player,
                    player.getBlockPos(),
                    sound.get(random.nextBetween(0, 2)).value(),
                    user.getSoundCategory(),
                    1.0F,
                    1.0F
            );
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("riptide"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_speed")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_damage")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 100, 40, 200, -10, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "speed", 2.0, 1.0, 4.5, 0.4, BTC.identifierOf("phantom_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "damage", 2.0, 1.0, 8.0, 1.0, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "duration", 20, 10, 60, 5, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}
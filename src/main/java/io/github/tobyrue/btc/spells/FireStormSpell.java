package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class FireStormSpell extends ChanneledSpell implements UpgradableSpell {

    public FireStormSpell() {
        super(
                SpellTypes.FIRE, 40, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.CLICK)
                        .disturbableTill((spell, args) -> args.getInt("castTime", 40))
                        .moveableDistance(-1)
                        .hold(20)
                        .build(),
                true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL, 0, false
        );
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        double maxRadius = args.getDouble("maxRadius", 8d);
        int duration = args.getInt("castTime", this.castTime);
        float maxDamage = (float) args.getDouble("maxDamage", 8.0d);
        var storedPos = start.pos();

        if (ctx.world() instanceof ServerWorld serverWorld) {
            double progress = (double) tick / (duration);
            double radius = maxRadius * progress;

            int count = (int) (maxRadius / 64d * 1280d);
            for (int i = 0; i < count; i++) {

                double angle = (2 * Math.PI / count) * i;

                double x = storedPos.getX() + Math.sin(angle) * radius;
                double z = storedPos.getZ() + Math.cos(angle) * radius;

                double yOffset = 0.2;
                double y = storedPos.getY() + yOffset;

                double xSpeed = Math.sin(angle) * 0.2;
                double zSpeed = Math.cos(angle) * 0.2;

                serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
            }

            for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, ctx.user().getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != ctx.user())) {
                double dist = target.getPos().distanceTo(storedPos);

                double stepSize = maxRadius / duration;
                if (dist <= radius && dist > (radius - stepSize)) {
                    target.setOnFireFor((float) ((radius * -1) + maxRadius));
                    target.damage(ctx.user().getDamageSources().inFire(), Math.min(maxDamage, (float) ((radius * -1) + maxRadius)));
                }
            }
        }
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("fire_storm"));
    }

    @Override
    public Text getDescription(GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getDouble("maxRadius", 8d) >= 9d ? (args.getDouble("maxRadius", 8d) == 9d ? "normal" : "strong") : "concentrated") + ".description");
    }

    @Override
    public Text getName(final GrabBag args) {
        return Text.translatable(this.getTranslationKey() + "." + (args.getDouble("maxRadius", 8d) >= 9d ? (args.getDouble("maxRadius", 8d) == 9d ? "normal" : "strong") : "concentrated"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFFF9400;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("lapis_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_max_damage")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_cast_time")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 400, 200, 800, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "maxRadius", 8.0, 4.0, 16.0, 1.0, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "maxRadius", 8.0, 4.0, 16.0, -1.0, BTC.identifierOf("lapis_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "maxDamage", 8.0, 4.0, 20.0, 2.0, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cast_time", 40, 20, 120, 10, BTC.identifierOf("echo_shard_upgrade"));
        return upgrades;
    }
}
package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PurgeBoltSpell extends Spell implements UpgradableSpell {

    public PurgeBoltSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        World world = ctx.world();
        LivingEntity user = ctx.user();
        if (user == null) return;

        double speed = args.getDouble("speed", 0.7d); // ~7 blocks/sec
        int lifetime = args.getInt("lifetime", 60);   // 3 seconds
        double range = args.getDouble("range", 10.0d);
        double forgiveness = args.getDouble("forgiveness", 0.3d);

        Vec3d start = user.getCameraPosVec(1.0F);
        Vec3d dir = user.getRotationVec(1.0F).normalize();
        Vec3d lookVec = user.getRotationVec(1.0F).normalize();

        ((Ticker.TickerTarget) user).bTC$add(
                Ticker.forTicks(tick -> {
                    Vec3d pos = start.add(dir.multiply(speed * tick));

                    if (!world.isClient) {
                        ((ServerWorld) world).spawnParticles(
                                ParticleTypes.LARGE_SMOKE,
                                pos.x, pos.y + 0.1, pos.z,
                                1,
                                0, 0, 0,
                                0
                        );
                    } else {
                        world.addParticle(ParticleTypes.LARGE_SMOKE,
                                pos.x, pos.y + 0.1, pos.z, 0, 0, 0);
                    }

                    if (!world.isClient) {
                        var entity = getEntityLookedAt(lookVec, user, range, forgiveness);
                        if (entity instanceof LivingEntity target) {
                            for (StatusEffectInstance effect : target.getStatusEffects().toArray(new StatusEffectInstance[0])) {
                                var type = effect.getEffectType();
                                if (type.value().getCategory() == StatusEffectCategory.BENEFICIAL) {
                                    target.removeStatusEffect(type);
                                }
                            }
                            return true;
                        }
                    }

                    return tick >= lifetime;
                }, lifetime)
        );
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 100), BTC.identifierOf("purge_bolt"));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFFAAAAAA;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_speed")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 100, 40, 200, -10, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "speed", 0.7, 0.3, 2.0, 0.2, BTC.identifierOf("phantom_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "lifetime", 60, 20, 180, 15, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 10.0, 5.0, 25.0, 2.5, BTC.identifierOf("ender_pearl_upgrade"));
        return upgrades;
    }
}
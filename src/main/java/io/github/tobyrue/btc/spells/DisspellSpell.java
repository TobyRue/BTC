package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModComponents;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.*;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class DisspellSpell extends Spell implements UpgradableSpell {

    public DisspellSpell() {
        super(SpellTypes.GENERIC);
    }

    @Override
    public void use(final SpellContext ctx, final GrabBag args) {
        World world = ctx.world();
        LivingEntity user = ctx.user();
        if (user == null) return;

        double speed = args.getDouble("speed", 1.2d);
        int lifetime = args.getInt("lifetime", 40);
        double range = args.getDouble("range", 20.0d);
        double forgiveness = args.getDouble("forgiveness", 0.6d);
        int silenceDuration = args.getInt("silenceDuration", 160);
        int globalDuration = args.getInt("globalSilenceDuration", 60);

        Vec3d start = user.getCameraPosVec(1.0F);
        Vec3d dir = user.getRotationVec(1.0F).normalize();
        Vec3d lookVec = user.getRotationVec(1.0F).normalize();

        ((Ticker.TickerTarget) user).bTC$add(
                Ticker.forTicks(tick -> {

                    Vec3d pos = start.add(dir.multiply(speed * tick));

                    if (!world.isClient) {
                        ((ServerWorld) world).spawnParticles(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                    }
                    var entity = getEntityLookedAt(lookVec, user, range, forgiveness);

                    if (!world.isClient) {
                        if (entity instanceof LivingEntity target) {

                            if (target instanceof SpellHost<?> host) {
                                silenceAnyHost(host, target, silenceDuration, globalDuration);
                            }

                            checkHand(target, target.getMainHandStack(), silenceDuration, globalDuration);
                            checkHand(target, target.getOffHandStack(), silenceDuration, globalDuration);

                            ((ServerWorld) world).spawnParticles(ParticleTypes.FLASH, target.getX(), target.getEyeY(), target.getZ(), 1, 0, 0, 0, 0);
                            return true;
                        }
                    }

                    return tick >= lifetime;
                }, lifetime)
        );
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 600), BTC.identifierOf("disspell"));
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF00FFFF;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_speed")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_silence_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_global_silence_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_forgiveness")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 600, 300, 900, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 20.0, 10.0, 40.0, 3.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "speed", 1.2, 0.6, 2.5, 0.2, BTC.identifierOf("phantom_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "silenceDuration", 160, 80, 400, 20, BTC.identifierOf("netherite_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "globalSilenceDuration", 60, 20, 200, 15, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "forgiveness", 0.6, 0.2, 1.5, 0.1, BTC.identifierOf("quartz_upgrade"));
        return upgrades;
    }
}
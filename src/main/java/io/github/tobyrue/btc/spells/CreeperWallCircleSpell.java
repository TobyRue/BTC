package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import io.github.tobyrue.xml.util.Nullable;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreeperWallCircleSpell extends Spell implements UpgradableSpell {
    public CreeperWallCircleSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF8A9E32;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        int spikes = args.getInt("spikes", 20);
        double radius = args.getDouble("radius", 3.0d);
        int regenDuration = args.getInt("regen_duration", 120);
        int regenAmplifier = args.getInt("regen_amplifier", 1);

        if (regenDuration > 0) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, regenDuration, regenAmplifier));
        }

        for (int i = 0; i < spikes; i++) {
            double angle = 2 * Math.PI * i / spikes;
            double x = user.getX() + radius * Math.cos(angle);
            double z = user.getZ() + radius * Math.sin(angle);
            BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) user.getY(), (int) z), 10);
            if (groundPos != null) {
                CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, user.getYaw(), user, CreeperPillarType.NORMAL);
                world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(user));
                world.spawnEntity(pillar);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    public static BlockPos findSpawnableGroundPillar(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            if (!(world.getBlockState(pos).getBlock() instanceof AirBlock) && world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
                return pos;
            }
        }

        return null;
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 1200), BTC.identifierOf("creeper_wall_circle"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_spikes")));
        upgrades.add(new Pair<>(BTC.identifierOf("lapis_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_spikes")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_regen_duration")));
        upgrades.add(new Pair<>(BTC.identifierOf("netherite_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_regen_potency")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 1200, 600, 1800, -40, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "spikes", 20, 10, 36, 4, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "spikes", 20, 10, 36, -4, BTC.identifierOf("lapis_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 3.0, 1.5, 7.0, -0.5, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 3.0, 1.5, 7.0, 0.5, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "regen_duration", 120, 60, 300, 30, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "regen_amplifier", 1, 1, 3, 1, BTC.identifierOf("netherite_upgrade"));
        return upgrades;
    }
}
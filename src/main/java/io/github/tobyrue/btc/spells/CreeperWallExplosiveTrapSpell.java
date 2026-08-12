package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.CreeperPillarEntity;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import net.minecraft.block.AirBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class CreeperWallExplosiveTrapSpell extends Spell implements UpgradableSpell {
    public CreeperWallExplosiveTrapSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    public int getColor(GrabBag args) {
        return 0xFF6CA56B;
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        double aimingForgiveness = args.getDouble("aimingForgiveness", 0.3D);
        double radius = args.getDouble("radius", 2.0D);
        double range = args.getDouble("range", 16.0D);
        int yRange = args.getInt("yRange", 10);
        int count = args.getInt("count", 17);

        Entity entityLookedAt = isTargetInRange(ctx.user(), ctx.target(), range);
        if (entityLookedAt != null) {
            for (int i = 0; i < count; i++) {
                double angle = 2 * Math.PI * i / count;
                double x = entityLookedAt.getX() + radius * Math.cos(angle);
                double z = entityLookedAt.getZ() + radius * Math.sin(angle);
                BlockPos groundPos = findSpawnableGroundPillar(world, new BlockPos((int) x, (int) entityLookedAt.getY(), (int) z), yRange);
                if (entityLookedAt instanceof LivingEntity) {
                    if (groundPos != null) {
                        CreeperPillarEntity pillar = new CreeperPillarEntity(world, x, groundPos.getY(), z, entityLookedAt.getYaw(), user, CreeperPillarType.RANDOM);
                        world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(user));
                        world.spawnEntity(pillar);
                    }
                }
            }
        }
    }

    @Nullable
    public static BlockPos findSpawnableGroundPillar(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (!(world.getBlockState(pos).getBlock() instanceof AirBlock) && world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
                return pos;
            }
        }

        // Fallback if no valid ground is found
        return null;
    }

    public static @Nullable Entity getEntityLookedAt(LivingEntity player, double range, double aimmingForgivness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit())) {
            Box entityBox = entity.getBoundingBox().expand(aimmingForgivness); // slightly expanded hitbox
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public Spell.SpellCooldown getCooldown(final GrabBag args, @io.github.tobyrue.xml.util.Nullable final LivingEntity user) {
        return new Spell.SpellCooldown(args.getInt("cooldown", 800), BTC.identifierOf("creeper_wall_explosive_trap"));
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("blaze_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("lapis_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("ender_pearl_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_range")));
        upgrades.add(new Pair<>(BTC.identifierOf("quartz_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("ghast_tear_upgrade"), Text.translatable("scroll_upgrade.btc.description.decrease_radius")));
        upgrades.add(new Pair<>(BTC.identifierOf("phantom_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_yrange")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 800, 400, 1200, -40, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 17, 8, 30, 2, BTC.identifierOf("blaze_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 17, 8, 30, -2, BTC.identifierOf("lapis_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "range", 16.0, 8.0, 32.0, 2.0, BTC.identifierOf("ender_pearl_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 2.0, 1.0, 5.0, 0.5, BTC.identifierOf("quartz_upgrade"));
        UpgradableSpell.withDoubleUpgrade(args, upgrades, "radius", 2.0, 1.0, 5.0, -0.5, BTC.identifierOf("ghast_tear_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "yRange", 10, 5, 25, 3, BTC.identifierOf("phantom_upgrade"));
        return upgrades;
    }
}
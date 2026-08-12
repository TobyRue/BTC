package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.UpgradableSpell;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

public class RaiseUndeadSpell extends ChanneledSpell implements UpgradableSpell {

    private static final WeakHashMap<LivingEntity, Boolean> STORED_TEAM = new WeakHashMap<>();

    private static final List<EntityType<? extends LivingEntity>> UNDEAD_TYPES = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.HUSK,
            EntityType.STRAY
    );

    public RaiseUndeadSpell() {
        super(
                SpellTypes.EARTH, 35 * 20, 1,
                DisturbConfig.builder()
                        .level(DistributionLevels.MOVE_DAMAGE_AND_CLICK)
                        .disturbableTill(5 * 20)
                        .moveableDistance(3)
                        .hold(20)
                        .build(),
                true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL, 5 * 20, true
        );
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick, final Start start) {
        var user = ctx.user();
        var world = ctx.world();

        if (!(world instanceof ServerWorld serverWorld)) return;

        Random random = world.random;
        int count = args.getInt("count", 10);

        ServerScoreboard scoreboard = serverWorld.getServer().getScoreboard();

        var et = scoreboard.getTeams().stream().filter(t -> t.getName().endsWith("_undead_team_BTC_RAISE_UNDEAD_SPELL")).toList();

        String teamName = user.getUuidAsString() + "_undead_team_BTC_RAISE_UNDEAD";

        Team knownTeam = user.getScoreboardTeam();

        if (!et.isEmpty()) {
            for (var t: et) {
                killAllEntitiesOnTeam(serverWorld, knownTeam);
                scoreboard.removeTeam(t);
            }
        }

        if (knownTeam == null) {
            knownTeam = scoreboard.addTeam(teamName);
            knownTeam.setDisplayName(Text.literal(user.getName().getString() + " Undead"));
            knownTeam.setColor(net.minecraft.util.Formatting.DARK_GREEN);
            STORED_TEAM.put(ctx.user(), true);
        } else {
            STORED_TEAM.put(ctx.user(), false);
        }

        scoreboard.addScoreHolderToTeam(user.getNameForScoreboard(), knownTeam);
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.2F);
        List<LivingEntity> summoned = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            EntityType<? extends LivingEntity> type = UNDEAD_TYPES.get(random.nextInt(UNDEAD_TYPES.size()));
            LivingEntity undead = type.create(world);
            if (undead == null) continue;

            Vec3d pos = user.getPos().add(
                    (random.nextDouble() - 0.5) * 8.0,
                    0,
                    (random.nextDouble() - 0.5) * 8.0
            );
            undead.refreshPositionAndAngles(pos.x, findSpawnableGround(world, user.getBlockPos(), 24) == null ? pos.getY() : findSpawnableGround(world, user.getBlockPos(), 24).getY() + 2, pos.z, random.nextFloat() * 360F, 0);

            scoreboard.addScoreHolderToTeam(undead.getNameForScoreboard(), knownTeam);

            if (undead instanceof SkeletonEntity || undead instanceof StrayEntity) {
                ItemStack bow = new ItemStack(Items.BOW);
                undead.equipStack(EquipmentSlot.MAINHAND, bow);
            } else if (undead instanceof ZombieEntity || undead instanceof HuskEntity) {
                ItemStack sword = new ItemStack(Items.STONE_SWORD);
                undead.equipStack(EquipmentSlot.MAINHAND, sword);
            }

            ItemStack helmet = new ItemStack(Items.LEATHER_HELMET);
            undead.equipStack(EquipmentSlot.HEAD, helmet);

            world.spawnEntity(undead);
            summoned.add(undead);

            serverWorld.spawnParticles(
                    ParticleTypes.SOUL,
                    pos.x, pos.y + 1.0, pos.z,
                    10, 0.5, 0.5, 0.5, 0.02
            );
        }
    }

    @Override
    protected void runEnd(SpellContext ctx, GrabBag args, int tick) {
        var user = ctx.user();
        var world = ctx.world();

        if (world instanceof ServerWorld serverWorld) {
            Team knownTeam = user.getScoreboardTeam();

            if (knownTeam != null) {
                killAllEntitiesOnTeam(serverWorld, knownTeam);
                if (STORED_TEAM.get(ctx.user())) {

                    ServerScoreboard scoreboard = serverWorld.getServer().getScoreboard();

                    scoreboard.removeTeam(knownTeam);
                }
            }
        }
        super.runEnd(ctx, args, tick);
    }

    public void killAllEntitiesOnTeam(ServerWorld serverWorld, Team knownTeam) {
        if (knownTeam == null) return;

        Box worldBox = new Box(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7);

        serverWorld.getEntitiesByClass(LivingEntity.class, worldBox, e -> e.getScoreboardTeam() != null && e.getScoreboardTeam().getName().equals(knownTeam.getName()) && (e instanceof ZombieEntity || e instanceof StrayEntity || e instanceof SkeletonEntity || e instanceof HuskEntity)).forEach(LivingEntity::kill);
    }

    @Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());

            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                return pos;
            }
        }

        return null;
    }

    @Override
    public SpellCooldown getCooldown(final GrabBag args, @Nullable final LivingEntity user) {
        return new SpellCooldown(args.getInt("cooldown", 400), BTC.identifierOf("raise_undead"));
    }

    @Override
    protected boolean canUse(Spell.SpellContext ctx, final GrabBag args) {
        return ctx.user() != null && super.canUse(ctx, args);
    }

    @Override
    public int getColor(final GrabBag args) {
        return 0xFF3CFF9B;
    }

    @Override
    public List<Pair<Identifier, Text>> getUpgradeDescriptions() {
        final List<Pair<Identifier, Text>> upgrades = new ArrayList<>();
        upgrades.add(new Pair<>(BTC.identifierOf("gold_ingot_upgrade"), Text.translatable("scroll_upgrade.btc.description.cooldown")));
        upgrades.add(new Pair<>(BTC.identifierOf("amethyst_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_summon_count")));
        upgrades.add(new Pair<>(BTC.identifierOf("echo_shard_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_cast_time")));
        upgrades.add(new Pair<>(BTC.identifierOf("copper_upgrade"), Text.translatable("scroll_upgrade.btc.description.increase_moveable_distance")));
        return upgrades;
    }

    @Override
    public HashMap<Identifier, Pair<String, ?>> getUpgradeOptions(GrabBag args) {
        final HashMap<Identifier, Pair<String, ?>> upgrades = new HashMap<>();
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cooldown", 400, 200, 800, -30, BTC.identifierOf("gold_ingot_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "count", 10, 3, 25, 2, BTC.identifierOf("amethyst_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "cast_time", 700, 200, 1200, 100, BTC.identifierOf("echo_shard_upgrade"));
        UpgradableSpell.withIntegerUpgrade(args, upgrades, "moveableDistance", 3, 0, 15, 2, BTC.identifierOf("copper_upgrade"));
        return upgrades;
    }
}
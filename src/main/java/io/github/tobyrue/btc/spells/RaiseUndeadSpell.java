package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.Tick;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RaiseUndeadSpell extends ChanneledSpell {

    private static final List<EntityType<? extends LivingEntity>> UNDEAD_TYPES = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.HUSK,
            EntityType.STRAY
    );

    public RaiseUndeadSpell() {
        super(SpellTypes.EARTH, 35 * 20, 5 * 20, DistributionLevels.DAMAGE_CROUCH_AND_MOVE, true, ParticleTypes.ENCHANTED_HIT, ParticleAnimation.SPIRAL, 5 * 20, true, 5 * 20);
    }

    @Override
    protected void useChanneled(SpellContext ctx, GrabBag args, int tick) {
        var user = ctx.user();
        var world = ctx.world();

        if (!(world instanceof ServerWorld serverWorld)) return;

        Random random = world.random;
        int count = args.getInt("count", 10);

        // ---- Get or Create Temporary Team ----
        ServerScoreboard scoreboard = serverWorld.getServer().getScoreboard();

        var et = scoreboard.getTeams().stream().filter(t -> t.getPlayerList().isEmpty() && t.getName().endsWith("_undead_team")).toList();
        if (!et.isEmpty()) {
            for (var t: et) {
                scoreboard.removeTeam(t);
            }
        }



        String teamName = user.getUuidAsString() + "_undead_team";

        Team knownTeam = user.getScoreboardTeam();

        boolean temporaryTeam;

        if (knownTeam == null) {
            knownTeam = scoreboard.addTeam(teamName);
            knownTeam.setDisplayName(Text.literal(user.getName().getString() + " Undead"));
            knownTeam.setColor(net.minecraft.util.Formatting.DARK_GREEN);
            temporaryTeam = true;
        } else {
            temporaryTeam = false;
        }

        // Add summoner to the team (ensures allies)
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
            undead.refreshPositionAndAngles(pos.x, findSpawnableGround(world, user.getBlockPos(), 24).getY() + 2, pos.z, random.nextFloat() * 360F, 0);

            // Join the team to prevent friendly fire
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
                    net.minecraft.particle.ParticleTypes.SOUL,
                    pos.x, pos.y + 1.0, pos.z,
                    10, 0.5, 0.5, 0.5, 0.02
            );
        }
    }


    @Override
    protected void runEnd(SpellContext ctx, GrabBag args, int tick) {
        var user = ctx.user();
        var world = ctx.world();


        if(world instanceof ServerWorld serverWorld) {
            Team knownTeam = user.getScoreboardTeam();

            if (knownTeam != null) {
                killAllEntitiesOnTeam(serverWorld, knownTeam, user);
            }
        }
    }

    public void killAllEntitiesOnTeam(ServerWorld serverWorld, Team knownTeam, LivingEntity excluded) {
        Scoreboard scoreboard = serverWorld.getScoreboard();
        if (knownTeam == null) return;

        Box worldBox = new Box(-3.0E7, -3.0E7, -3.0E7, 3.0E7, 3.0E7, 3.0E7);

        serverWorld.getEntitiesByClass(LivingEntity.class, worldBox, e -> e.getScoreboardTeam() != null && e.getScoreboardTeam().getName().equals(knownTeam.getName()) && e != excluded).forEach(LivingEntity::kill);
    }

    @org.jetbrains.annotations.Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());
        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                return pos;
            }
        }

        // Fallback if no valid ground is found
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
        return 0x3CFF9B;
    }
}

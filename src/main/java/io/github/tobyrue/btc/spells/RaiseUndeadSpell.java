package io.github.tobyrue.btc.spells;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.enums.SpellTypes;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RaiseUndeadSpell extends Spell {

    private static final List<EntityType<? extends LivingEntity>> UNDEAD_TYPES = List.of(
            EntityType.ZOMBIE,
            EntityType.SKELETON,
            EntityType.HUSK,
            EntityType.STRAY
    );

    public RaiseUndeadSpell() {
        super(SpellTypes.EARTH);
    }

    @Override
    protected void use(SpellContext ctx, GrabBag args) {
        var user = ctx.user();
        var world = ctx.world();
        int cnt = args.getInt("count", 10);   // 3 seconds

        Random random = world.random;
        Team ownerTeam = (user instanceof PlayerEntity player) ? player.getScoreboardTeam() : user.getScoreboardTeam();

        // Play a cool spooky sound
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 1.0F, 1.2F);

        for (int i = 0; i < cnt; i++) {
            EntityType<? extends LivingEntity> type = UNDEAD_TYPES.get(random.nextInt(UNDEAD_TYPES.size()));
            LivingEntity undead = type.create(world);
            if (undead == null) continue;

            // Random position around user
            Vec3d pos = user.getPos().add(
                    (random.nextDouble() - 0.5) * 8.0,
                    0,
                    (random.nextDouble() - 0.5) * 8.0
            );

            undead.refreshPositionAndAngles(pos.x, pos.y, pos.z, random.nextFloat() * 360F, 0);

            // Prevent targeting summoner or their allies
            if (ownerTeam != null) {
                ServerScoreboard scoreboard = ctx.world().getServer().getScoreboard();
                ((Scoreboard)scoreboard).addScoreHolderToTeam(undead.getNameForScoreboard(), ctx.user().getScoreboardTeam());
            }

            // Spawn and effects
            world.spawnEntity(undead);
            if (world instanceof ServerWorld s) {
                s.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SOUL,
                        pos.x, pos.y + 1.0, pos.z,
                        10, 0.5, 0.5, 0.5, 0.02
                );
            }
        }
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

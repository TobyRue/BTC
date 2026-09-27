package io.github.tobyrue.btc.entity.ai.brain;

import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EldritchLuminaryBrain {

    private final EldritchLuminaryEntity mob;

    private LivingEntity retaliationTarget;
    private int retaliationTicks;

    private UUID preferredPlayer;

    private Identifier lastSpell;
    private Identifier previousSpell;

    private int comboTicks;
    private int ticksSinceSpell;

    private int damagedRecentlyTicks;
    private int closeRangeTicks;
    private int targetLostTicks;

    private int decisionCooldown;

    public EldritchLuminaryBrain(EldritchLuminaryEntity mob) {
        this.mob = mob;
    }

    // ============================================================
    // MAIN BRAIN
    // ============================================================

    public void tick() {

        if (decisionCooldown > 0) {
            decisionCooldown--;
        }

        if (retaliationTicks > 0) {
            retaliationTicks--;
        } else {
            retaliationTarget = null;
        }

        if (damagedRecentlyTicks > 0) {
            damagedRecentlyTicks--;
        }

        if (comboTicks > 0) {
            comboTicks--;
        }

        ticksSinceSpell++;

        maintainTarget();

        if (mob.getTarget() != null) {
            double distance = mob.squaredDistanceTo(mob.getTarget());

            if (distance < 16.0D) {
                closeRangeTicks++;
            } else {
                closeRangeTicks = Math.max(0, closeRangeTicks - 2);
            }

            targetLostTicks = 0;
        } else {
            targetLostTicks++;
            closeRangeTicks = 0;
        }

        if (decisionCooldown <= 0) {
            updatePositioning();
            decisionCooldown = 5;
        }
    }

    // ============================================================
    // DAMAGE / RETALIATION
    // ============================================================

    public void onDamaged(DamageSource source) {

        Entity attacker = source.getAttacker();

        if (!(attacker instanceof LivingEntity living)) {
            return;
        }

        if (!isValidEnemy(living)) {
            return;
        }

        /*
         * Getting attacked creates a temporary high-priority
         * retaliation target.
         */
        retaliationTarget = living;
        retaliationTicks = 100;
        damagedRecentlyTicks = 100;

        /*
         * Immediately respond if this isn't another Luminary.
         */
        mob.setTarget(living);
    }

    // ============================================================
    // TARGETING
    // ============================================================

    private void maintainTarget() {

        LivingEntity current = mob.getTarget();

        /*
         * Retaliation has priority while the memory is active.
         */
        if (retaliationTarget != null
                && retaliationTicks > 0
                && isValidEnemy(retaliationTarget)
                && mob.squaredDistanceTo(retaliationTarget) <= 48.0D * 48.0D) {

            if (current != retaliationTarget) {
                mob.setTarget(retaliationTarget);
            }

            return;
        }

        /*
         * If the current target is still good, keep it.
         *
         * Players are always preferred over ordinary mobs when
         * we're not actively retaliating.
         */
        if (current != null && isValidEnemy(current)) {

            if (!(current instanceof PlayerEntity)) {
                PlayerEntity player = findNearestPlayer();

                if (player != null) {
                    mob.setTarget(player);
                }
            }

            return;
        }

        /*
         * Normal priority:
         * nearest player.
         */
        PlayerEntity player = findNearestPlayer();

        if (player != null) {
            mob.setTarget(player);
            preferredPlayer = player.getUuid();
            return;
        }

        /*
         * No player available. Don't randomly pick a mob.
         *
         * The only time a non-player gets targeted without being
         * the attacker is through retaliation.
         */
        mob.setTarget(null);
    }

    private PlayerEntity findNearestPlayer() {

        Box searchBox =
                mob.getBoundingBox().expand(48.0D, 16.0D, 48.0D);

        List<PlayerEntity> players =
                mob.getWorld().getEntitiesByClass(
                        PlayerEntity.class,
                        searchBox,
                        player -> player.isAlive()
                                && !player.isSpectator()
                                && !player.isCreative()
                                && Math.abs(player.getY() - mob.getY()) <= 16.0D
                );

        return players.stream()
                .min(Comparator.comparingDouble(mob::squaredDistanceTo))
                .orElse(null);
    }

    public boolean isValidEnemy(LivingEntity entity) {

        if (entity == null
                || entity == mob
                || !entity.isAlive()) {
            return false;
        }

        /*
         * NEVER target another Eldritch Luminary.
         */
        if (entity instanceof EldritchLuminaryEntity) {
            return false;
        }

        /*
         * Players are always valid hostile targets.
         */
        if (entity instanceof PlayerEntity player) {
            return !player.isSpectator()
                    && !player.isCreative();
        }

        /*
         * Team protection applies to non-player creatures.
         */
        AbstractTeam myTeam = mob.getScoreboardTeam();
        AbstractTeam targetTeam = entity.getScoreboardTeam();

        if (myTeam != null
                && targetTeam != null
                && myTeam.isEqual(targetTeam)) {
            return false;
        }

        return true;
    }

    // ============================================================
    // POSITIONING
    // ============================================================

    private void updatePositioning() {

        LivingEntity target = mob.getTarget();

        if (target == null || !target.isAlive()) {
            return;
        }

        /*
         * Don't fight the navigation system while swimming.
         */
        if (mob.isTouchingWater()) {
            return;
        }

        double distanceSq = mob.squaredDistanceTo(target);

        double idealMin;
        double idealMax;

        switch (mob.getArchetype()) {

            case PYROMANCER -> {
                idealMin = 9.0D;
                idealMax = 18.0D;
            }

            case STORM_WARDEN -> {
                idealMin = 10.0D;
                idealMax = 20.0D;
            }

            case SHADOW_SUMMONER -> {
                idealMin = 8.0D;
                idealMax = 16.0D;
            }

            case SUPPORT -> {
                idealMin = 14.0D;
                idealMax = 24.0D;
            }

            default -> {
                idealMin = 8.0D;
                idealMax = 18.0D;
            }
        }


        if (distanceSq < idealMin * idealMin) {

            Vec3d away =
                    mob.getPos()
                            .subtract(target.getPos())
                            .multiply(1.0D, 0.0D, 1.0D);

            if (away.lengthSquared() > 0.0001D) {
                away = away.normalize();

                double x =
                        mob.getX() + away.x * 6.0D;

                double z =
                        mob.getZ() + away.z * 6.0D;

                mob.getNavigation().startMovingTo(
                        x,
                        mob.getY(),
                        z,
                        1.05D
                );
            }

            return;
        }


        if (distanceSq > idealMax * idealMax) {

            mob.getNavigation().startMovingTo(
                    target,
                    0.9D
            );
        }
    }

    // ============================================================
    // SUPPORT
    // ============================================================

    public boolean shouldActWithoutPlayer() {

        if (mob.getArchetype()
                != EldritchLuminaryEntity.LuminaryArchetype.SUPPORT) {
            return false;
        }

        return findAllyNeedingSupport() != null;
    }

    public EldritchLuminaryEntity findAllyNeedingSupport() {

        Box box =
                mob.getBoundingBox().expand(18.0D);

        List<EldritchLuminaryEntity> allies =
                mob.getWorld().getEntitiesByClass(
                        EldritchLuminaryEntity.class,
                        box,
                        entity ->
                                entity != mob
                                        && entity.isAlive()
                                        && entity.getHealth()
                                        < entity.getMaxHealth() * 0.90F
                );

        return allies.stream()
                .min(Comparator.comparingDouble(mob::squaredDistanceTo))
                .orElse(null);
    }

    public Spell.InstancedSpell chooseSpell() {

        List<Spell.InstancedSpell> spells =
                mob.getAllSpellInstances();

        if (spells.isEmpty()) {
            return new Spell.InstancedSpell(
                    ModSpells.EMPTY,
                    io.github.tobyrue.btc.spell.GrabBag.empty()
            );
        }

        LivingEntity target = mob.getTarget();

        EldritchLuminaryEntity.LuminaryArchetype archetype =
                mob.getArchetype();

        /*
         * Support can act without a player.
         */
        boolean supportAction =
                archetype
                        == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                        && findAllyNeedingSupport() != null;

        List<ScoredSpell> candidates =
                new ArrayList<>();

        for (Spell.InstancedSpell spell : spells) {

            if (spell == null || spell.spell() == null) {
                continue;
            }

            if (spell.spell() == ModSpells.EMPTY) {
                continue;
            }

            /*
             * Normal combat spells require a target.
             *
             * Support spells are allowed without one.
             */
            if (target == null && !supportAction) {
                continue;
            }

            if (!mob.canUseSpell(spell)) {
                continue;
            }

            double score =
                    scoreSpell(spell, target, archetype);

            if (score <= 0.0D) {
                continue;
            }

            candidates.add(
                    new ScoredSpell(spell, score)
            );
        }

        if (candidates.isEmpty()) {
            return new Spell.InstancedSpell(
                    ModSpells.EMPTY,
                    io.github.tobyrue.btc.spell.GrabBag.empty()
            );
        }


        candidates.sort(
                Comparator.comparingDouble(ScoredSpell::score)
                        .reversed()
        );

        int count =
                Math.min(4, candidates.size());

        double totalWeight = 0.0D;

        for (int i = 0; i < count; i++) {
            totalWeight += candidates.get(i).score();
        }

        double random =
                mob.getRandom().nextDouble() * totalWeight;

        for (int i = 0; i < count; i++) {

            random -= candidates.get(i).score();

            if (random <= 0.0D) {
                return candidates.get(i).spell();
            }
        }

        return candidates.getFirst().spell();
    }

    private double scoreSpell(
            Spell.InstancedSpell spell,
            LivingEntity target,
            EldritchLuminaryEntity.LuminaryArchetype archetype
    ) {

        double score =
                Math.max(
                        0.1D,
                        mob.getSpellWeight(spell)
                );

        double distance =
                target == null
                        ? 0.0D
                        : mob.distanceTo(target);

        double health =
                mob.getHealth()
                        / mob.getMaxHealth();


        Identifier spellId =
                mob.getSpellId(spell);

        if (Objects.equals(spellId, lastSpell)) {
            score *= 0.18D;
        }


        if (health < 0.35D) {

            if (spell.spell() == ModSpells.LIFE_STEAL) {
                score += 35.0D;
            }

            if (spell.spell() == ModSpells.ICE_BLOCK) {
                score += 30.0D;
            }

            if (spell.spell() == ModSpells.SHADOW_STEP) {
                score += 22.0D;
            }

            if (spell.spell() == ModSpells.MIST_VEIL) {
                score += 18.0D;
            }

            if (spell.spell() == ModSpells.TRIGGERED_POTION) {
                score += 32.0D;
            }
        }

        /*
         * ========================================================
         * VERY CLOSE
         * ========================================================
         */

        if (distance <= 4.0D) {

            if (spell.spell() == ModSpells.STORM_PUSH
                    || spell.spell() == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell() == ModSpells.WIND_TORNADO) {

                score += 35.0D;
            }

            if (spell.spell() == ModSpells.FLAME_BURST) {
                score += 22.0D;
            }

            if (spell.spell() == ModSpells.DRAGONS_BREATH) {
                score += 18.0D;
            }

            if (spell.spell() == ModSpells.EARTH_SPIKE_LINE) {
                score += 18.0D;
            }

            if (spell.spell() == ModSpells.SHADOW_STEP) {
                score += 20.0D;
            }
        }

        /*
         * ========================================================
         * PLAYER TOO FAR
         * ========================================================
         */

        if (distance >= 20.0D) {

            if (spell.spell() == ModSpells.FIREBALL) {
                score += 18.0D;
            }

            if (spell.spell() == ModSpells.DRAGON_FIREBALL) {
                score += 25.0D;
            }

            if (spell.spell() == ModSpells.LIGHTNING_STRIKE) {
                score += 18.0D;
            }

            if (spell.spell() == ModSpells.ABYSSAL_SHARDS) {
                score += 12.0D;
            }

            if (spell.spell() == ModSpells.SHULKER_BULLET) {
                score += 10.0D;
            }
        }

        /*
         * ========================================================
         * TARGET IS MOVING / NORMAL COMBAT
         * ========================================================
         */

        if (target != null) {

            Vec3d velocity =
                    target.getVelocity();

            double movementSpeed =
                    velocity.lengthSquared();

            if (movementSpeed > 0.025D) {

                if (spell.spell() == ModSpells.LIGHTNING_STRIKE) {
                    score += 10.0D;
                }

                if (spell.spell() == ModSpells.EARTH_SPIKE_LINE) {
                    score += 10.0D;
                }

                if (spell.spell() == ModSpells.SHULKER_BULLET) {
                    score += 8.0D;
                }
            }

            /*
             * Stationary target = area denial opportunity.
             */
            if (movementSpeed < 0.005D) {

                if (spell.spell() == ModSpells.EARTH_SPIKE_LINE) {
                    score += 16.0D;
                }

                if (spell.spell() == ModSpells.FIRE_STORM) {
                    score += 14.0D;
                }

                if (spell.spell() == ModSpells.BLAZE_STORM) {
                    score += 12.0D;
                }

                if (spell.spell() == ModSpells.TEMPESTS_CALL) {
                    score += 14.0D;
                }
            }
        }

        /*
         * ========================================================
         * RETALIATION
         * ========================================================
         */

        if (retaliationTarget != null
                && retaliationTicks > 0
                && target == retaliationTarget) {

            score += 10.0D;

            if (spell.spell()
                    == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell() == ModSpells.STORM_PUSH) {

                score += 10.0D;
            }
        }

        /*
         * ========================================================
         * ARCHETYPE PERSONALITY
         * ========================================================
         */

        switch (archetype) {

            case PYROMANCER -> {

                if (spell.spell().getSpellType()
                        == io.github.tobyrue.btc.enums.SpellTypes.FIRE) {

                    score += 10.0D;
                }

                if (spell.spell() == ModSpells.FIREBALL) {
                    score += 8.0D;
                }

                if (spell.spell() == ModSpells.FIRE_STORM) {
                    score += 10.0D;
                }

                if (spell.spell() == ModSpells.DRAGONS_BREATH) {
                    score += 7.0D;
                }
            }

            case STORM_WARDEN -> {

                if (spell.spell() == ModSpells.LIGHTNING_STRIKE
                        || spell.spell() == ModSpells.TEMPESTS_CALL) {

                    score += 8.0D;
                }

                if (spell.spell() == ModSpells.EARTH_SPIKE_LINE
                        || spell.spell() == ModSpells.WIND_TORNADO) {

                    score += 10.0D;
                }

                if (spell.spell() == ModSpells.FROST_REFLEX) {
                    score += 12.0D;
                }
            }

            case SHADOW_SUMMONER -> {

                if (spell.spell() == ModSpells.ELDRITCH_TETHER) {
                    score += 12.0D;
                }

                if (spell.spell() == ModSpells.ABYSSAL_SHARDS) {
                    score += 9.0D;
                }

                if (spell.spell() == ModSpells.RAISE_UNDEAD) {

                    if (countNearbySummons() == 0) {
                        score += 18.0D;
                    } else {
                        score -= 10.0D;
                    }
                }

                if (spell.spell() == ModSpells.SHADOW_STEP) {
                    score += 7.0D;
                }
            }

            case SUPPORT -> {

                if (spell.spell()
                        == ModSpells.LUMINARY_EMPOWER) {

                    EldritchLuminaryEntity ally =
                            findAllyNeedingSupport();

                    if (ally != null) {
                        score += 45.0D;

                        if (ally.getHealth()
                                < ally.getMaxHealth() * 0.5F) {
                            score += 15.0D;
                        }
                    }
                }

                if (spell.spell() == ModSpells.TRIGGERED_POTION
                        && health < 0.55D) {

                    score += 28.0D;
                }

                if (spell.spell() == ModSpells.ICE_BLOCK) {
                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.LOCALIZED_STORM_PUSH
                        && distance < 7.0D) {

                    score += 24.0D;
                }

                /*
                 * Support should be less interested in damage
                 * when allies need help.
                 */
                if (findAllyNeedingSupport() != null
                        && isDamageSpell(spell)) {

                    score *= 0.55D;
                }
            }

            default -> {
                /*
                 * ALL gets general-purpose behavior.
                 */
            }
        }

        /*
         * ========================================================
         * COMBOS
         * ========================================================
         */

        if (comboTicks > 0) {

            /*
             * Push -> punish
             */
            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.LOCALIZED_STORM_PUSH || ModRegistries.SPELL.get(lastSpell) == ModSpells.STORM_PUSH) {

                if (spell.spell() == ModSpells.FIREBALL
                        || spell.spell() == ModSpells.LIGHTNING_STRIKE
                        || spell.spell() == ModSpells.ABYSSAL_SHARDS) {

                    score += 28.0D;
                }
            }


            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.FIREBALL) {

                if (spell.spell() == ModSpells.FLAME_BURST
                        || spell.spell() == ModSpells.BLAZE_STORM
                        || spell.spell() == ModSpells.FIRE_STORM) {

                    score += 24.0D;
                }
            }

            /*
             * Control -> punishment
             */
            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.ICE_BLOCK
                    || ModRegistries.SPELL.get(lastSpell) == ModSpells.EARTH_SPIKE_LINE) {

                if (spell.spell() == ModSpells.LIGHTNING_STRIKE
                        || spell.spell() == ModSpells.FIREBALL) {

                    score += 24.0D;
                }
            }

            /*
             * Tether -> projectiles.
             */
            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.ELDRITCH_TETHER) {

                if (spell.spell() == ModSpells.ABYSSAL_SHARDS
                        || spell.spell() == ModSpells.SHULKER_BULLET) {

                    score += 30.0D;
                }
            }

            /*
             * Illusion -> teleport.
             */
            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.ELDRITCH_ILLUSION
                    && spell.spell() == ModSpells.SHADOW_STEP) {

                score += 35.0D;
            }

            /*
             * Summon -> empower.
             */
            if (ModRegistries.SPELL.get(lastSpell) == ModSpells.RAISE_UNDEAD
                    && spell.spell()
                    == ModSpells.LUMINARY_EMPOWER) {

                score += 15.0D;
            }
        }

        /*
         * Don't cast support buff repeatedly.
         */
        if (spell.spell() == ModSpells.LUMINARY_EMPOWER) {

            EldritchLuminaryEntity ally =
                    findAllyNeedingSupport();

            if (ally == null) {
                score = 0.0D;
            }
        }

        return score;
    }

    private boolean isDamageSpell(
            Spell.InstancedSpell spell
    ) {

        return spell.spell() != ModSpells.LUMINARY_EMPOWER
                && spell.spell() != ModSpells.TRIGGERED_POTION
                && spell.spell() != ModSpells.POTION
                && spell.spell() != ModSpells.ICE_BLOCK
                && spell.spell() != ModSpells.MIST_VEIL;
    }

    private int countNearbySummons() {

        Box box =
                mob.getBoundingBox().expand(16.0D);

        return (int) mob.getWorld()
                .getOtherEntities(
                        mob,
                        box,
                        entity ->
                                entity instanceof LivingEntity living
                                        && !(living instanceof PlayerEntity)
                                        && !(living instanceof EldritchLuminaryEntity)
                )
                .stream()
                .count();
    }

    public void onSpellCast(
            Spell.InstancedSpell spell
    ) {

        if (spell == null || spell.spell() == null) {
            return;
        }

        previousSpell = lastSpell;
        lastSpell = mob.getSpellId(spell);

        comboTicks = 80;
        ticksSinceSpell = 0;
    }

    public Identifier getLastSpell() {
        return lastSpell;
    }

    public Identifier getPreviousSpell() {
        return previousSpell;
    }

    private record ScoredSpell(
            Spell.InstancedSpell spell,
            double score
    ) {
    }
}
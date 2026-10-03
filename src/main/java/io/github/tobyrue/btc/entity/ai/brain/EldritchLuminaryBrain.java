package io.github.tobyrue.btc.entity.ai.brain;

import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.regestries.ModRegistries;
import io.github.tobyrue.btc.regestries.ModSpells;
import io.github.tobyrue.btc.spell.ChanneledSpell;
import io.github.tobyrue.btc.spell.GrabBag;
import io.github.tobyrue.btc.spell.Spell;
import io.github.tobyrue.btc.spell.SpellDataStore;
import io.github.tobyrue.btc.spell.SpellHost;
import io.github.tobyrue.btc.spell.SpellItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

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
    private int decisionCooldown;
    private int hardControlCooldown;
    private int trappedPressureCooldown;


    private int combatBreakTicks;
    private int nextCombatBreakTick;


    private int damagePressure;


    private Vec3d previousVelocity = Vec3d.ZERO;
    private Vec3d previousPreviousVelocity = Vec3d.ZERO;
    private int stableMovementTicks;


    private int playerCastingObservationTicks;
    private UUID playerCastingTarget;

    public EldritchLuminaryBrain(EldritchLuminaryEntity mob) {
        this.mob = mob;


        this.nextCombatBreakTick =
                220 + mob.getRandom().nextInt(121);
    }

    public void tick() {

        if (retaliationTicks > 0) {
            retaliationTicks--;
        } else {
            retaliationTarget = null;
        }

        if (damagedRecentlyTicks > 0) {
            damagedRecentlyTicks--;
        }

        if (hardControlCooldown > 0) {
            hardControlCooldown--;
        }

        if (trappedPressureCooldown > 0) {
            trappedPressureCooldown--;
        }

        if (comboTicks > 0) {
            comboTicks--;
        }

        if (combatBreakTicks > 0) {
            combatBreakTicks--;
        }

        if (decisionCooldown > 0) {
            decisionCooldown--;
        }

        if (damagePressure > 0
                && mob.getArchetype()
                != EldritchLuminaryEntity.LuminaryArchetype.SUPPORT) {

            damagePressure--;
        }

        ticksSinceSpell++;

        maintainTarget();
        updatePlayerPrediction();
        updateCastingObservation();
        maybeStartCombatBreak();

        if (decisionCooldown <= 0) {
            decisionCooldown = 4;
        }
    }


    public void onDamaged(DamageSource source) {

        Entity attacker = source.getAttacker();


        damagePressure =
                Math.min(100, damagePressure + 18);

        damagedRecentlyTicks = 100;

        if (!(attacker instanceof LivingEntity living)) {
            return;
        }

        if (!isValidEnemy(living)) {
            return;
        }


        if (living instanceof PlayerEntity player) {

            preferredPlayer = player.getUuid();

            retaliationTarget = player;
            retaliationTicks = 180;

            mob.setTarget(player);
            return;
        }

        LivingEntity current = mob.getTarget();


        if (current instanceof PlayerEntity
                && isValidEnemy(current)) {

            return;
        }


        retaliationTarget = living;
        retaliationTicks = 80;

        mob.setTarget(living);
    }


    private void maintainTarget() {

        LivingEntity current = mob.getTarget();


        if (retaliationTarget instanceof PlayerEntity player
                && retaliationTicks > 0
                && isValidEnemy(player)) {

            if (current != player) {
                mob.setTarget(player);
            }

            preferredPlayer = player.getUuid();
            return;
        }


        if (current instanceof PlayerEntity player
                && isValidEnemy(player)
                && mob.squaredDistanceTo(player)
                <= 64.0D * 64.0D) {

            preferredPlayer = player.getUuid();
            return;
        }

        PlayerEntity preferred = findPreferredPlayer();

        if (preferred != null) {
            mob.setTarget(preferred);
            return;
        }


        PlayerEntity nearest = findNearestPlayer();

        if (nearest != null) {
            preferredPlayer = nearest.getUuid();
            mob.setTarget(nearest);
            return;
        }


        if (retaliationTarget != null
                && retaliationTicks > 0
                && isValidEnemy(retaliationTarget)) {

            mob.setTarget(retaliationTarget);
            return;
        }


        if (current != null
                && isValidEnemy(current)) {

            return;
        }

        mob.setTarget(null);
    }

    private PlayerEntity findPreferredPlayer() {

        if (preferredPlayer == null) {
            return null;
        }

        Box box =
                mob.getBoundingBox()
                        .expand(64.0D, 24.0D, 64.0D);

        for (PlayerEntity player :
                mob.getWorld().getEntitiesByClass(
                        PlayerEntity.class,
                        box,
                        player ->
                                player.getUuid().equals(preferredPlayer)
                                        && isValidEnemy(player)
                )) {

            return player;
        }

        preferredPlayer = null;
        return null;
    }

    private PlayerEntity findNearestPlayer() {

        Box box =
                mob.getBoundingBox()
                        .expand(64.0D, 24.0D, 64.0D);

        return mob.getWorld()
                .getEntitiesByClass(
                        PlayerEntity.class,
                        box,
                        this::isValidEnemy
                )
                .stream()
                .min(
                        Comparator.comparingDouble(
                                mob::squaredDistanceTo
                        )
                )
                .orElse(null);
    }

    public boolean isValidEnemy(LivingEntity entity) {

        if (entity == null
                || entity == mob
                || !entity.isAlive()) {

            return false;
        }


        if (entity instanceof EldritchLuminaryEntity) {
            return false;
        }


        if (entity instanceof PlayerEntity player) {
            return !player.isSpectator()
                    && !player.isCreative();
        }


        AbstractTeam myTeam =
                mob.getScoreboardTeam();

        AbstractTeam targetTeam =
                entity.getScoreboardTeam();

        if (myTeam != null
                && targetTeam != null
                && myTeam.isEqual(targetTeam)) {

            return false;
        }

        return true;
    }


    private void maybeStartCombatBreak() {


        if (mob.getArchetype()
                == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT) {

            return;
        }

        if (combatBreakTicks > 0
                || mob.isCastingSpell()) {

            return;
        }

        if (mob.getTarget() == null) {
            return;
        }

        boolean damageForcedBreak =
                damagePressure >= 55
                        && damagedRecentlyTicks > 0;

        boolean scheduledBreak =
                mob.age >= nextCombatBreakTick;

        if (!damageForcedBreak
                && !scheduledBreak) {

            return;
        }


        combatBreakTicks =
                50 + mob.getRandom().nextInt(31);

        damagePressure = 0;


        nextCombatBreakTick =
                mob.age
                        + 220
                        + mob.getRandom().nextInt(121);
    }

    public boolean isCombatBreaking() {
        return combatBreakTicks > 0
                && mob.getArchetype()
                != EldritchLuminaryEntity.LuminaryArchetype.SUPPORT;
    }

    public int getCombatBreakTicks() {
        return combatBreakTicks;
    }


    public boolean shouldActWithoutPlayer() {

        return mob.getArchetype()
                == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                && findAllyNeedingSupport() != null;
    }


    public boolean shouldSeekSupport() {

        if (mob.getArchetype()
                == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT) {

            return false;
        }

        double healthRatio =
                mob.getHealth() / mob.getMaxHealth();

        if (healthRatio > 0.35D
                && !isCombatBreaking()) {

            return false;
        }

        EldritchLuminaryEntity support =
                findNearestSupport();

        if (support == null) {
            return false;
        }

        double supportHealth =
                support.getHealth()
                        / support.getMaxHealth();

        return supportHealth > 0.35D
                && mob.squaredDistanceTo(support)
                <= 28.0D * 28.0D;
    }

    public EldritchLuminaryEntity findNearestSupport() {

        Box box =
                mob.getBoundingBox()
                        .expand(28.0D, 12.0D, 28.0D);

        return mob.getWorld()
                .getEntitiesByClass(
                        EldritchLuminaryEntity.class,
                        box,
                        entity ->
                                entity != mob
                                        && entity.isAlive()
                                        && entity.getArchetype()
                                        == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                )
                .stream()
                .min(
                        Comparator.comparingDouble(
                                mob::squaredDistanceTo
                        )
                )
                .orElse(null);
    }


    public EldritchLuminaryEntity findAllyNeedingSupport() {

        Box box =
                mob.getBoundingBox()
                        .expand(22.0D, 12.0D, 22.0D);

        List<EldritchLuminaryEntity> allies =
                mob.getWorld()
                        .getEntitiesByClass(
                                EldritchLuminaryEntity.class,
                                box,
                                entity ->
                                        entity != mob
                                                && entity.isAlive()
                                                && entity.getHealth()
                                                < entity.getMaxHealth()
                                                * 0.92F
                        );

        return allies.stream()
                .max(
                        Comparator.comparingDouble(
                                this::supportNeedScore
                        )
                )
                .orElse(null);
    }

    private double supportNeedScore(
            EldritchLuminaryEntity ally
    ) {

        double ownRatio =
                mob.getHealth() / mob.getMaxHealth();

        double allyRatio =
                ally.getHealth()
                        / ally.getMaxHealth();


        double score =
                (1.0D - allyRatio) * 100.0D;


        if (allyRatio < ownRatio) {
            score += 30.0D;
        }


        if (allyRatio < 0.50D) {
            score += 25.0D;
        }


        if (ally.getTarget() instanceof PlayerEntity) {
            score += 15.0D;
        }


        long otherSupports =
                mob.getWorld()
                        .getEntitiesByClass(
                                EldritchLuminaryEntity.class,
                                ally.getBoundingBox().expand(18.0D),
                                other ->
                                        other != mob
                                                && other != ally
                                                && other.isAlive()
                                                && other.getArchetype()
                                                == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                        )
                        .size();

        if (allyRatio > ownRatio - 0.10D) {
            score -= otherSupports * 25.0D;
        }

        score -=
                Math.sqrt(
                        mob.squaredDistanceTo(ally)
                ) * 0.20D;

        return score;
    }

    public boolean hasNearbyLuminaryCasting() {

        Box box =
                mob.getBoundingBox()
                        .expand(18.0D);

        return mob.getWorld()
                .getEntitiesByClass(
                        EldritchLuminaryEntity.class,
                        box,
                        entity ->
                                entity != mob
                                        && entity.isAlive()
                                        && entity.isCastingSpell()
                )
                .stream()
                .findAny()
                .isPresent();
    }

    public int countNearbyLuminaries() {

        Box box =
                mob.getBoundingBox()
                        .expand(20.0D);

        return mob.getWorld()
                .getEntitiesByClass(
                        EldritchLuminaryEntity.class,
                        box,
                        entity ->
                                entity != mob
                                        && entity.isAlive()
                )
                .size();
    }

    private void updatePlayerPrediction() {

        LivingEntity target =
                mob.getTarget();

        if (!(target instanceof PlayerEntity player)
                || !player.isAlive()) {

            stableMovementTicks = 0;
            previousVelocity = Vec3d.ZERO;
            previousPreviousVelocity = Vec3d.ZERO;
            return;
        }

        Vec3d current =
                horizontal(
                        player.getVelocity()
                );

        Vec3d previous =
                horizontal(
                        previousVelocity
                );

        Vec3d previousPrevious =
                horizontal(
                        previousPreviousVelocity
                );

        boolean moving =
                current.lengthSquared() >= 0.0324D;

        boolean firstStable =
                moving
                        && directionSimilarity(
                        current,
                        previous
                ) >= 0.995D;

        boolean secondStable =
                previous.lengthSquared() >= 0.0324D
                        && directionSimilarity(
                        previous,
                        previousPrevious
                ) >= 0.995D;

        double speedDelta =
                Math.abs(
                        current.length()
                                - previous.length()
                );

        boolean speedStable =
                speedDelta <= 0.035D;

        boolean predictableGroundMovement =
                player.isOnGround();

        if (moving
                && firstStable
                && secondStable
                && speedStable
                && predictableGroundMovement) {

            stableMovementTicks++;

        } else {

            stableMovementTicks = 0;
        }

        previousPreviousVelocity =
                previousVelocity;

        previousVelocity =
                player.getVelocity();
    }

    private double directionSimilarity(
            Vec3d a,
            Vec3d b
    ) {

        if (a.lengthSquared() < 0.0001D
                || b.lengthSquared() < 0.0001D) {

            return 0.0D;
        }

        return a.normalize()
                .dotProduct(
                        b.normalize()
                );
    }

    private Vec3d horizontal(Vec3d value) {
        return new Vec3d(
                value.x,
                0.0D,
                value.z
        );
    }


    public boolean hasHighConfidencePrediction() {
        return stableMovementTicks >= 6;
    }


    public Vec3d getAimPoint(
            Spell.InstancedSpell spell,
            LivingEntity target
    ) {
        Vec3d current =
                target.getPos()
                        .add(
                                0.0D,
                                target.getStandingEyeHeight() * 0.55D,
                                0.0D
                        );

        if (spell == null
                || spell.spell() != ModSpells.FIREBALL
                || !hasHighConfidencePrediction()) {
            return current;
        }

        Vec3d velocity =
                horizontal(
                        target.getVelocity()
                );

        if (velocity.lengthSquared() < 0.0001D) {
            return current;
        }

        Vec3d predicted =
                current.add(
                        velocity.multiply(5.0D)
                );

        Vec3d offset =
                predicted.subtract(current);

        if (offset.lengthSquared() > 2.5D * 2.5D) {
            predicted =
                    current.add(
                            offset.normalize()
                                    .multiply(2.5D)
                    );
        }

        return predicted;
    }

    private void updateCastingObservation() {

        LivingEntity target =
                mob.getTarget();

        if (!(target instanceof PlayerEntity player)) {

            playerCastingObservationTicks = 0;
            playerCastingTarget = null;

            return;
        }

        if (isPlayerCasting(player)) {

            if (!Objects.equals(
                    playerCastingTarget,
                    player.getUuid()
            )) {

                playerCastingTarget =
                        player.getUuid();

                playerCastingObservationTicks = 0;
            }

            playerCastingObservationTicks =
                    Math.min(
                            20,
                            playerCastingObservationTicks + 1
                    );

        } else {

            playerCastingObservationTicks =
                    Math.max(
                            0,
                            playerCastingObservationTicks - 2
                    );

            if (playerCastingObservationTicks == 0) {
                playerCastingTarget = null;
            }
        }
    }

    public boolean isPlayerCastingWithWarning() {

        return playerCastingObservationTicks >= 8
                && mob.getTarget() instanceof PlayerEntity player
                && Objects.equals(
                playerCastingTarget,
                player.getUuid()
        );
    }

    @SuppressWarnings("unchecked")
    private boolean isPlayerCasting(
            PlayerEntity player
    ) {


        if (player instanceof SpellHost<?> host) {

            SpellDataStore data =
                    ((SpellHost<LivingEntity>) host)
                            .getSpellDataStore(player);

            if (data != null
                    && data.getSpell()
                    instanceof ChanneledSpell) {

                return true;
            }
        }


        return isSpellItemCasting(
                player.getMainHandStack()
        )
                || isSpellItemCasting(
                player.getOffHandStack()
        );
    }

    private boolean isSpellItemCasting(
            ItemStack stack
    ) {

        if (stack.isEmpty()
                || !(stack.getItem()
                instanceof SpellItem spellItem)) {

            return false;
        }

        SpellDataStore data =
                spellItem.getSpellDataStore(stack);

        return data != null
                && data.getSpell()
                instanceof ChanneledSpell;
    }

    public boolean isTargetRestricted() {

        LivingEntity target =
                mob.getTarget();

        return target != null
                && isTargetRestricted(target);
    }

    public boolean isTargetRestricted(
            LivingEntity target
    ) {

        BlockPos center =
                BlockPos.ofFloored(
                        target.getX(),
                        target.getY(),
                        target.getZ()
                );

        int blocked = 0;
        int iceBlocks = 0;

        for (Direction direction :
                Direction.Type.HORIZONTAL) {

            BlockPos side =
                    center.offset(direction);

            BlockState state =
                    mob.getWorld()
                            .getBlockState(side);

            if (!state.getCollisionShape(
                    mob.getWorld(),
                    side
            ).isEmpty()) {

                blocked++;
            }

            if (state.isOf(Blocks.ICE)
                    || state.isOf(Blocks.PACKED_ICE)
                    || state.isOf(Blocks.BLUE_ICE)) {

                iceBlocks++;
            }
        }


        return blocked >= 3
                || iceBlocks >= 2;
    }


    public boolean canUseShadowStep() {

        LivingEntity target =
                mob.getTarget();

        if (target == null
                || isTargetRestricted(target)) {

            return false;
        }

        Vec3d backwards =
                horizontal(
                        target.getRotationVector()
                );

        if (backwards.lengthSquared()
                < 0.0001D) {

            return false;
        }

        backwards =
                backwards.normalize();

        Vec3d destination =
                target.getPos()
                        .subtract(
                                backwards.multiply(2.5D)
                        );

        BlockPos foot =
                BlockPos.ofFloored(destination);

        World world =
                mob.getWorld();

        return world.getBlockState(foot)
                .getCollisionShape(
                        world,
                        foot
                )
                .isEmpty()

                && world.getBlockState(foot.up())
                .getCollisionShape(
                        world,
                        foot.up()
                )
                .isEmpty()

                && world.getBlockState(foot.down())
                .isSolidBlock(
                        world,
                        foot.down()
                );
    }


    public Spell.InstancedSpell chooseSpell() {

        if (isCombatBreaking()) {
            return emptySpell();
        }

        List<Spell.InstancedSpell> spells =
                mob.getAllSpellInstances();

        if (spells.isEmpty()) {
            return emptySpell();
        }

        LivingEntity target =
                mob.getTarget();

        EldritchLuminaryEntity.LuminaryArchetype archetype =
                mob.getArchetype();

        EldritchLuminaryEntity supportAlly =
                findAllyNeedingSupport();

        boolean canActWithoutPlayer =
                archetype
                        == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                        && supportAlly != null;

        if (target == null
                && !canActWithoutPlayer) {

            return emptySpell();
        }

        List<ScoredSpell> candidates =
                new ArrayList<>();

        for (Spell.InstancedSpell spell :
                spells) {

            if (spell == null
                    || spell.spell() == null
                    || spell.spell() == ModSpells.EMPTY) {

                continue;
            }

            if (!mob.canUseSpell(spell)) {

                if (!(spell.spell()
                        == ModSpells.LUMINARY_EMPOWER
                        && canActWithoutPlayer)) {

                    continue;
                }
            }

            double score =
                    scoreSpell(
                            spell,
                            target,
                            supportAlly,
                            archetype
                    );

            if (score > 0.0D) {
                candidates.add(
                        new ScoredSpell(
                                spell,
                                score
                        )
                );
            }
        }

        if (candidates.isEmpty()) {
            return emptySpell();
        }

        candidates.sort(
                Comparator.comparingDouble(
                        ScoredSpell::score
                ).reversed()
        );

        int candidateCount =
                Math.min(
                        4,
                        candidates.size()
                );

        double totalWeight = 0.0D;

        for (int i = 0; i < candidateCount; i++) {
            totalWeight +=
                    candidates.get(i).score();
        }

        double point =
                mob.getRandom().nextDouble()
                        * totalWeight;

        for (int i = 0; i < candidateCount; i++) {

            point -=
                    candidates.get(i).score();

            if (point <= 0.0D) {
                return candidates.get(i).spell();
            }
        }

        return candidates.getFirst().spell();
    }
    public boolean shouldRetreat() {
        if (mob.getArchetype() == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT) {
            return mob.getHealth() / mob.getMaxHealth() <= 0.22F;
        }

        return isCombatBreaking()
                || shouldSeekSupport()
                || mob.getHealth() / mob.getMaxHealth() <= 0.25F;
    }

    public double getDesiredMinDistance() {
        return switch (mob.getArchetype()) {
            case PYROMANCER -> mob.getHealth() / mob.getMaxHealth() < 0.45D ? 11.0D : 9.0D;
            case STORM_WARDEN -> 10.0D;
            case SHADOW_SUMMONER -> 9.0D;
            case SUPPORT -> 16.0D;
            default -> 9.0D;
        };
    }

    public double getDesiredMaxDistance() {
        return switch (mob.getArchetype()) {
            case PYROMANCER -> mob.getHealth() / mob.getMaxHealth() < 0.45D ? 21.0D : 18.0D;
            case STORM_WARDEN -> 21.0D;
            case SHADOW_SUMMONER -> 17.0D;
            case SUPPORT -> 26.0D;
            default -> 18.0D;
        };
    }

    private record ScoredSpell(
            Spell.InstancedSpell spell,
            double score
    ) {
    }
    private double scoreSpell(
            Spell.InstancedSpell spell,
            LivingEntity target,
            EldritchLuminaryEntity supportAlly,
            EldritchLuminaryEntity.LuminaryArchetype archetype
    ) {

        double score =
                Math.max(
                        0.1D,
                        mob.getSpellWeight(spell)
                );

        double selfHealth =
                mob.getHealth()
                        / mob.getMaxHealth();

        double distance =
                target == null
                        ? 0.0D
                        : mob.distanceTo(target);

        boolean restricted =
                target != null
                        && isTargetRestricted(target);

        boolean playerCasting =
                target instanceof PlayerEntity
                        && isPlayerCastingWithWarning();

        boolean playerBuffed =
                target instanceof PlayerEntity
                        && hasImportantBeneficialEffects(
                        (PlayerEntity) target
                );

        boolean nearbyCaster =
                hasNearbyLuminaryCasting();

        Identifier id =
                mob.getSpellId(spell);

        if (Objects.equals(
                id,
                lastSpell
        )) {

            score *= 0.20D;
        }

        if (Objects.equals(
                id,
                previousSpell
        )) {

            score *= 0.55D;
        }

        if (spell.spell() == ModSpells.DISSPELL) {

            if (!playerCasting) {
                return 0.0D;
            }

            score += 95.0D;
        }


        if (spell.spell() == ModSpells.PURGE_BOLT) {

            if (!playerBuffed) {
                score *= 0.05D;
            } else {
                score += 70.0D;
            }
        }


        if (spell.spell() == ModSpells.SHADOW_STEP) {

            if (!canUseShadowStep()) {
                return 0.0D;
            }
        }


        if (spell.spell()
                == ModSpells.LUMINARY_EMPOWER) {

            if (supportAlly == null) {
                return 0.0D;
            }

            score += 65.0D;

            double allyHealth =
                    supportAlly.getHealth()
                            / supportAlly.getMaxHealth();

            if (allyHealth < 0.50D) {
                score += 35.0D;
            }

            if (supportAlly.getTarget()
                    instanceof PlayerEntity) {

                score += 20.0D;
            }
        }

        if (archetype
                == EldritchLuminaryEntity.LuminaryArchetype.SUPPORT
                && supportAlly != null
                && isDamageSpell(spell)) {

            score *= 0.45D;
        }


        if (selfHealth < 0.30D) {

            if (spell.spell() == ModSpells.LIFE_STEAL) {
                score += 45.0D;
            }

            if (spell.spell() == ModSpells.TRIGGERED_POTION) {
                score += 40.0D;
            }

            if (spell.spell() == ModSpells.ICE_BLOCK) {
                score += 36.0D;
            }

            if (spell.spell() == ModSpells.MIST_VEIL) {
                score += 28.0D;
            }

            if (spell.spell()
                    == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell()
                    == ModSpells.STORM_PUSH) {

                score += 25.0D;
            }
        }


        if (shouldSeekSupport()) {

            if (spell.spell()
                    == ModSpells.SHADOW_STEP
                    && canUseShadowStep()) {

                score += 8.0D;
            }

            if (spell.spell()
                    == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell()
                    == ModSpells.STORM_PUSH
                    || spell.spell()
                    == ModSpells.WIND_TORNADO) {

                score += 12.0D;
            }

            if (isDamageSpell(spell)) {
                score *= 0.65D;
            }
        }


        if (restricted) {


            if (trappedPressureCooldown > 0
                    && isDamageSpell(spell)) {

                score *= 0.28D;
            }


            if (isHardControlSpell(spell)) {

                if (trappedPressureCooldown > 0) {
                    return 0.0D;
                }

                score *= 0.60D;
            }


            if (spell.spell()
                    == ModSpells.SHADOW_STEP) {

                return 0.0D;
            }

            if (spell.spell()
                    == ModSpells.FIREBALL
                    || spell.spell()
                    == ModSpells.LIGHTNING_STRIKE
                    || spell.spell()
                    == ModSpells.ABYSSAL_SHARDS) {

                score += 8.0D;
            }
        }


        if (distance <= 4.5D) {

            if (spell.spell()
                    == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell()
                    == ModSpells.STORM_PUSH
                    || spell.spell()
                    == ModSpells.WIND_TORNADO) {

                score += 34.0D;
            }

            if (spell.spell()
                    == ModSpells.FLAME_BURST
                    || spell.spell()
                    == ModSpells.DRAGONS_BREATH) {

                score += 18.0D;
            }

            if (spell.spell()
                    == ModSpells.SHADOW_STEP
                    && canUseShadowStep()) {

                score += 16.0D;
            }
        }


        if (distance >= 18.0D) {

            if (spell.spell() == ModSpells.FIREBALL) {
                score += 14.0D;
            }

            if (spell.spell()
                    == ModSpells.DRAGON_FIREBALL) {

                score += 20.0D;
            }

            if (spell.spell()
                    == ModSpells.LIGHTNING_STRIKE) {

                score += 14.0D;
            }

            if (spell.spell()
                    == ModSpells.SHULKER_BULLET) {

                score += 10.0D;
            }
        }

        if (target != null) {
            double movement =
                    horizontal(
                            target.getVelocity()
                    ).lengthSquared();

            if (spell.spell() == ModSpells.FIREBALL
                    && hasHighConfidencePrediction()) {

                score += 10.0D;
            }
        }


        if (target != null
                && retaliationTarget == target
                && retaliationTicks > 0) {

            score +=
                    target instanceof PlayerEntity
                            ? 15.0D
                            : 8.0D;


            if (distance <= 5.0D
                    && (spell.spell() == ModSpells.LOCALIZED_STORM_PUSH
                    || spell.spell() == ModSpells.STORM_PUSH)) {

                score += 12.0D;
            }
        }


        switch (archetype) {

            case PYROMANCER -> {

                if (spell.spell()
                        == ModSpells.FIREBALL) {

                    score += 8.0D;
                }

                if (spell.spell()
                        == ModSpells.FIRE_STORM) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.BLAZE_STORM) {

                    score += 8.0D;
                }

                if (spell.spell()
                        == ModSpells.DRAGONS_BREATH) {

                    score += 6.0D;
                }

                if (spell.spell()
                        == ModSpells.GEYSER_STEP) {

                    score += 5.0D;
                }
            }

            case STORM_WARDEN -> {

                if (spell.spell()
                        == ModSpells.EARTH_SPIKE_LINE) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.WIND_TORNADO) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.LOCALIZED_STORM_PUSH) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.FROST_REFLEX) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.TEMPESTS_CALL) {

                    score += 8.0D;
                }
            }

            case SHADOW_SUMMONER -> {

                if (spell.spell()
                        == ModSpells.ELDRITCH_TETHER) {

                    score += 12.0D;
                }

                if (spell.spell()
                        == ModSpells.ABYSSAL_SHARDS) {

                    score += 8.0D;
                }

                if (spell.spell()
                        == ModSpells.SHADOW_STEP
                        && canUseShadowStep()) {

                    score += 12.0D;
                }

                if (spell.spell()
                        == ModSpells.RAISE_UNDEAD
                        && countNearbySummons() == 0) {

                    score += 18.0D;
                }

                if (spell.spell()
                        == ModSpells.PURGE_BOLT
                        && playerBuffed) {

                    score += 20.0D;
                }
            }

            case SUPPORT -> {

                if (spell.spell()
                        == ModSpells.LUMINARY_EMPOWER) {

                    score += 40.0D;
                }

                if (spell.spell()
                        == ModSpells.ICE_BLOCK
                        && selfHealth < 0.70D) {

                    score += 10.0D;
                }

                if (spell.spell()
                        == ModSpells.LOCALIZED_STORM_PUSH
                        && distance < 7.0D) {

                    score += 20.0D;
                }
            }

            default -> {
            }
        }


        if (comboTicks > 0 && lastSpell != null) {

            Spell last =
                    ModRegistries.SPELL.get(lastSpell);


            if (last == ModSpells.LOCALIZED_STORM_PUSH
                    || last == ModSpells.STORM_PUSH) {

                if (spell.spell()
                        == ModSpells.FIREBALL
                        || spell.spell()
                        == ModSpells.LIGHTNING_STRIKE
                        || spell.spell()
                        == ModSpells.ABYSSAL_SHARDS) {

                    score += 26.0D;
                }
            }


            if (last == ModSpells.FIREBALL) {

                if (spell.spell()
                        == ModSpells.FLAME_BURST
                        || spell.spell()
                        == ModSpells.BLAZE_STORM
                        || spell.spell()
                        == ModSpells.FIRE_STORM) {

                    score += 22.0D;
                }
            }


            if (last == ModSpells.ICE_BLOCK
                    || last == ModSpells.EARTH_SPIKE_LINE) {

                if (spell.spell()
                        == ModSpells.LIGHTNING_STRIKE
                        || spell.spell()
                        == ModSpells.FIREBALL) {

                    score += 22.0D;
                }
            }


            if (last == ModSpells.ELDRITCH_TETHER) {

                if (spell.spell()
                        == ModSpells.ABYSSAL_SHARDS
                        || spell.spell()
                        == ModSpells.SHULKER_BULLET) {

                    score += 28.0D;
                }
            }


            if (last == ModSpells.ELDRITCH_ILLUSION
                    && spell.spell()
                    == ModSpells.SHADOW_STEP
                    && canUseShadowStep()) {

                score += 28.0D;
            }


            if (last == ModSpells.RAISE_UNDEAD
                    && spell.spell()
                    == ModSpells.LUMINARY_EMPOWER) {

                score += 22.0D;
            }

            if (last == ModSpells.LOCALIZED_STORM_PUSH
                    && spell.spell()
                    == ModSpells.DRAGONS_BREATH) {

                score += 14.0D;
            }
        }


        if (nearbyCaster
                && isHighImpactSpell(spell)) {

            score *= 0.55D;
        }

        if (countNearbyLuminaries() >= 3
                && isHighImpactSpell(spell)) {

            score *= 0.80D;
        }


        if (hardControlCooldown > 0
                && isHardControlSpell(spell)) {

            score = 0.0D;
        }

        return score;
    }

    private boolean hasImportantBeneficialEffects(
            PlayerEntity player
    ) {

        return player.hasStatusEffect(
                StatusEffects.REGENERATION
        )
                || player.hasStatusEffect(
                StatusEffects.RESISTANCE
        )
                || player.hasStatusEffect(
                StatusEffects.ABSORPTION
        )
                || player.hasStatusEffect(
                StatusEffects.STRENGTH
        )
                || player.hasStatusEffect(
                StatusEffects.SPEED
        )
                || player.hasStatusEffect(
                StatusEffects.FIRE_RESISTANCE
        )
                || player.hasStatusEffect(
                StatusEffects.HASTE
        )
                || player.hasStatusEffect(
                StatusEffects.JUMP_BOOST
        )
                || player.hasStatusEffect(
                StatusEffects.WATER_BREATHING
        )
                || player.hasStatusEffect(
                StatusEffects.NIGHT_VISION
        )
                || player.hasStatusEffect(
                StatusEffects.INVISIBILITY
        )
                || player.hasStatusEffect(
                StatusEffects.SLOW_FALLING
        )
                || player.hasStatusEffect(
                StatusEffects.CONDUIT_POWER
        )
                || player.hasStatusEffect(
                StatusEffects.DOLPHINS_GRACE
        )
                || player.hasStatusEffect(
                StatusEffects.LUCK
        );
    }

    private boolean isDamageSpell(
            Spell.InstancedSpell spell
    ) {

        return spell.spell()
                != ModSpells.LUMINARY_EMPOWER

                && spell.spell()
                != ModSpells.TRIGGERED_POTION

                && spell.spell()
                != ModSpells.POTION

                && spell.spell()
                != ModSpells.ICE_BLOCK

                && spell.spell()
                != ModSpells.MIST_VEIL;
    }

    private boolean isHardControlSpell(
            Spell.InstancedSpell spell
    ) {

        return spell.spell()
                == ModSpells.STORM_PUSH

                || spell.spell()
                == ModSpells.LOCALIZED_STORM_PUSH

                || spell.spell()
                == ModSpells.WIND_TORNADO

                || spell.spell()
                == ModSpells.EARTH_SPIKE_LINE

                || spell.spell()
                == ModSpells.ICE_BLOCK;
    }

    private boolean isHighImpactSpell(
            Spell.InstancedSpell spell
    ) {

        return spell.spell()
                == ModSpells.FIRE_STORM

                || spell.spell()
                == ModSpells.BLAZE_STORM

                || spell.spell()
                == ModSpells.DRAGON_FIREBALL

                || spell.spell()
                == ModSpells.TEMPESTS_CALL

                || spell.spell()
                == ModSpells.CREEPER_WALL_EXPLOSIVE_TRAP

                || spell.spell()
                == ModSpells.RAISE_UNDEAD;
    }

    private int countNearbySummons() {

        Box box =
                mob.getBoundingBox()
                        .expand(16.0D);

        return mob.getWorld()
                .getOtherEntities(
                        mob,
                        box,
                        entity ->
                                entity instanceof LivingEntity living
                                        && !(living instanceof PlayerEntity)
                                        && !(living instanceof EldritchLuminaryEntity)
                )
                .size();
    }


    public void onSpellCast(
            Spell.InstancedSpell spell
    ) {

        if (spell == null
                || spell.spell() == null
                || spell.spell() == ModSpells.EMPTY) {

            return;
        }

        previousSpell =
                lastSpell;

        lastSpell =
                mob.getSpellId(spell);

        comboTicks = 80;
        ticksSinceSpell = 0;

        if (isHardControlSpell(spell)) {
            hardControlCooldown = 38;
        }

        if (isTargetRestricted()
                && isDamageSpell(spell)) {

            trappedPressureCooldown = 55;
        }
    }

    private Spell.InstancedSpell emptySpell() {

        return new Spell.InstancedSpell(
                ModSpells.EMPTY,
                GrabBag.empty()
        );
    }

    public Identifier getLastSpell() {
        return lastSpell;
    }

    public Identifier getPreviousSpell() {
        return previousSpell;
    }
}
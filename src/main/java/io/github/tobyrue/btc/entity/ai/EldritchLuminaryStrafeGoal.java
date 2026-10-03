package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.ai.brain.EldritchLuminaryBrain;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class EldritchLuminaryStrafeGoal extends Goal {

    private final EldritchLuminaryEntity mob;
    private final double speed;

    private int direction;
    private int directionTicks;
    private int repathCooldown;

    public EldritchLuminaryStrafeGoal(
            EldritchLuminaryEntity mob,
            double speed,
            float ignoredMaximumRange
    ) {
        this.mob = mob;
        this.speed = speed;

        this.setControls(
                EnumSet.of(Control.MOVE)
        );
    }

    private EldritchLuminaryBrain getBrain() {
        return mob.getLuminaryBrain();
    }

    @Override
    public boolean canStart() {
        return hasMovementTarget();
    }

    @Override
    public boolean shouldContinue() {
        return hasMovementTarget();
    }

    @Override
    public void start() {
        direction =
                mob.getRandom().nextBoolean()
                        ? 1
                        : -1;

        directionTicks =
                45 + mob.getRandom().nextInt(45);

        repathCooldown = 0;
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {

        EldritchLuminaryBrain brain = getBrain();

        if (brain.shouldSeekSupport()) {
            EldritchLuminaryEntity support =
                    brain.findNearestSupport();

            if (support != null) {
                moveTowardSupport(support);
                return;
            }
        }

        LivingEntity target =
                mob.getTarget();

        if (brain.shouldRetreat()
                && target != null) {
            retreatFrom(target);
            return;
        }

        if (target == null) {
            mob.getNavigation().stop();
            return;
        }

        updateDirection();

        if (repathCooldown > 0) {
            repathCooldown--;
            return;
        }

        double minDistance =
                brain.getDesiredMinDistance();

        double maxDistance =
                brain.getDesiredMaxDistance();

        double distance =
                mob.distanceTo(target);

        double desiredRadius =
                (minDistance + maxDistance) * 0.5D;

        if (distance < minDistance) {
            moveAwayWithOrbit(
                    target,
                    minDistance + 3.0D
            );
        } else if (distance > maxDistance) {
            moveTowardWithOrbit(
                    target,
                    desiredRadius
            );
        } else {
            orbitTarget(
                    target,
                    desiredRadius
            );
        }

        repathCooldown =
                7 + mob.getRandom().nextInt(6);
    }

    private boolean hasMovementTarget() {
        EldritchLuminaryBrain brain = getBrain();

        return mob.getTarget() != null
                || brain.shouldActWithoutPlayer()
                || brain.shouldSeekSupport();
    }

    private void updateDirection() {
        if (directionTicks > 0) {
            directionTicks--;
            return;
        }

        direction *= -1;

        directionTicks =
                40 + mob.getRandom().nextInt(61);
    }

    private void orbitTarget(
            LivingEntity target,
            double radius
    ) {
        Vec3d radial =
                horizontal(
                        mob.getPos()
                                .subtract(target.getPos())
                );

        if (radial.lengthSquared() < 0.0001D) {
            radial =
                    new Vec3d(
                            direction,
                            0.0D,
                            0.0D
                    );
        } else {
            radial = radial.normalize();
        }

        Vec3d tangent =
                new Vec3d(
                        -radial.z * direction,
                        0.0D,
                        radial.x * direction
                ).normalize();

        double sideAmount =
                2.5D
                        + Math.sin(
                        mob.age * 0.11D
                ) * 1.5D;

        Vec3d candidate =
                target.getPos()
                        .add(
                                radial.multiply(radius)
                        )
                        .add(
                                tangent.multiply(sideAmount)
                        );

        moveToBestCandidate(
                target,
                candidate,
                radius,
                false
        );
    }

    private void moveAwayWithOrbit(
            LivingEntity target,
            double radius
    ) {
        Vec3d radial =
                horizontal(
                        mob.getPos()
                                .subtract(target.getPos())
                );

        if (radial.lengthSquared() < 0.0001D) {
            radial =
                    new Vec3d(
                            direction,
                            0.0D,
                            0.0D
                    );
        } else {
            radial = radial.normalize();
        }

        Vec3d tangent =
                new Vec3d(
                        -radial.z * direction,
                        0.0D,
                        radial.x * direction
                ).normalize();

        Vec3d candidate =
                target.getPos()
                        .add(
                                radial.multiply(radius)
                        )
                        .add(
                                tangent.multiply(2.0D)
                        );

        moveToBestCandidate(
                target,
                candidate,
                radius,
                true
        );
    }

    private void moveTowardWithOrbit(
            LivingEntity target,
            double radius
    ) {
        Vec3d radial =
                horizontal(
                        mob.getPos()
                                .subtract(target.getPos())
                );

        if (radial.lengthSquared() < 0.0001D) {
            radial =
                    new Vec3d(
                            direction,
                            0.0D,
                            0.0D
                    );
        } else {
            radial = radial.normalize();
        }

        Vec3d tangent =
                new Vec3d(
                        -radial.z * direction,
                        0.0D,
                        radial.x * direction
                ).normalize();

        Vec3d candidate =
                target.getPos()
                        .add(
                                radial.multiply(radius)
                        )
                        .add(
                                tangent.multiply(2.5D)
                        );

        moveToBestCandidate(
                target,
                candidate,
                radius,
                false
        );
    }

    private void retreatFrom(
            LivingEntity target
    ) {
        EldritchLuminaryBrain brain = getBrain();

        Vec3d away =
                horizontal(
                        mob.getPos()
                                .subtract(target.getPos())
                );

        if (away.lengthSquared() < 0.0001D) {
            away =
                    new Vec3d(
                            direction,
                            0.0D,
                            0.0D
                    );
        } else {
            away = away.normalize();
        }

        double retreatDistance =
                brain.isCombatBreaking()
                        ? 22.0D
                        : 20.0D;

        EldritchLuminaryEntity support =
                brain.findNearestSupport();

        if (support != null
                && brain.shouldSeekSupport()) {
            moveTowardSupport(support);
            return;
        }

        Vec3d side =
                new Vec3d(
                        -away.z,
                        0.0D,
                        away.x
                ).normalize();

        Vec3d primary =
                target.getPos()
                        .add(
                                away.multiply(
                                        retreatDistance
                                )
                        );

        Vec3d alternate =
                primary.add(
                        side.multiply(
                                direction * 5.0D
                        )
                );

        if (!tryNavigate(alternate)) {
            tryNavigate(primary);
        }
    }

    private void moveTowardSupport(
            EldritchLuminaryEntity support
    ) {
        double distance =
                mob.distanceTo(support);

        if (distance > 8.0D) {
            mob.getNavigation()
                    .startMovingTo(
                            support,
                            speed * 1.15D
                    );
            return;
        }

        Vec3d away =
                horizontal(
                        mob.getPos()
                                .subtract(
                                        support.getPos()
                                )
                );

        if (away.lengthSquared() < 0.0001D) {
            away =
                    new Vec3d(
                            1.0D,
                            0.0D,
                            0.0D
                    );
        } else {
            away = away.normalize();
        }

        Vec3d desired =
                support.getPos()
                        .add(
                                away.multiply(5.5D)
                        );

        tryNavigate(desired);
    }

    private void moveToBestCandidate(
            LivingEntity target,
            Vec3d preferred,
            double radius,
            boolean retreat
    ) {
        Vec3d radial =
                horizontal(
                        preferred.subtract(
                                target.getPos()
                        )
                );

        if (radial.lengthSquared() < 0.0001D) {
            radial =
                    new Vec3d(
                            1.0D,
                            0.0D,
                            0.0D
                    );
        } else {
            radial = radial.normalize();
        }

        Vec3d tangent =
                new Vec3d(
                        -radial.z,
                        0.0D,
                        radial.x
                ).normalize();

        Vec3d[] candidates =
                new Vec3d[]{
                        preferred,

                        preferred.add(
                                tangent.multiply(3.0D)
                        ),

                        preferred.subtract(
                                tangent.multiply(3.0D)
                        ),

                        target.getPos()
                                .add(
                                        radial.multiply(
                                                radius + 3.0D
                                        )
                                )
                                .add(
                                tangent.multiply(
                                        direction * 4.0D
                                )
                        ),

                        target.getPos()
                                .add(
                                        radial.multiply(
                                                Math.max(
                                                        6.0D,
                                                        radius - 3.0D
                                                )
                                        )
                                )
                                .add(
                                tangent.multiply(
                                        direction * 4.0D
                                )
                        )
                };

        Vec3d best = null;
        double bestScore = -Double.MAX_VALUE;

        for (Vec3d candidate : candidates) {

            if (!isGoodCandidate(candidate)) {
                continue;
            }

            double candidateDistance =
                    Math.sqrt(
                            candidate.squaredDistanceTo(
                                    target.getPos()
                            )
                    );

            double score =
                    -Math.abs(
                            candidateDistance - radius
                    ) * 4.0D;

            if (mob.getVisibilityCache()
                    .canSee(target)) {
                score += 8.0D;
            }

            score -= wallPenalty(candidate);

            if (retreat
                    && candidateDistance > radius) {
                score += 8.0D;
            }

            if (score > bestScore) {
                bestScore = score;
                best = candidate;
            }
        }

        if (best != null
                && tryNavigate(best)) {
            return;
        }

        mob.getNavigation()
                .startMovingTo(
                        target,
                        speed
                );
    }

    private boolean tryNavigate(
            Vec3d position
    ) {
        if (!isGoodCandidate(position)) {
            return false;
        }

        EldritchLuminaryBrain brain = getBrain();

        double movementSpeed =
                brain.isCombatBreaking()
                        ? speed * 1.20D
                        : speed;

        mob.getNavigation()
                .startMovingTo(
                        position.x,
                        position.y,
                        position.z,
                        movementSpeed
                );

        return true;
    }

    private boolean isGoodCandidate(
            Vec3d candidate
    ) {
        BlockPos foot =
                BlockPos.ofFloored(candidate);

        if (!mob.getWorld()
                .getBlockState(foot)
                .getCollisionShape(
                        mob.getWorld(),
                        foot
                )
                .isEmpty()) {
            return false;
        }

        if (!mob.getWorld()
                .getBlockState(foot.up())
                .getCollisionShape(
                        mob.getWorld(),
                        foot.up()
                )
                .isEmpty()) {
            return false;
        }

        if (!mob.getWorld()
                .getBlockState(foot.down())
                .isSolidBlock(
                        mob.getWorld(),
                        foot.down()
                )) {
            return false;
        }

        return mob.getWorld()
                .getWorldBorder()
                .contains(candidate);
    }

    private double wallPenalty(
            Vec3d candidate
    ) {
        BlockPos pos =
                BlockPos.ofFloored(candidate);

        double penalty = 0.0D;

        for (Direction direction :
                Direction.Type.HORIZONTAL) {

            BlockPos adjacent =
                    pos.offset(direction);

            if (!mob.getWorld()
                    .getBlockState(adjacent)
                    .getCollisionShape(
                            mob.getWorld(),
                            adjacent
                    )
                    .isEmpty()) {
                penalty += 3.0D;
            }
        }

        return penalty;
    }

    private Vec3d horizontal(
            Vec3d value
    ) {
        return new Vec3d(
                value.x,
                0.0D,
                value.z
        );
    }
}
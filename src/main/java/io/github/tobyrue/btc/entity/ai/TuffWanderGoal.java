package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import io.github.tobyrue.btc.entity.custom.TuffGolemEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.GapContent;
import java.util.EnumSet;

public class TuffWanderGoal extends Goal {
    public static final int DEFAULT_CHANCE = 120;
    protected final TuffGolemEntity mob;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    private final boolean canDespawn;
    private int wanderTime = 0;

    public TuffWanderGoal(TuffGolemEntity mob, double speed) {
        this(mob, speed, DEFAULT_CHANCE);
    }

    public TuffWanderGoal(TuffGolemEntity mob, double speed, int chance) {
        this(mob, speed, chance, true);
    }

    public TuffWanderGoal(TuffGolemEntity mob, double speed, int chance, boolean canDespawn) {
        this.mob = mob;
        this.speed = speed;
        this.chance = chance;
        this.canDespawn = canDespawn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.mob.getCanMove() && mob.isSleeping()) {
            return false;
        }
        if (this.mob.hasControllingPassenger()) {
            return false;
        } else {
            if (!this.ignoringChance) {
                if (this.canDespawn && this.mob.getDespawnCounter() >= 100) {
                    return false;
                }

                if (this.mob.getRandom().nextInt(toGoalTicks(this.chance)) != 0) {
                    return false;
                }
            }

            Vec3d vec3d = this.getWanderTarget();
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                this.ignoringChance = false;
                return true;
            }
        }
    }

    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d home = mob.getHomePosition();
        if (home != null) {
            Vec3d candidate = NoPenaltyTargeting.find(mob, 12, 7);
            if (candidate != null && candidate.isInRange(home, 10)) {
                return candidate;
            }

            // Try returning to home if far away
            if (!mob.getPos().isInRange(home, 10)) {
                mob.ticksAwayFromHome++;
                if (mob.ticksAwayFromHome > 100) {
                    mob.ticksAwayFromHome = 0;
                    return home;
                }
            } else {
                mob.ticksAwayFromHome = 0;
            }
            return null;
        }

        return NoPenaltyTargeting.find(mob, 12, 7);
    }

    @Override
    public boolean shouldContinue() {
        // Stop movement immediately if oxidized while already moving
        if (!this.mob.getCanMove() && mob.isSleeping()) {
            return false;
        }
        return !this.mob.getNavigation().isIdle() && !this.mob.hasControllingPassenger();
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

    public void ignoreChanceOnce() {
        this.ignoringChance = true;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}

package io.github.tobyrue.btc.entity.ai;

import java.util.EnumSet;

import io.github.tobyrue.btc.entity.custom.CopperGolemEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class CopperGolemWander extends Goal {
    public static final int DEFAULT_CHANCE = 120;
    protected final CopperGolemEntity mob;  // Change to CopperGolemEntity
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected final double speed;
    protected int chance;
    protected boolean ignoringChance;
    private final boolean canDespawn;

    public CopperGolemWander(CopperGolemEntity mob, double speed) {
        this(mob, speed, DEFAULT_CHANCE);
    }

    public CopperGolemWander(CopperGolemEntity mob, double speed, int chance) {
        this(mob, speed, chance, true);
    }

    public CopperGolemWander(CopperGolemEntity mob, double speed, int chance, boolean canDespawn) {
        this.mob = mob;
        this.speed = speed;
        this.chance = chance;
        this.canDespawn = canDespawn;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        // Only allow CopperGolemEntity to wander
        if (!(this.mob instanceof CopperGolemEntity)) {
            return false;
        }

        // Check if the Copper Golem is fully oxidized (OXIDIZED state)
        if (this.mob.getOxidation() == CopperGolemEntity.Oxidation.OXIDIZED) {
            return false; // Stop moving if fully oxidized
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
        return NoPenaltyTargeting.find(this.mob, 10, 7);
    }

    @Override
    public boolean shouldContinue() {
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

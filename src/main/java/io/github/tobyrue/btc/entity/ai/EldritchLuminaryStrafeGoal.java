package io.github.tobyrue.btc.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class EldritchLuminaryStrafeGoal extends Goal {
    private final MobEntity actor;
    private final double speed;
    private final float range;
    private final double squaredRange;
    private int combatTicks = -1;
    private int targetSeeingTicker = 0;
    private boolean movingToLeft = false;
    private boolean backward = false;

    public EldritchLuminaryStrafeGoal(MobEntity actor, double speed, float range) {
        this.actor = actor;
        this.speed = speed;
        this.range = range;
        this.squaredRange = range * range; // Precompute squared range
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.actor.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void stop() {
        this.combatTicks = -1;
        this.targetSeeingTicker = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.actor.getTarget();
        if (target == null) return;

        double squaredDistance = this.actor.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
        boolean canSee = this.actor.getVisibilityCache().canSee(target);
        boolean wasSeeing = this.targetSeeingTicker > 0;

        if (canSee != wasSeeing) {
            this.targetSeeingTicker = 0;
        }

        if (canSee) {
            ++this.targetSeeingTicker;
        } else {
            --this.targetSeeingTicker;
        }

        if (squaredDistance <= this.squaredRange && this.targetSeeingTicker >= 20) {
            this.actor.getNavigation().stop();
            ++this.combatTicks;
        } else {
            this.actor.getNavigation().startMovingTo(target, this.speed);
            this.combatTicks = -1;
        }

        if (this.combatTicks >= 20) {
            if (this.actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;
            }

            if (this.actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;
            }

            this.combatTicks = 0;
        }

        if (this.combatTicks > -1) {
            if (squaredDistance > this.squaredRange * 0.75F) {
                this.backward = false;
            } else if (squaredDistance < this.squaredRange * 0.25F) {
                this.backward = true;
            }

            this.actor.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
            this.actor.lookAtEntity(target, 30.0F, 30.0F);
        } else {
            this.actor.getLookControl().lookAt(target, 30.0F, 30.0F);
        }
    }
}

package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Hand;

import java.util.EnumSet;
import java.util.List;

public class ControlMountGoal extends Goal {
    private final HuggerEntity hugger;
    private final List<Class<? extends LivingEntity>> rideableClasses;
    private int attackCooldown;

    public ControlMountGoal(HuggerEntity hugger, List<Class<? extends LivingEntity>> rideableClasses) {
        this.hugger = hugger;
        this.rideableClasses = rideableClasses;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.hugger.hasVehicle() || !(this.hugger.getVehicle() instanceof MobEntity host)) {
            return false;
        }
        LivingEntity target = this.hugger.getTarget();
        if (!isValidControlledTarget(target, host)) {
            if (target != null) {
                this.hugger.setTarget(null);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldContinue() {
        if (!this.hugger.hasVehicle() || !(this.hugger.getVehicle() instanceof MobEntity host)) {
            return false;
        }
        return isValidControlledTarget(this.hugger.getTarget(), host);
    }

    private boolean isValidControlledTarget(LivingEntity target, MobEntity host) {
        if (target == null || !target.isAlive() || target == host || target == this.hugger) {
            return false;
        }
        if (target instanceof HuggerEntity) {
            return false;
        }
        for (Class<? extends LivingEntity> clazz : this.rideableClasses) {
            if (clazz.isInstance(target)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void start() {
        this.attackCooldown = 0;
    }

    @Override
    public void tick() {
        if (!(this.hugger.getVehicle() instanceof MobEntity host)) {
            return;
        }

        LivingEntity target = this.hugger.getTarget();
        if (!isValidControlledTarget(target, host)) {
            this.hugger.setTarget(null);
            return;
        }

        if (this.attackCooldown > 0) {
            this.attackCooldown--;
        }

        this.hugger.getLookControl().lookAt(target, 30.0F, 30.0F);
        host.getLookControl().lookAt(target, 30.0F, 30.0F);
        host.setBodyYaw(host.getHeadYaw());

        host.getMoveControl().moveTo(target.getX(), target.getY(), target.getZ(), 1.75D);

        double reachSq = (host.getWidth() * 2.0F) * (host.getWidth() * 2.0F) + target.getWidth();
        if (host.squaredDistanceTo(target) <= Math.max(reachSq, 3.5D) && this.attackCooldown <= 0 && host.getVisibilityCache().canSee(target)) {
            host.swingHand(Hand.MAIN_HAND);
            host.tryAttack(target);
            this.attackCooldown = 20;
        }
    }

    @Override
    public void stop() {
        if (this.hugger.getVehicle() instanceof MobEntity host) {
            host.getNavigation().stop();
        }
    }
}
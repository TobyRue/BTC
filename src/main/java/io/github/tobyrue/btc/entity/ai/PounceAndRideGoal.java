package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.HuggerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.List;

public class PounceAndRideGoal extends Goal {
    private final HuggerEntity hugger;
    private LivingEntity target;
    private final float velocity;
    private final List<Class<? extends LivingEntity>> entities;
    private int jumpCooldown;
    private int attackCooldown;
    private int retreatTicks;
    private int windUpTicks;

    public PounceAndRideGoal(HuggerEntity mob, float velocity, List<Class<? extends LivingEntity>> entities) {
        this.hugger = mob;
        this.velocity = velocity;
        this.entities = entities;
        this.setControls(EnumSet.of(Control.JUMP, Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.hugger.hasControllingPassenger() || this.hugger.hasVehicle()) return false;
        this.target = this.hugger.getTarget();
        if (this.target == null || !this.target.isAlive()) return false;

        double distanceSq = this.hugger.squaredDistanceTo(this.target);
        return distanceSq >= 4.0 && distanceSq <= 324.0;
    }

    @Override
    public boolean shouldContinue() {
        this.target = this.hugger.getTarget();
        return this.target != null && this.target.isAlive() && !this.hugger.hasVehicle() && this.hugger.squaredDistanceTo(this.target) <= 484.0;
    }

    @Override
    public void start() {
        this.jumpCooldown = 0;
        this.attackCooldown = 0;
        this.retreatTicks = 0;
        this.windUpTicks = 0;
    }

    @Override
    public void stop() {
        this.target = null;
        this.hugger.getNavigation().stop();
        this.hugger.setAttacking(false);
        this.hugger.setWindingUp(false);
    }

    @Override
    public void tick() {
        if (this.target == null || this.hugger.hasVehicle()) return;

        if (this.windUpTicks > 0) {
            this.windUpTicks--;
            this.hugger.getNavigation().stop();
            this.hugger.getLookControl().lookAt(this.target, 30.0F, 30.0F);
            if (this.windUpTicks == 2) {
                this.hugger.setWindingUp(true);
            }

            if (this.windUpTicks == 0) {
                this.hugger.setWindingUp(false);
                this.executePounce();
            }
            return;
        }

        this.hugger.getLookControl().lookAt(this.target, 30.0F, 30.0F);

        if (this.jumpCooldown > 0) this.jumpCooldown--;
        if (this.attackCooldown > 0) this.attackCooldown--;
        if (this.retreatTicks > 0) this.retreatTicks--;

        double distanceSq = this.hugger.squaredDistanceTo(this.target);

        if (this.hugger.isOnGround()) {
            if (this.retreatTicks > 0) {
                Vec3d awayPos = this.hugger.getPos().add(this.hugger.getPos().subtract(this.target.getPos()).normalize().multiply(3.0));
                this.hugger.getNavigation().startMovingTo(awayPos.x, awayPos.y, awayPos.z, 1.2D);
                return;
            }

            if (distanceSq < 4.0) {
                if (this.matchesTargetClass()) {
                    if (this.hugger.startRiding(this.target)) {
                        this.hugger.setTarget(null);
                        this.hugger.swingHand(Hand.MAIN_HAND);
                    }
                } else {
                    Vec3d awayPos = this.hugger.getPos().add(this.hugger.getPos().subtract(this.target.getPos()).normalize().multiply(2.5));
                    this.hugger.getNavigation().startMovingTo(awayPos.x, awayPos.y, awayPos.z, 1.3D);
                }
            } else if (distanceSq >= 4.0 && distanceSq <= 16.0 && this.jumpCooldown <= 0) {
                this.windUpTicks = 5;
                this.jumpCooldown = 0;
            } else {
                this.hugger.getNavigation().startMovingTo(this.target, 1.5D);
            }
        } else {
            if (distanceSq <= 4.0 && this.attackCooldown <= 0 && this.hugger.getVisibilityCache().canSee(this.target)) {
                if (this.matchesTargetClass()) {
                    if (this.hugger.startRiding(this.target)) {
                        this.hugger.setTarget(null);
                        this.hugger.swingHand(Hand.MAIN_HAND);
                    }
                } else if (this.hugger.tryAttack(this.target)) {
                    this.hugger.swingHand(Hand.MAIN_HAND);
                    this.attackCooldown = 12;
                    this.retreatTicks = 15;
                }
            }
        }
    }

    private boolean matchesTargetClass() {
        if (this.target == null) return false;
        for (Class<? extends LivingEntity> clazz : this.entities) {
            if (clazz.isInstance(this.target)) {
                return true;
            }
        }
        return false;
    }

    private void executePounce() {
        double distance = Math.sqrt(this.hugger.squaredDistanceTo(this.target));
        Vec3d currentVelocity = this.hugger.getVelocity();
        Vec3d targetDir = new Vec3d(this.target.getX() - this.hugger.getX(), 0.0, this.target.getZ() - this.hugger.getZ());

        if (targetDir.lengthSquared() > 1.0E-7) {
            targetDir = targetDir.normalize();
        }

        double leapStrength = MathHelper.clamp(distance * 0.3D, 0.5D, 1.3D);
        double vx = targetDir.x * leapStrength + currentVelocity.x * 0.45D;
        double vz = targetDir.z * leapStrength + currentVelocity.z * 0.45D;

        this.hugger.setVelocity(vx, (double) this.velocity, vz);
        this.hugger.setAttacking(true);
    }
}
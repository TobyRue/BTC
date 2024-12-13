package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.EldritchLuminariesEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.Hand;

public class EldritchLuminariesCastGoal extends MeleeAttackGoal {
    private final EldritchLuminariesEntity entity;
    private int attackDelay = 25;
    private int ticksUntilNextAttack = 20;
    private boolean shouldCountTillNextAttack = false;

    public EldritchLuminariesCastGoal(PathAwareEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        entity = (((EldritchLuminariesEntity) mob));
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 25;
        ticksUntilNextAttack = 20;
    }

    @Override
    protected void attack(LivingEntity eEnemy) {
        if (isEnemyWithinAttackingDistance(eEnemy)) {
            shouldCountTillNextAttack = true;

            if (isTimeToStartAttackAnimation()) {
                entity.setAttacking(true);
            }

            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(eEnemy.getX(), eEnemy.getY(), eEnemy.getZ());
            }
        } else {
            resetAttackCooldown();
            entity.setAttacking(false);
            entity.attackAnimationTimeout = 0;
        }
    }

    private boolean isEnemyWithinAttackingDistance(LivingEntity eEnemy) {
        return this.entity.distanceTo(eEnemy) <= 2f;
    }

    protected  void resetAttackCooldown() {this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);}

    protected boolean isTimeToStartAttackAnimation() {return this.ticksUntilNextAttack <= attackDelay;}

    protected boolean isTimeToAttack() {return this.ticksUntilNextAttack <= 0;}

    protected void performAttack(LivingEntity eEnemy) {
        this.resetAttackCooldown();
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(eEnemy);
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    public void stop() {
        entity.setAttacking(false);
        super.stop();
    }
}

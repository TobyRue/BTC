package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.enums.AttackType;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryWindBurstGoal extends Goal {
    private final EldritchLuminaryEntity luminary;
    private int ticksUntilNextAttack = 20;
    private int attackDelay = 20;
    private boolean shouldCountTillNextAttack = false;

    public EldritchLuminaryWindBurstGoal(PathAwareEntity mob) {
        luminary = ((EldritchLuminaryEntity) mob);
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 20;
        ticksUntilNextAttack = 20;
    }


    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }



    private boolean isEnemyWithinAttackDistance(LivingEntity eEnemy) {
        return this.luminary.distanceTo(eEnemy) >= 1f && this.luminary.distanceTo(eEnemy) <= 6f;
    }
    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity eEnemy = this.luminary.getTarget();
        if (isEnemyWithinAttackDistance(eEnemy)) {
            shouldCountTillNextAttack = true;
//            if (eEnemy == null) {
//                    this.ticksUntilNextAttack = 40;
//                    return;
//            }
            if(/*isTimeToStartAttackAnimation() && */luminary.getAttack() == AttackType.NONE) {
                luminary.setAttack(AttackType.WIND_CHARGE);
            }
            double maxDistance = 64.0;
            if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {

                if (isTimeToAttack() && luminary.getAttack() == AttackType.WIND_CHARGE) {
                    World world = this.luminary.getWorld();

                    double speed = 1.5;
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, world);
                    windChargeEntity.setVelocity(velocity);
                    windChargeEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );

                    world.spawnEntity(windChargeEntity);
                    this.ticksUntilNextAttack = 20;
                }
            }
        } else {
            ticksUntilNextAttack = 20;
            shouldCountTillNextAttack = false;
            if (luminary.getAttack() == AttackType.WIND_CHARGE) {
                luminary.setAttack(AttackType.NONE);
            }
            luminary.attackAnimationTimeout = 0;
        }
        if(shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }
    @Override
    public void stop() {
        luminary.setAttack(AttackType.NONE);
        super.stop();
    }
}

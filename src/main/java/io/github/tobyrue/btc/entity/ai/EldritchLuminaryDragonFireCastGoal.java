package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.AttackType;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryDragonFireCastGoal extends Goal {
    private final EldritchLuminaryEntity luminary;
    private int ticksUntilNextAttack = 61;
    private int attackDelay = 61;
    private boolean shouldCountTillNextAttack = false;

    public EldritchLuminaryDragonFireCastGoal(PathAwareEntity mob) {
        luminary = ((EldritchLuminaryEntity) mob);
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 61;
        ticksUntilNextAttack = 61;
    }


    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);
    }

    protected boolean isTimeToStartAttackAnimation() {
        return this.ticksUntilNextAttack <= attackDelay;
    }

    protected boolean isTimeToAttack() {
        return luminary.getProgress() == 60;
    }



    private boolean isEnemyWithinAttackDistance(LivingEntity eEnemy) {
        return this.luminary.distanceTo(eEnemy) >= 6f && this.luminary.distanceTo(eEnemy) <= 16f;
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
//                this.ticksUntilNextAttack = 40;
//                return;
//            }
            if(/*isTimeToStartAttackAnimation() && */luminary.getAttack() == AttackType.NONE) {
                luminary.setAttack(AttackType.DRAGON_FIRE_BALL);
            }

            double maxDistance = 64.0;
            if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {
                if (isTimeToAttack() && luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
                    World world = this.luminary.getWorld();
                    System.out.println(luminary.getAttack() + " Fire with client " + world.isClient);
//                    double targetYaw = MathHelper.atan2(targetPos.z, targetPos.x) * (180.0 / Math.PI) - 90.0;
//                    double yawDifference = MathHelper.wrapDegrees(this.luminary.getYaw() - (float) targetYaw);
//
//                    if (Math.abs(yawDifference) > 10.0F) {
//                        this.luminary.getLookControl().lookAt(eEnemy, 30.0F, 30.0F);
//                        this.luminary.setYaw((float) targetYaw);
//                        return;
//                    }

                    double speed = 1.5;
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // More precise aim towards the center of the target
                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d vec3d2 = new Vec3d(dx, dy, dz);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, luminary, velocity);
                    dragonFireballEntity.setVelocity(velocity);
                    dragonFireballEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );

                    world.spawnEntity(dragonFireballEntity);
                    this.ticksUntilNextAttack = 61;
                }
            }
        } else {
            resetAttackCooldown();
            shouldCountTillNextAttack = false;
            if (luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
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

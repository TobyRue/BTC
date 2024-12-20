package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryWindBurstGoal extends Goal {
    private final EldritchLuminaryEntity luminary;
    private int ticksUntilNextAttack = 45; // Attack delay in ticks (1 second = 20 ticks)
    private int attackDelay = 45;

    public EldritchLuminaryWindBurstGoal(EldritchLuminaryEntity luminary) {
        this.luminary = luminary;
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        attackDelay = 45;
        ticksUntilNextAttack = 45;
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.getTickCount(attackDelay * 2);
    }
    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    @Override
    public void stop() {
        this.luminary.setAttacking(false);
        super.stop();
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity eEnemy) {
        return this.luminary.distanceTo(eEnemy) >= 4f && this.luminary.distanceTo(eEnemy) <= 16f; // TODO
    }
    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity eEnemy = this.luminary.getTarget();
        if (isEnemyWithinAttackDistance(eEnemy)) {
            luminary.setAttacking(true);
            if (eEnemy == null) {
                this.ticksUntilNextAttack = 45;
                return;
            }
            double maxDistance = 64.0;
            if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {
                if (isTimeToAttack()) {
                    World world = this.luminary.getWorld();

                    // Start attack animation preparation
//                if (!this.isPreparingAttack && this.ticksUntilNextAttack <= 0) {
//                    this.isPreparingAttack = true;
//                    this.luminary.setAttacking(true); // Start animation
//                    this.ticksUntilNextAttack = this.attackAnimationTicks;
//                }

                    // When it's time to attack during animation
                    double speed = 1.5; // Adjusted speed for Wind Charge
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // Calculate velocity towards the target
                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    // Create and spawn the WindCharge entity
                    WindChargeEntity windChargeEntity = new WindChargeEntity(EntityType.WIND_CHARGE, world);
                    windChargeEntity.setVelocity(velocity);
                    windChargeEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );

                    world.spawnEntity(windChargeEntity);
                    this.ticksUntilNextAttack = 45; // Cooldown before next attack
                }

                // Reduce cooldown timer
                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            }
        } else {
            resetAttackCooldown();
            this.ticksUntilNextAttack = 45;
            luminary.setAttacking(false);
        }
    }
}

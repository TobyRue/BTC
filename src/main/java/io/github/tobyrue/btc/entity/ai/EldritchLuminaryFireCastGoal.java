package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryFireCastGoal extends Goal {
    private final EldritchLuminaryEntity luminary;
    private int ticksUntilNextAttack = 45; // Attack delay in ticks (1 second = 20 ticks)
    private int attackDelay = 45;
    public EldritchLuminaryFireCastGoal(EldritchLuminaryEntity luminary) {
        this.luminary = luminary;
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        attackDelay = 45;
        ticksUntilNextAttack = 45;
        luminary.setAttacking(true);
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
            double maxDistance = 64.0;
            if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {
                if (isEnemyWithinAttackDistance(eEnemy)) {
                    World world = this.luminary.getWorld();

                // Rotate towards the target
                Vec3d targetPos = eEnemy.getPos().subtract(this.luminary.getPos());
                double targetYaw = MathHelper.atan2(targetPos.z, targetPos.x) * (180.0 / Math.PI) - 90.0;
                double yawDifference = MathHelper.wrapDegrees(this.luminary.getYaw() - (float) targetYaw);

                if (Math.abs(yawDifference) > 10.0F) {
                    // If not facing the target, rotate towards it
                    this.luminary.getLookControl().lookAt(eEnemy, 30.0F, 30.0F);
                    this.luminary.setYaw((float) targetYaw);
                    return;
                }

                if (this.isTimeToAttack()) {
                    double speed = 1.5; // Adjusted speed for higher accuracy
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // More precise aim towards the center of the target
                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d vec3d2 = new Vec3d(dx, dy, dz);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    FireballEntity fireballEntity = new FireballEntity(world, this.luminary, vec3d2.normalize(), this.luminary.getFireballStrength());
                    fireballEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );

                    world.spawnEntity(fireballEntity);
                    this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
                }
            } else {
                resetAttackCooldown();
                this.ticksUntilNextAttack = 45;
                luminary.setAttacking(false);
            }

        }
    }
}

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
    public int cooldown;

    public EldritchLuminaryFireCastGoal(EldritchLuminaryEntity luminary) {
        this.luminary = luminary;
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        this.cooldown = 0;
        luminary.setAttacking(true);
    }

    @Override
    public void stop() {
        luminary.setAttacking(false);
        System.out.println("Shooting stopped.");
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.luminary.getTarget();
        if (target != null) {
            double maxDistance = 64.0;
            if (target.squaredDistanceTo(this.luminary) < maxDistance * maxDistance && this.luminary.canSee(target)) {
                World world = this.luminary.getWorld();
                ++this.cooldown;

                // Rotate towards the target
                Vec3d targetPos = target.getPos().subtract(this.luminary.getPos());
                double targetYaw = MathHelper.atan2(targetPos.z, targetPos.x) * (180.0 / Math.PI) - 90.0;
                double yawDifference = MathHelper.wrapDegrees(this.luminary.getYaw() - (float) targetYaw);

                if (Math.abs(yawDifference) > 10.0F) {
                    // If not facing the target, rotate towards it
                    this.luminary.getLookControl().lookAt(target, 30.0F, 30.0F);
                    this.luminary.setYaw((float) targetYaw);
                    this.cooldown = 0; // Reset cooldown if not facing the target
                    return;
                }

                if (this.cooldown == 20) {
                    double speed = 1.5; // Adjusted speed for higher accuracy
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // More precise aim towards the center of the target
                    double dx = target.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = target.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = target.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d vec3d2 = new Vec3d(dx, dy, dz);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    FireballEntity fireballEntity = new FireballEntity(world, this.luminary, vec3d2.normalize(), this.luminary.getFireballStrength());
                    fireballEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );

                    world.spawnEntity(fireballEntity);
                    this.cooldown = -40;
                }
            } else if (this.cooldown > 0) {
                --this.cooldown;
            }

            System.out.println("Cooldown: " + (this.cooldown > 10));
        }
    }
}

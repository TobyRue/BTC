package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryFireCastGoal extends Goal {
        private final EldritchLuminaryEntity luminary;
        public int cooldown;

        public EldritchLuminaryFireCastGoal(EldritchLuminaryEntity luminary) {
            this.luminary = luminary;
        }

    public boolean canStart() {
            return this.luminary.getTarget() != null;
        }

        public void start() {
            this.cooldown = 0;
        }

        public void stop() {
            System.out.println("Shooting, " + false);
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.luminary.getTarget();
            if (livingEntity != null) {
                double d = 64.0;
                if (livingEntity.squaredDistanceTo(this.luminary) < 4096.0 && this.luminary.canSee(livingEntity)) {
                    World world = this.luminary.getWorld();
                    ++this.cooldown;

                    if (this.cooldown == 20) {
                        double e = 4.0;
                        Vec3d vec3d = this.luminary.getRotationVec(1.0F);
                        double f = livingEntity.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                        double g = livingEntity.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                        double h = livingEntity.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);
                        Vec3d vec3d2 = new Vec3d(f, g, h);

                        FireballEntity fireballEntity = new FireballEntity(world, this.luminary, vec3d2.normalize(), this.luminary.getFireballStrength());
                        fireballEntity.setPosition(this.luminary.getX() + vec3d.x * 1, this.luminary.getBodyY(0.5) + 0.5, fireballEntity.getZ() + vec3d.z * 1);
                        world.spawnEntity(fireballEntity);
                        this.cooldown = -40;
                    }
                } else if (this.cooldown > 0) {
                    --this.cooldown;
                }

                System.out.println("Cooldown, " + (this.cooldown > 10));
            }
        }

}

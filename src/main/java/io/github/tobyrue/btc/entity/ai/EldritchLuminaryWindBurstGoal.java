package io.github.tobyrue.btc.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class EldritchLuminaryWindBurstGoal extends Goal {
    private final MobEntity luminary;
    private LivingEntity target;
    private final World world;
    private int cooldown = 0;
    private double windChargeSpeed = 0;


    public EldritchLuminaryWindBurstGoal(MobEntity luminary, double windChargeSpeed) {
        this.luminary = luminary;
        this.world = luminary.getWorld();
        this.windChargeSpeed = windChargeSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }


    @Override
    public void start() {
        this.cooldown = 0;
    }

    @Override
    public void stop() {
        System.out.println("Shooting stopped.");
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }
    @Override
    public void tick() {
        if (this.target == null) return;

        this.luminary.lookAtEntity(this.target, 30.0F, 30.0F);

        if (cooldown > 0) {
            cooldown--;
            return;
        }

        double distance = this.luminary.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        if (distance < 100.0D) { // 10 blocks range
            // Play wind sound
            this.luminary.playSound(SoundEvents.ENTITY_WIND_CHARGE_THROW, 1.0F, 1.0F);

            // Calculate direction
            Vec3d direction = this.target.getPos().subtract(this.luminary.getPos()).normalize();

            double speedMultiplier = windChargeSpeed;
            Vec3d velocity = direction.multiply(speedMultiplier);

            WindChargeEntity windCharge = new WindChargeEntity(
                    this.luminary.getWorld(),
                    this.luminary.getX() + direction.x * 2.0,
                    this.luminary.getEyeY() + 0.5,
                    this.luminary.getZ() + direction.z * 2.0,
                    velocity
            );

            // Set the owner of the wind charge
            windCharge.setOwner(this.luminary);

            windCharge.setVelocity(velocity);

            // Spawn the wind charge in the world
            this.luminary.getWorld().spawnEntity(windCharge);

            // Reset cooldown to 2–3 seconds (randomly chosen)
            cooldown = this.luminary.getRandom().nextInt(21) + 40;
        }
        System.out.println("Cooldown: " + (this.cooldown > 10));
    }
}

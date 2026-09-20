package io.github.tobyrue.btc.status_effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class AccelerationEffect extends StatusEffect {
    private static final double MAX_AIR_HORIZONTAL_SPEED = 0.8;
    private static final double MAX_AIR_VERTICAL_SPEED = 0.5;

    private static final double ICE_FRICTION = 0.98;

    public AccelerationEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xFFFFFF);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        Vec3d velocity = entity.getVelocity();

        boolean isMovingInput = false;
        if (entity instanceof PlayerEntity player) {
            isMovingInput = Math.abs(player.sidewaysSpeed) > 1e-3F || Math.abs(player.forwardSpeed) > 1e-3F;
        } else {
            isMovingInput = velocity.horizontalLengthSquared() > 1e-4;
        }

        if (entity.isFallFlying()) {
            entity.setVelocity(velocity.multiply(
                    1 - (0.1 / (amplifier + 1)),
                    1 - (0.025 / (amplifier + 1)),
                    1 - (0.1 / (amplifier + 1))
            ));
        } else if (!entity.isOnGround()) {
            if (isMovingInput) {
                double speedMultiplierX = 1 + 0.05 * (amplifier + 1);
                double speedMultiplierY = 1 + 0.01 * (amplifier + 1);
                double speedMultiplierZ = 1 + 0.05 * (amplifier + 1);

                double newX = velocity.x * speedMultiplierX;
                double newY = velocity.y * speedMultiplierY;
                double newZ = velocity.z * speedMultiplierZ;

                double horizontalSpeedSq = newX * newX + newZ * newZ;
                double maxHorizontal = MAX_AIR_HORIZONTAL_SPEED * (1 + 0.2 * amplifier);
                if (horizontalSpeedSq > maxHorizontal * maxHorizontal) {
                    double scale = maxHorizontal / Math.sqrt(horizontalSpeedSq);
                    newX *= scale;
                    newZ *= scale;
                }

                double maxVertical = MAX_AIR_VERTICAL_SPEED * (1 + 0.2 * amplifier);
                newY = Math.max(-maxVertical, Math.min(maxVertical, newY));

                entity.setVelocity(new Vec3d(newX, newY, newZ));
            } else {
                entity.setVelocity(new Vec3d(velocity.x * ICE_FRICTION, velocity.y, velocity.z * ICE_FRICTION));
            }
        } else {
            if (isMovingInput) {
                entity.setVelocity(velocity.multiply(
                        1 + 0.2 * (amplifier + 1),
                        1 + 0.05 * (amplifier + 1),
                        1 + 0.2 * (amplifier + 1)
                ));
            } else {
                entity.setVelocity(new Vec3d(velocity.x * ICE_FRICTION, velocity.y, velocity.z * ICE_FRICTION));
            }
        }

        return super.applyUpdateEffect(entity, amplifier);
    }
}
package io.github.tobyrue.btc.status_effects;

import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ParticleCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class FireBurstStatusEffect extends StatusEffect {
    public boolean durationMax = false;
    public int durationLock;
    public Vec3d storedPos;

    public FireBurstStatusEffect() {
        super(StatusEffectCategory.HARMFUL, 0xFF4500); // Orange-red color
    }

    @Override
    public boolean applyUpdateEffect(LivingEntity entity, int amplifier) {
        World world = entity.getWorld();

        if (world instanceof ServerWorld serverWorld) {
            StatusEffectInstance effectInstance = entity.getStatusEffect(ModStatusEffects.FIRE_BURST);
            if (effectInstance != null) {
                int duration = effectInstance.getDuration();

                // Fixed assumed applied duration
                if (!durationMax) {
                    durationLock = duration;
                    durationMax = true;
                    storedPos = entity.getPos();
                }
                double maxRadius = Math.min(64.0, 4.0 * (amplifier + 1));
                double progress = 1.0 - (duration / (double) durationLock);
                progress = Math.max(0.0, Math.min(1.0, progress)); // clamp
                double radius = maxRadius * progress;


                int count = (int) (40 * (Math.min(32, amplifier + 1)));

                for (int i = 0; i < count; i++) {
                    double angle = (2 * Math.PI / count) * i;

                    double x = storedPos.getX() + Math.sin(angle) * radius;
                    double z = storedPos.getZ() + Math.cos(angle) * radius;

                    double yOffset = 0.0;
                    double y = storedPos.getY() + 1.0 + yOffset;

                    double xSpeed = Math.sin(angle) * 0.2;
                    double zSpeed = Math.cos(angle) * 0.2;

                    serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
                }
                Vec3d origin = storedPos;

                for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != entity)) {
                    double dist = target.getPos().distanceTo(origin);

                    double stepSize = maxRadius / durationLock;
                    if (dist <= radius && dist > (radius - stepSize)) {
                        target.setOnFireFor((float) ((radius * -1) + maxRadius));
                        target.damage(entity.getDamageSources().inFire(), Math.min(8, (float) ((radius * -1) + maxRadius)));
                    }
                }
            }
        }
        return true;
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true; // Runs every tick while active
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        durationMax = false;
    }

    @Override
    public void onEntityRemoval(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        durationMax = false;
    }
}
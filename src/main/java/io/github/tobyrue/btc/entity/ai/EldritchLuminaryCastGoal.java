package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.enums.AttackType;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EldritchLuminaryCastGoal extends Goal {
    private final EldritchLuminaryEntity luminary;


    public EldritchLuminaryCastGoal(PathAwareEntity mob) {
        luminary = ((EldritchLuminaryEntity) mob);
    }

    @Override
    public boolean canStart() {
        return this.luminary.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
    }

    protected boolean isTimeToAttack() {
        return luminary.getProgress() == 20;
    }

    protected boolean canDisappear() {
        return luminary.getDisappearDelay() <= 0;
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
//            if (eEnemy == null) {
//                this.ticksUntilNextAttack = 43;
//                return;
//            }
//            if(/*isTimeToStartAttackAnimation() && */luminary.getAttack() == AttackType.NONE) {
//                luminary.setAttack(AttackType.FIRE_BALL);
//            }
            if (luminary.getAttack() == AttackType.NONE && luminary.getProgress() == 50) {
                // First number of Random is the amount of outcomes, second number should never change and is the offset
                int random = (int)(Math.random() * 5 + 1);
                luminary.setAttack(AttackType.byId(random));
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
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
                    World world = this.luminary.getWorld();
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
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.REGENERATION) {
                    World world = this.luminary.getWorld();
                    luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.WATER_BLAST) {
                    World world = this.luminary.getWorld();

                    double speed = 1.5;
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // More precise aim towards the center of the target
                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d vec3d2 = new Vec3d(dx, dy, dz);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    WaterBlastEntity waterBlast = new WaterBlastEntity(luminary, world, luminary.getX(), luminary.getY() + 1.25, luminary.getZ(), velocity);
                    waterBlast.setVelocity(velocity);
                    waterBlast.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );
                    waterBlast.setNoGravity(true);
                    world.spawnEntity(waterBlast);
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.FIRE_BALL) {
                    World world = this.luminary.getWorld();
                    double speed = 1.5;
                    Vec3d vec3d = this.luminary.getRotationVec(1.0F);

                    // More precise aim towards the center of the target
                    double dx = eEnemy.getX() - (this.luminary.getX() + vec3d.x * 4.0);
                    double dy = eEnemy.getBodyY(0.5) - (0.5 + this.luminary.getBodyY(0.5));
                    double dz = eEnemy.getZ() - (this.luminary.getZ() + vec3d.z * 4.0);

                    Vec3d vec3d2 = new Vec3d(dx, dy, dz);

                    Vec3d velocity = new Vec3d(dx, dy, dz).normalize().multiply(speed);

                    FireballEntity fireballEntity = new FireballEntity(world, this.luminary, vec3d2.normalize(), this.luminary.getFireballStrength());
                    fireballEntity.setVelocity(velocity);
                    fireballEntity.setPosition(
                            this.luminary.getX() + vec3d.x * 1.5,
                            this.luminary.getBodyY(0.5) + 0.5,
                            this.luminary.getZ() + vec3d.z * 1.5
                    );
                    world.spawnEntity(fireballEntity);
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.INVISIBLE && canDisappear() && !luminary.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                    this.luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
                }
            }
        } else {
            if (luminary.getAttack() != AttackType.NONE) {
                luminary.setAttack(AttackType.NONE);
            }
        }
    }
    @Override
    public void stop() {
        luminary.setAttack(AttackType.NONE);
        super.stop();
    }
}

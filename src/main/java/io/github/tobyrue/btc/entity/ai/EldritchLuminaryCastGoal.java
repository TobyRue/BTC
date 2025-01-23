package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.AttackType;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

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
        System.out.println("Attack is, " + luminary.getAttack());
        if (isEnemyWithinAttackDistance(eEnemy)) {
//            if (eEnemy == null) {
//                this.ticksUntilNextAttack = 43;
//                return;
//            }
//            if(/*isTimeToStartAttackAnimation() && */luminary.getAttack() == AttackType.NONE) {
//                luminary.setAttack(AttackType.FIRE_BALL);
//            }
            System.out.println("Progress is: " + luminary.getProgress());
            if (luminary.getAttack() == AttackType.NONE && luminary.getProgress() == 50) {
                int random = (int)(Math.random() * 3 + 2);
                System.out.println("Random is: " + random);
                luminary.setAttack(AttackType.byId(random));
            }
            double maxDistance = 64.0;
            if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {
                if (isTimeToAttack() && luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
                    World world = this.luminary.getWorld();
//                    System.out.println(luminary.getAttack() + " Fire with client " + world.isClient);
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
                }
                if (isTimeToAttack() && luminary.getAttack() == AttackType.FIRE_BALL) {
                    World world = this.luminary.getWorld();
//                    System.out.println(luminary.getAttack() + " Fire with client " + world.isClient);

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
//                    System.out.println("Has Invisibility: " + luminary.hasStatusEffect(StatusEffects.INVISIBILITY));
                    this.luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
                }
            }
        } else {
            if (luminary.getAttack() == AttackType.FIRE_BALL || luminary.getAttack() == AttackType.DRAGON_FIRE_BALL || luminary.getAttack() == AttackType.INVISIBLE) {
                luminary.setAttack(AttackType.NONE);
            }
        }
//        System.out.println("Can Disappear is, " + canDisappear() + ", Can Disappear progress is, " + luminary.getDisappearDelay());
    }
    @Override
    public void stop() {
        luminary.setAttack(AttackType.NONE);
        super.stop();
    }
}

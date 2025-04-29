package io.github.tobyrue.btc.entity.ai;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.enums.AttackType;
import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        return this.luminary.distanceTo(eEnemy) >= 3f && this.luminary.distanceTo(eEnemy) <= 16f;
    }
    private boolean isEnemyWithinSmallAttackDistance(LivingEntity eEnemy) {
        return this.luminary.distanceTo(eEnemy) >= 0f && this.luminary.distanceTo(eEnemy) <= 6f;
    }
    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public boolean shouldContinue() {
        return this.luminary.getTarget() != null;
    }

    @Nullable
    public BlockPos findSpawnableGround(World world, BlockPos centerPos, int yRange) {
        int topY = Math.min(centerPos.getY() + yRange, world.getTopY());
        int bottomY = Math.max(centerPos.getY() - yRange, world.getBottomY());

        System.out.println("Searching from Y " + topY + " to " + bottomY + " at XZ: " + centerPos.getX() + ", " + centerPos.getZ());

        // Start from top and go downwards
        for (int y = topY; y >= bottomY; y--) {
            BlockPos pos = new BlockPos(centerPos.getX(), y, centerPos.getZ());
            // Improved block check to ensure solid block and air above or open space above
            if (world.getBlockState(pos).isSolidBlock(world, pos) && !world.getBlockState(pos.up()).isSolidBlock(world, pos.up()) && !world.getBlockState(pos.up()).isOf(Blocks.CHEST)) {
                System.out.println("Found spawnable ground at: " + pos);
                return pos;
            }
        }

        // Fallback if no valid ground is found
        System.out.println("No ground found at XZ: " + centerPos.getX() + ", " + centerPos.getZ());
        return null;
    }

    private static void shootProjectile(EldritchLuminaryEntity shooter, LivingEntity target, ProjectileEntity projectile, float speed) {
        Vec3d shooterEye = shooter.getEyePos();
        Vec3d targetEye = target.getEyePos();

        Vec3d direction = targetEye.subtract(shooterEye).normalize();

        projectile.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);

        projectile.setVelocity(direction.multiply(speed));
        projectile.setNoGravity(true); // magic projectiles like breeze and yours have no gravity
    }

    private static void shootTargetAway(EldritchLuminaryEntity luminary, LivingEntity target, int strength, int damage) {
        double dx = target.getX() - luminary.getX();
        double dy = target.getY() - luminary.getY();
        double dz = target.getZ() - luminary.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        // Apply velocity away from the user
        if (distance != 0) {
            target.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
        }
        target.damage(luminary.getWorld().getDamageSources().flyIntoWall(), damage);
    }

    @Override
    public void tick() {
        LivingEntity eEnemy = this.luminary.getTarget();
        World world = this.luminary.getWorld();
        if (eEnemy != null) {
            if (isEnemyWithinSmallAttackDistance(eEnemy)) {
                if (this.luminary.squaredDistanceTo(eEnemy) < 6 * 6) {
                    if (luminary.getHealth() <= 0.5 * luminary.getMaxHealth() && !luminary.hasStatusEffect(StatusEffects.REGENERATION)) {
                        int randomF = (int) (Math.random() * 2);
                        luminary.setAttack(AttackType.REGENERATION);
                        if (randomF == 0) {
                            shootTargetAway(luminary, eEnemy, 2, 4);
                        }
                        if (isTimeToAttack() && luminary.getAttack() == AttackType.REGENERATION) {
                            shootTargetAway(luminary, eEnemy, 1, 2);
                            luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
                        }
                    } else {
                        if (luminary.getAttack() == AttackType.NONE && luminary.getProgress() == 50) {
                            int randomE = (int) (Math.random() * 5);
                            if (randomE == 0 || randomE == 1) {
                                luminary.setAttack(AttackType.EARTH_SPIKE_C);
                            } else if (randomE == 2 || randomE == 3){
                                luminary.setAttack(AttackType.EARTH_SPIKE_L);
                            } else {
                                int projChoice = (int) (Math.random() * 4); // Random choice of projectile
                                if (projChoice == 0) {
                                    luminary.setAttack(AttackType.WIND_CHARGE);
                                } else if (projChoice == 1) {
                                    luminary.setAttack(AttackType.FIRE_BALL);
                                } else if (projChoice == 2) {
                                    luminary.setAttack(AttackType.WATER_BLAST);
                                } else {
                                    luminary.setAttack(AttackType.DRAGON_FIRE_BALL);
                                }
                            }
                        }
                        if (isTimeToAttack() && luminary.getAttack() == AttackType.EARTH_SPIKE_C) {

                            double distance = this.luminary.distanceTo(eEnemy);
                            int spikes = Math.max(8, (int) (distance * 4) + 8);  // number of spikes proportional to distance
                            double radius = distance;

                            for (int i = 0; i < spikes; i++) {
                                double angle = 2 * Math.PI * i / spikes;
                                double x = luminary.getX() + radius * Math.cos(angle);
                                double z = luminary.getZ() + radius * Math.sin(angle);
                                BlockPos groundPos = findSpawnableGround(world, new BlockPos((int) x, (int) luminary.getY(), (int) z), 8);

                                if (groundPos != null) {
                                    EarthSpikeEntity spike = new EarthSpikeEntity(world, x, groundPos.getY(), z, luminary.getYaw(), luminary);
                                    luminary.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(luminary));
                                    world.spawnEntity(spike);
                                }
                            }

                            EarthSpikeEntity spike2 = new EarthSpikeEntity(world, eEnemy.getX(), eEnemy.getY() - 1, eEnemy.getZ(), luminary.getYaw(), luminary);
                            luminary.getWorld().emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(eEnemy.getX(), eEnemy.getY(), eEnemy.getZ()), GameEvent.Emitter.of(luminary));
                            world.spawnEntity(spike2);

                        } else if (isTimeToAttack() && luminary.getAttack() == AttackType.EARTH_SPIKE_L) {
                            Vec3d startPos = luminary.getPos();
                            Vec3d targetPos = eEnemy.getPos();
                            Vec3d direction = targetPos.subtract(startPos).normalize();

                            int spikeCount = 8;    // how many spikes in the line

                            for (int i = 1; i <= spikeCount; i++) {
                                double x = startPos.x + direction.x * i;
                                double z = startPos.z + direction.z * i;
                                BlockPos groundPos = findSpawnableGround(world, new BlockPos((int) x, (int) luminary.getY(), (int) z), 8);
                                if (groundPos != null) {
                                    EarthSpikeEntity spike = new EarthSpikeEntity(world, x, groundPos.getY(), z, luminary.getYaw(), luminary);
                                    world.emitGameEvent(GameEvent.ENTITY_PLACE, new Vec3d(x, groundPos.getY(), z), GameEvent.Emitter.of(luminary));
                                    world.spawnEntity(spike);
                                }
                            }
                        }
                    }
                }
            } else if (isEnemyWithinAttackDistance(eEnemy)) {
                if (luminary.getAttack() == AttackType.NONE && luminary.getProgress() == 50) {
                    // First number of Random is the amount of outcomes, second number should never change and is the offset
                    int random = (int) (Math.random() * 5 + 1);
                    luminary.setAttack(AttackType.byId(random));
                }
//                double maxDistance = 64.0;
//                if (this.luminary.squaredDistanceTo(eEnemy) < maxDistance * maxDistance && this.luminary.canSee(eEnemy)) {
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.WIND_CHARGE) {
//                        WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, world);
//                        shootProjectile(luminary, eEnemy, windCharge, 1.5F);
//                        world.spawnEntity(windCharge);
//                    }
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
//                        Vec3d shooterEye = luminary.getEyePos();
//                        Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
//                        Vec3d direction = targetEye.subtract(shooterEye).normalize();
//
//
//                        DragonFireballEntity dragonFireball = new DragonFireballEntity(world, luminary, direction.multiply(1.3F));
//                        dragonFireball.setVelocity(direction.multiply(1.3F));
//
//                        dragonFireball.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);
//                        world.spawnEntity(dragonFireball);
//                    }
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.REGENERATION) {
//                        luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
//                    }
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.WATER_BLAST) {
//                        Vec3d shooterEye = luminary.getEyePos();
//                        Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
//                        Vec3d direction = targetEye.subtract(shooterEye).normalize();
//
//                        WaterBlastEntity waterBlast = new WaterBlastEntity(luminary, world, luminary.getX(), luminary.getY() + 1.25, luminary.getZ(), direction.multiply(1.5F));
//
//                        waterBlast.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);
//
//                        waterBlast.setVelocity(direction.multiply(1.5F));
//                        waterBlast.setNoGravity(true);
//                        world.spawnEntity(waterBlast);
//                    }
//
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.FIRE_BALL) {
//                        Vec3d shooterEye = luminary.getEyePos();
//                        Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
//                        Vec3d direction = targetEye.subtract(shooterEye).normalize();
//
//                        FireballEntity fireball = new FireballEntity(world, luminary, direction.multiply(1.2), this.luminary.getFireballStrength());
//
//                        fireball.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);
//
//                        fireball.setVelocity(direction.multiply(1.2F));
//                        world.spawnEntity(fireball);
//                    }
//                    if (isTimeToAttack() && luminary.getAttack() == AttackType.INVISIBLE && canDisappear() && !luminary.hasStatusEffect(StatusEffects.INVISIBILITY)) {
//                        this.luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
//                    ]
            } else {
                if (luminary.getAttack() != AttackType.NONE) {
                    luminary.setAttack(AttackType.NONE);
                }
            }
            if (isTimeToAttack() && luminary.getAttack() == AttackType.WIND_CHARGE) {
                WindChargeEntity windCharge = new WindChargeEntity(EntityType.WIND_CHARGE, world);
                shootProjectile(luminary, eEnemy, windCharge, 1.5F);
                world.spawnEntity(windCharge);
            }
            if (isTimeToAttack() && luminary.getAttack() == AttackType.DRAGON_FIRE_BALL) {
                Vec3d shooterEye = luminary.getEyePos();
                Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
                Vec3d direction = targetEye.subtract(shooterEye).normalize();


                DragonFireballEntity dragonFireball = new DragonFireballEntity(world, luminary, direction.multiply(1.3F));
                dragonFireball.setVelocity(direction.multiply(1.3F));

                dragonFireball.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);
                world.spawnEntity(dragonFireball);
            }
            if (isTimeToAttack() && luminary.getAttack() == AttackType.REGENERATION) {
                luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 2));
            }
            if (isTimeToAttack() && luminary.getAttack() == AttackType.WATER_BLAST) {
                Vec3d shooterEye = luminary.getEyePos();
                Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
                Vec3d direction = targetEye.subtract(shooterEye).normalize();

                WaterBlastEntity waterBlast = new WaterBlastEntity(luminary, world, luminary.getX(), luminary.getY() + 1.25, luminary.getZ(), direction.multiply(1.5F));

                waterBlast.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);

                waterBlast.setVelocity(direction.multiply(1.5F));
                waterBlast.setNoGravity(true);
                world.spawnEntity(waterBlast);
            }

            if (isTimeToAttack() && luminary.getAttack() == AttackType.FIRE_BALL) {
                Vec3d shooterEye = luminary.getEyePos();
                Vec3d targetEye = eEnemy.getEyePos().subtract(0, 0.5, 0);
                Vec3d direction = targetEye.subtract(shooterEye).normalize();

                FireballEntity fireball = new FireballEntity(world, luminary, direction.multiply(1.2), this.luminary.getFireballStrength());

                fireball.setPosition(shooterEye.x, shooterEye.y, shooterEye.z);

                fireball.setVelocity(direction.multiply(1.2F));
                world.spawnEntity(fireball);
            }
            if (isTimeToAttack() && luminary.getAttack() == AttackType.INVISIBLE && canDisappear() && !luminary.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                this.luminary.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
            }
        }
    }
    @Override
    public void stop() {
        luminary.setAttack(AttackType.NONE);
        super.stop();
    }
}

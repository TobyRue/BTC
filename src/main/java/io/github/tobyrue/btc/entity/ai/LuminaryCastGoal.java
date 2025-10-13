//package io.github.tobyrue.btc.entity.ai;
//
//import io.github.tobyrue.btc.entity.custom.EarthSpikeEntity;
//import io.github.tobyrue.btc.entity.custom.EldritchLuminaryEntity;
//import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
//import io.github.tobyrue.btc.enums.AttackType;
//import net.minecraft.block.Blocks;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.ai.goal.Goal;
//import net.minecraft.entity.effect.StatusEffectInstance;
//import net.minecraft.entity.effect.StatusEffects;
//import net.minecraft.entity.mob.PathAwareEntity;
//import net.minecraft.entity.projectile.DragonFireballEntity;
//import net.minecraft.entity.projectile.FireballEntity;
//import net.minecraft.entity.projectile.ProjectileEntity;
//import net.minecraft.entity.projectile.WindChargeEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Vec3d;
//import net.minecraft.world.World;
//import net.minecraft.world.event.GameEvent;
//import org.jetbrains.annotations.Nullable;
//
//public class LuminaryCastGoal extends Goal {
//    private final EldritchLuminaryEntity luminary;
//
//
//    public LuminaryCastGoal(PathAwareEntity mob) {
//        luminary = ((EldritchLuminaryEntity) mob);
//    }
//
//    @Override
//    public boolean canStart() {
//        return this.luminary.getTarget() != null;
//    }
//
//    @Override
//    public void start() {
//        super.start();
//    }
//
//    protected boolean isTimeToAttack() {
//        return luminary.getProgress() == 20;
//    }
//
//    protected boolean canDisappear() {
//        return luminary.getDisappearDelay() <= 0;
//    }
//
//    private boolean isEnemyWithinAttackDistance(LivingEntity eEnemy) {
//        return this.luminary.distanceTo(eEnemy) >= 3f && this.luminary.distanceTo(eEnemy) <= 32f;
//    }
//
//    private boolean isEnemyWithinSmallAttackDistance(LivingEntity eEnemy) {
//        return this.luminary.distanceTo(eEnemy) >= 0f && this.luminary.distanceTo(eEnemy) <= 6f;
//    }
//
//    @Override
//    public boolean shouldRunEveryTick() {
//        return true;
//    }
//
//    @Override
//    public boolean shouldContinue() {
//        return this.luminary.getTarget() != null;
//    }
//
//    @Override
//    public void tick() {
//        LivingEntity eEnemy = this.luminary.getTarget();
//        World world = this.luminary.getWorld();
//
//    }
//
//    @Override
//    public void stop() {
//        luminary.setAttack(AttackType.NONE);
//        super.stop();
//    }
//}

package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.EldritchLuminariesStrafeGoal;
import io.github.tobyrue.btc.entity.ai.EldritchLuminaryFireCastGoal;
import io.github.tobyrue.btc.entity.ai.EldritchLuminaryStrafeGoal;
import io.github.tobyrue.btc.entity.ai.EldritchLuminaryWindBurstGoal;
import io.github.tobyrue.btc.item.ModItems;
import io.github.tobyrue.btc.item.StaffItem;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EldritchLuminaryEntity extends HostileEntity implements Angerable, RangedAttackMob {

    @Nullable
    private StaffItem staff = null;
//    private static final TrackedData<Boolean> ATTACKING =
//            DataTracker.registerData(EldritchLuminariesEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public EldritchLuminaryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.staff = ModItems.FIRE_STAFF;
        this.experiencePoints = 15;
    }
    public int getFireballStrength() {
        return 1;
    }
    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }

        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 40;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.getWorld().isClient()) {
            setupAnimationStates();
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1D));
//        this.goalSelector.add(2, new EldritchLuminaryFireCastGoal(this));
        this.goalSelector.add(2, new EldritchLuminaryWindBurstGoal(this, 2));
        this.goalSelector.add(2, new TemptGoal(this, 1.3D, Ingredient.ofItems(ModItems.STAFF, ModItems.DRAGON_STAFF, ModItems.FIRE_STAFF, ModItems.WIND_STAFF, ModItems.RUBY_TRIAL_KEY), false));
        this.goalSelector.add(1, new EldritchLuminaryStrafeGoal(this, 0.8, 12.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> {
            return Math.abs(entity.getY() - this.getY()) <= 4.0;
        }));
    }

    public static DefaultAttributeContainer.Builder createEldritchLuminaryAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
    }

    @Nullable
    public StaffItem getHeldStaff() {
        return this.staff;
    }


    @Override
    public int getAngerTime() {
        return 0;
    }

    @Override
    public void setAngerTime(int angerTime) {

    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return null;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {

    }

    @Override
    public void chooseRandomAngerTime() {
    }



    @Override
    public void shootAt(LivingEntity target, float pullProgress) {

//        ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
//        ItemStack itemStack2 = this.getProjectileType(itemStack);
//        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
//        double d = target.getX() - this.getX();
//        double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
//        double f = target.getZ() - this.getZ();
//        double g = Math.sqrt(d * d + f * f);
//        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
//        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
//        this.getWorld().spawnEntity(persistentProjectileEntity);
    }
//    public void setAttacking(boolean attacking) {
//        this.dataTracker.set(ATTACKING, attacking);
//    }
//
//    @Override
//    public boolean isAttacking() {
//        return this.dataTracker.get(ATTACKING);
//    }
//
//    @Override
//    protected void initDataTracker(DataTracker.Builder builder) {
//        super.initDataTracker(builder);
//        builder.add(ATTACKING, false);
//    }

}

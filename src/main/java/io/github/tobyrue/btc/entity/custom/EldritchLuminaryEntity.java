package io.github.tobyrue.btc.entity.custom;


import io.github.tobyrue.btc.enums.AttackType;
import io.github.tobyrue.btc.entity.ai.*;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.EntityEffectParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EldritchLuminaryEntity extends HostileEntity implements Angerable, RangedAttackMob {
    private static final TrackedData<Byte> ATTACKING;
    private LivingEntity target;

    private int chooseAttack = 80;
    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    private int progress = 0;
    private int disappearDelay = 200;

    private AttackType attackType;
    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(5, new WanderAroundGoal(this, 1D));
//        this.goalSelector.add(1, new EldritchLuminaryWindBurstGoal(this));
//        this.goalSelector.add(1, new EldritchLuminaryDragonFireCastGoal(this));
//        this.goalSelector.add(1, new EldritchLuminaryFireCastGoal(this));
        this.goalSelector.add(1, new EldritchLuminaryCastGoal(this));
        this.goalSelector.add(6, new TemptGoal(this, 1.3D, Ingredient.ofItems(ModItems.STAFF, ModItems.DRAGON_STAFF, ModItems.FIRE_STAFF, ModItems.WIND_STAFF, ModItems.RUBY_TRIAL_KEY), false));
        this.goalSelector.add(1, new EldritchLuminaryStrafeGoal(this, 0.8, 16.0F));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, 10, true, false, (entity) -> {
            return Math.abs(entity.getY() - this.getY()) <= 4.0;
        }));
        this.targetSelector.add(0, new TrackTargetGoal(this, true, false) {
            @Override
            public boolean canStart() {
                return true;
            }
        });
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.isOf(DamageTypes.INDIRECT_MAGIC)) {
            return false;
        }
        return super.damage(source, amount);
    }

    public EldritchLuminaryEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 15;
        this.attackType = AttackType.NONE;
    }

    public int getFireballStrength() {
        return 3;
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.startIfNotRunning(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
        if(this.getAttack() != AttackType.NONE) {
            attackAnimationTimeout = 40;
            attackAnimationState.startIfNotRunning(this.age);
        } else {
            --this.attackAnimationTimeout;
        }

        if(this.getAttack() == AttackType.NONE) {
            attackAnimationState.stop();
        }
    }

    protected void updateLimbs(float posDelta) {
        float f = this.getPose() == EntityPose.STANDING ? Math.min(posDelta * 6.0f, 1.0f) : 0.0f;
        this.limbAnimator.updateLimbs(f, 0.2F);
    }

    public void triggerAttackAnimation() {
        this.attackAnimationState.start(this.age);
    }

    public int attackTick = 100; // Default to no action scheduled.





    @Override
    public void tick() {
        super.tick();
        if (this.getAttack() == AttackType.INVISIBLE && this.getDisappearDelay() > 600) {
            this.setAttack(AttackType.NONE);
            progress = 40;
        }

        if (!this.getWorld().isClient()) {
            if ((this.getAttack() == AttackType.INVISIBLE && this.getDisappearDelay() <= -20) || this.getAttack() != AttackType.INVISIBLE || (this.getAttack() == AttackType.INVISIBLE && this.hasStatusEffect(StatusEffects.INVISIBILITY)) || this.getAttack() == AttackType.NONE) {
                progress++;
            }
            disappearDelay--;
            if (progress == 65) {
                progress = 0;
            } else if (progress == 40) {
                setAttack(AttackType.NONE);
            }
            if (this.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                disappearDelay = 2400;
            }
        }

        //
        if (this.getWorld().isClient && this.getAttack() != AttackType.NONE) {
            AttackType spell = this.getAttack();
            float r = (float)spell.color[0];
            float g = (float)spell.color[1];
            float b = (float)spell.color[2];
            float i = this.bodyYaw * 0.017453292F + MathHelper.cos((float)this.age * 0.6662F) * 0.25F;
            float j = MathHelper.cos(i);
            float k = MathHelper.sin(i);
            double d = 0.6 * (double)this.getScale();
            double e = 1.8 * (double)this.getScale();
                this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), this.getX() + (double) j * d, this.getY() + e, this.getZ() + (double) k * d, 0.0, 0.0, 0.0);
                this.getWorld().addParticle(EntityEffectParticleEffect.create(ParticleTypes.ENTITY_EFFECT, r, g, b), this.getX() - (double) j * d, this.getY() + e, this.getZ() - (double) k * d, 0.0, 0.0, 0.0);

        }
        if (this.getWorld().isClient()) {
            setupAnimationStates();
        }
    }

    public int getProgress() {
        return this.progress;
    }
    public int getDisappearDelay() {
        return this.disappearDelay;
    }
    public void scheduleAction(int ticks) {
        this.attackTick = ticks;
    }

    private void executeTimedAction() {
        this.setAttack(AttackType.NONE); // Example action
    }
    static {
        ATTACKING = DataTracker.registerData(EldritchLuminaryEntity.class, TrackedDataHandlerRegistry.BYTE);
    }

    public void setAttack(AttackType attack) {
        this.dataTracker.set(ATTACKING, (byte) attack.id);
        this.attackType = attack;
    }

    public AttackType getAttack() {
        return !this.getWorld().isClient ? this.attackType : AttackType.byId((Byte)this.dataTracker.get(ATTACKING));
    }

    @Override
    public boolean isAttacking() {
        return this.getAttack() != AttackType.NONE;
    }

    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ATTACKING, (byte) 0);
    }

    public static DefaultAttributeContainer.Builder createEldritchLuminaryAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1.5)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
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
    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }
    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
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

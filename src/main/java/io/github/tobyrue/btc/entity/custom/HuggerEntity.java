package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ai.PounceAndAttackTargetGoal;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;

public class HuggerEntity extends HostileEntity {
    private static final TrackedData<Boolean> WINDING_UP = DataTracker.registerData(HuggerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public HuggerEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }
    @Override
    protected void initGoals() {

        this.goalSelector.add(10, new PounceAndAttackTargetGoal(this, 0.6F));
        this.targetSelector.add(8, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
//        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
//        this.goalSelector.add(4, new LookAroundGoal(this));
        this.goalSelector.add(
                4,
                new TemptGoal(
                        this,
                        1.3D,
                        Ingredient.ofItems(
                                ModItems.TRIAL_JERKY
                        ),
                        false
                )
        );
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(WINDING_UP, false);
    }

    public boolean isWindingUp() {
        return this.dataTracker.get(WINDING_UP);
    }

    public void setWindingUp(boolean windingUp) {
        this.dataTracker.set(WINDING_UP, windingUp);
    }

    public static DefaultAttributeContainer.Builder createHuggerEntityAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 8f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ARMOR, 2f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3f)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,1f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48)
                .add(EntityAttributes.GENERIC_STEP_HEIGHT, 1)
                .add(EntityAttributes.GENERIC_WATER_MOVEMENT_EFFICIENCY, 1)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 0.75f);
    }

    @Override
    public boolean hurtByWater() {
        return false;
    }
}

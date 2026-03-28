package io.github.tobyrue.btc.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TrialCubeEntity extends Entity {
    private static final TrackedData<Boolean> COMPANION = DataTracker.registerData(TrialCubeEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> CLICKS = DataTracker.registerData(TrialCubeEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final int REQUIRED_CLICKS = 64;
    private static final int HEARTS_START_AFTER = 51;

    public TrialCubeEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(COMPANION, false);
        builder.add(CLICKS, 0);
    }

    @Override
    protected double getGravity() {
        return this.isTouchingWater() ? 0.005 : 0.04;
    }

    @Override
    protected void applyGravity() {
        double d = this.getFinalGravity();
        if (d != 0.0) {
            this.setVelocity(this.getVelocity().add(0.0, -d, 0.0));
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        if (stack.isEmpty() && getClicks() != -1) {
            setClicks(getClicks() + 1);
            return ActionResult.SUCCESS;
        }
        if (getClicks() == -1 && isCompanion()) {
            showEmoteParticle(6, 6);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public void tick() {
        if (this.isOnGround()) {
            BlockState belowState = this.getWorld().getBlockState(this.getBlockPos().down());
            float friction = belowState.getBlock().getSlipperiness() * 0.91F;
            this.setVelocity(getVelocity().x * friction, getVelocity().y, getVelocity().z * friction);
        } else {
            this.applyGravity();
            this.setVelocity(this.getVelocity().multiply(0.98));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (getClicks() > HEARTS_START_AFTER && this.age % 20 == 0) {
            showEmoteParticle(getClicks() - HEARTS_START_AFTER, 12);
        }
        if (getClicks() >= REQUIRED_CLICKS) {
            setCompanion(true);
            setClicks(-1);
        }
        super.tick();
    }

    protected void showEmoteParticle(int count, int maxCount) {
        ParticleEffect particleEffect = ParticleTypes.HEART;
        for(int i = 0; i < Math.min(maxCount + 1, count + 1); ++i) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.getWorld().addParticle(particleEffect, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public ProjectileDeflection getProjectileDeflection(ProjectileEntity projectile) {
        return super.getProjectileDeflection(projectile);
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    public boolean isCompanion() {
        return this.dataTracker.get(COMPANION);
    }
    public void setCompanion(boolean companion) {
        this.dataTracker.set(COMPANION, companion);
    }

    public int getClicks() {
        return this.dataTracker.get(CLICKS);
    }
    public void setClicks(int clicks) {
        this.dataTracker.set(CLICKS, clicks);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.dataTracker.set(COMPANION, nbt.getBoolean("Companion"));
        this.dataTracker.set(CLICKS, nbt.getInt("Clicks"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Companion", this.dataTracker.get(COMPANION));
        nbt.putInt("Clicks", this.dataTracker.get(CLICKS));
    }
}

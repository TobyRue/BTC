package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EarthSpikeEntity extends Entity implements Ownable {

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;
    private int lifeTime = 0;
    private double baseY;
    private boolean setBaseY = false;
    private boolean deltDamage = false;

    public final AnimationState attackAnimationState = new AnimationState();

    public EarthSpikeEntity(EntityType<? extends EarthSpikeEntity> type, World world) {
        super(type, world);
        this.setPosition(this.getX(), this.getY(), this.getZ()); // Spawn lower
    }

    public EarthSpikeEntity(World world, double x, double y, double z, float yaw, LivingEntity owner) {
        this(ModEntities.EARTH_SPIKE, world);
        this.setOwner(owner);
        this.setYaw(yaw * 57.295776F);
        this.attackAnimationState.start(age);
        this.setPosition(x, y, z);
        //CALL this for the spawning and spawn the entity at top block pos
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        super.tick();
        if (!setBaseY) {
            baseY = this.getY();
            setBaseY = true;
        }
        if (lifeTime <= 30) {
            double yOffset = 0;

            if (lifeTime <= 5) {
                // Rising: 0 → 1 over 10 ticks
                yOffset = 1 * (lifeTime / 5.0);
            } else if (lifeTime <= 15) {
                // Pause at top
                yOffset = 1;
            } else {
                // Falling: 1 → 0 over 10 ticks
                yOffset = 1 * (1.0 - ((double) (lifeTime - 15) / 15));
            }
            if (!this.getWorld().isClient) {
                if (!deltDamage) {
                    for (Entity entity : this.getWorld().getOtherEntities(this, this.getBoundingBox())) {
                        if (entity instanceof LivingEntity && entity != this.getOwner()) {
                            entity.damage(this.getDamageSources().mobAttack(getOwner()), 6.0F); // Damage value: 6 hearts
                            deltDamage = true;
                        }
                    }
                }
            }

            if (lifeTime == 30) {
                this.kill();
            }

            this.setPosition(this.getX(), baseY + yOffset, this.getZ());
            lifeTime++;
        }
    }



    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    @Override
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUuid != null && this.getWorld() instanceof ServerWorld) {
            Entity entity = ((ServerWorld)this.getWorld()).getEntity(this.ownerUuid);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }
        return this.owner;
    }
}

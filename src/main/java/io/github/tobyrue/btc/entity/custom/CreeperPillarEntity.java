package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class CreeperPillarEntity extends Entity implements Ownable {

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUuid;
    private int lifeTime = 0;
    private double baseY;
    private boolean setBaseY = false;
    private boolean deltDamage = false;
    private boolean hasExploded = false;
    private CreeperPillarType creeperPillarType = CreeperPillarType.RANDOM;


    public CreeperPillarEntity(EntityType<? extends CreeperPillarEntity> type, World world) {
        super(type, world);
        if (getCreeperPillarType() == CreeperPillarType.RANDOM) {
            int number = new java.util.Random().nextInt(2);
            if (number == 0) {
                setCreeperPillarType(CreeperPillarType.EXPLOSIVE);
            } else {
                setCreeperPillarType(CreeperPillarType.NORMAL);
            }
        }
    }

    public CreeperPillarEntity(World world, double x, double y, double z, float yaw, LivingEntity owner, CreeperPillarType type) {
        this(ModEntities.CREEPER_PILLAR, world);
        this.setOwner(owner);
        this.setYaw(yaw * 57.295776F);
        this.setPosition(x, y, z);
        if (getCreeperPillarType() == CreeperPillarType.RANDOM) {
            int number = new java.util.Random().nextInt(2 );
            if (number == 0) {
                setCreeperPillarType(CreeperPillarType.EXPLOSIVE);
            } else {
                setCreeperPillarType(CreeperPillarType.NORMAL);
            }
        } else {
            this.creeperPillarType = type;
        }
    }
    public CreeperPillarType getCreeperPillarType() {
        return creeperPillarType;
    }
    public void setCreeperPillarType(CreeperPillarType pillarType) {
        this.creeperPillarType = pillarType;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
        this.ownerUuid = owner == null ? null : owner.getUuid();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (getCreeperPillarType() == CreeperPillarType.RANDOM) {
            int number = new java.util.Random().nextInt(2);
            if (number == 0) {
                setCreeperPillarType(CreeperPillarType.EXPLOSIVE);
            } else {
                setCreeperPillarType(CreeperPillarType.NORMAL);
            }
        }
        setCreeperPillarType(getCreeperPillarType());
        if (!setBaseY) {
            this.setPosition(this.getX(), this.getY(), this.getZ());
            System.out.println("Pillar type for " + this.getUuid() + " is " + getCreeperPillarType());
            baseY = this.getY();
            setBaseY = true;
        }
        if (lifeTime <= 140) {
            double yOffset = 0;

            if (lifeTime <= 10) {
                // Rising: 0 → 2 over 5 ticks
                yOffset = 2 * (lifeTime / 10.0);
            } else if (lifeTime <= 110) {
                // Pause at top for 100 ticks or 5 seconds
                yOffset = 2;
                if (getCreeperPillarType() == CreeperPillarType.EXPLOSIVE && !hasExploded && lifeTime >= 100) {
                    this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), null, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0F, true, World.ExplosionSourceType.BLOCK);
                    hasExploded = true;
                    this.discard();
                }
            } else {
                // Falling: 2 → 0 over 30 ticks
                yOffset = 2*(2.0- (double) (lifeTime - 80) / 30);
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

            if (lifeTime == 140) {
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

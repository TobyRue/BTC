package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.enums.CreeperPillarType;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.PickaxeItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.rmi.registry.Registry;
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
    private int pickaxeHitCount = 0;
    private int pickaxeHitTimer = 0;

    private static final TrackedData<String> PILLAR_TYPE =
            DataTracker.registerData(CreeperPillarEntity.class, TrackedDataHandlerRegistry.STRING);

    public CreeperPillarEntity(EntityType<? extends CreeperPillarEntity> type, World world) {
        super(type, world);
        if (!world.isClient()) {
            setCreeperPillarType(randomPillarType());
        }
    }
    public CreeperPillarEntity(World world, double x, double y, double z, float yaw, LivingEntity owner, CreeperPillarType type) {
        this(ModEntities.CREEPER_PILLAR, world);
        this.setOwner(owner);
        this.setYaw(yaw * 57.295776F);
        this.setPosition(x, y, z);
        if (!world.isClient()) {
            if (type == CreeperPillarType.RANDOM) {
                setCreeperPillarType(randomPillarType());
            } else {
                this.setCreeperPillarType(type);
            }
        }
    }
    private CreeperPillarType randomPillarType() {
        return new java.util.Random().nextInt(4) == 0 ? CreeperPillarType.EXPLOSIVE : CreeperPillarType.NORMAL;
    }
    public CreeperPillarType getCreeperPillarType() {
        return CreeperPillarType.valueOf(this.getDataTracker().get(PILLAR_TYPE));
    }

    public void setCreeperPillarType(CreeperPillarType pillarType) {
        this.getDataTracker().set(PILLAR_TYPE, pillarType.name());
    }
    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
        return super.createSpawnPacket(entityTrackerEntry);
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
    public boolean isAttackable() {
        return true;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return true;
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (pickaxeHitTimer > 0) {
            pickaxeHitTimer--;
            if (pickaxeHitTimer == 0) {
                pickaxeHitCount = 0; // Reset hit counter if timer runs out
            }
        }
        if (!setBaseY) {
            this.setPosition(this.getX(), this.getY(), this.getZ());
            baseY = this.getY();
            setBaseY = true;
        }
        if (lifeTime <= 140 && !getWorld().isClient()) {
            double yOffset = 0;

            if (lifeTime <= 20) {
                // Rising: 0 → 2 over 5 ticks
                yOffset = 2 * (lifeTime / 20.0);
            } else if (lifeTime <= 120) {
                // Pause at top for 100 ticks or 5 seconds
                yOffset = 2;
                if (getCreeperPillarType() == CreeperPillarType.EXPLOSIVE && !hasExploded && lifeTime == 60 && !this.getWorld().isClient()) {
                    this.getWorld().createExplosion(this, Explosion.createDamageSource(this.getWorld(), this), null, this.getX(), this.getBodyY(0.0625), this.getZ(), 1.5F, true, World.ExplosionSourceType.BLOCK);
                    hasExploded = true;
                    this.discard();
                }
            } else {
                // Falling: 2 → 0 over 10 ticks
                yOffset = 2*(2.0 - (double) (lifeTime - 100) / 20);
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
    public boolean damage(DamageSource source, float amount) {
        if (this.isRemoved()) return false;
        if (getWorld() instanceof ServerWorld serverWorld) {
            if (source.getAttacker() instanceof PlayerEntity player) {
                if (source.getWeaponStack() != null) {
                    if (source.getWeaponStack().isIn(ItemTags.PICKAXES)) {
                        pickaxeHitCount++;
                        pickaxeHitTimer = 40;

                        if (!player.isCreative()) {
                            source.getWeaponStack().damage(1, player, EquipmentSlot.MAINHAND);
                        }
                        if (pickaxeHitCount >= 3) {
                            lifeTime = 120;
                            return true;
                        }

                        return true; // cancel regular damage processing
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(PILLAR_TYPE, CreeperPillarType.NORMAL.name());
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("PillarType", getCreeperPillarType().name());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("PillarType")) {
            setCreeperPillarType(CreeperPillarType.valueOf(nbt.getString("PillarType")));
        }
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

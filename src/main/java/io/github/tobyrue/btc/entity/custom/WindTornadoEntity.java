package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WindTornadoEntity extends Entity {
    private int maxAge;

    private LivingEntity user;

    public WindTornadoEntity(EntityType<?> type, World world) {
        super(type, world);
        this.maxAge = 320;
        user = null;
    }

    public WindTornadoEntity(LivingEntity user, World world, double x, double y, double z, int maxAge) {
        super(ModEntities.WIND_TORNADO, world);
        this.setPosition(x, y, z);
        this.maxAge = maxAge;
        this.user = user;
    }

    public void setUser(LivingEntity user) {
        this.user = user;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
    }

    @Override
    public void tick() {
        super.tick();

        World world = this.getWorld();


        // Slow falling gravity
        Vec3d velocity = this.getVelocity();
        if (!this.isOnGround()) {
            this.setVelocity(velocity.x, Math.max(velocity.y - 0.02, -0.1), velocity.z);
        }

        // Slight drifting motion
        double driftX = (random.nextDouble() - 0.5) * 0.1;
        double driftZ = (random.nextDouble() - 0.5) * 0.1;
        this.setVelocity(this.getVelocity().add(driftX, 0, driftZ));
        this.move(MovementType.SELF, this.getVelocity());

        // Ambient swirl particles (low amount)
        if (world.getTime() % 2 == 0) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(ParticleTypes.CLOUD,
                        getX() + (random.nextDouble() - 0.5) * 1.5,
                        getY() + random.nextDouble() * 2,
                        getZ() + (random.nextDouble() - 0.5) * 1.5,
                        0, 0.01, 0);
            }
        }

        // Pull nearby mobs
        if (!world.isClient) {
            Box pullBox = this.getBoundingBox().expand(5.5);
//        List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class, pullBox, e -> {
//            if (e == this || !e.isAlive()) return false;
//            if (e == user) return false;
//
//            // Skip tamed pets owned by the user
//            if (e instanceof TameableEntity tameable) {
//                if (tameable.isTamed() && tameable.getOwner() == user) {
//                    return false;
//                }
//            }
//
//            // Skip creative/spectator players
//            if (e instanceof PlayerEntity player) {
//                return !player.isCreative() && !player.isSpectator();
//            }
//
//            return true;
//        });
//        List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class, pullBox, e ->
//                e != (Entity) this &&
//                        e.isAlive() &&
//                        (e != user) &&
//                        (!(e instanceof PlayerEntity) || (!((PlayerEntity) e).isCreative() && !((PlayerEntity) e).isSpectator()))
//        );
            //TODO
            List<LivingEntity> nearby = world.getEntitiesByClass(LivingEntity.class, pullBox, e -> {

                if (e == (Entity) this || !e.isAlive()) return false;

                if (user != null) {
                    if (e.equals(user)) return false;

                    // Skip tamed pets of user
                    if (e instanceof TameableEntity tameable && tameable.isTamed()) {
                        if (tameable.getOwner() == user) return false;
                    }

                    // Skip creative or spectator players
                    return !(e instanceof PlayerEntity player) || (!player.isCreative() && !player.isSpectator());
                }

                return !(e instanceof PlayerEntity player) || (!player.isCreative() && !player.isSpectator());
            });

            for (LivingEntity entity : nearby) {
                Vec3d direction = this.getPos().subtract(entity.getPos()).normalize().multiply(0.15);
                Vec3d upward = new Vec3d(0, 0.1, 0);
                entity.addVelocity(direction.x, direction.y + upward.y, direction.z);
                entity.velocityModified = true;

                world.addParticle(ParticleTypes.CLOUD,
                        entity.getX() + (random.nextDouble() - 0.5),
                        entity.getY() + random.nextDouble() * 1.5,
                        entity.getZ() + (random.nextDouble() - 0.5),
                        0, 0.01, 0);

                if (entity.horizontalCollision && entity.getVelocity().length() > 0.1) {
                    float damage = (float) (4.0 + Math.round(entity.getVelocity().length()) * 2);
                    entity.damage(entity.getDamageSources().inWall(), damage);
                }
            }
        }


        if (++age >= maxAge) {
            for (int i = 0; i < 60; i++) {
                double dx = (random.nextDouble() - 0.5) * 2;
                double dy = (random.nextDouble() - 0.5) * 2;
                double dz = (random.nextDouble() - 0.5) * 2;
                world.addParticle(ParticleTypes.POOF,
                        getX(), getY() + 0.5, getZ(),
                        dx, dy, dz);
            }

            discard(); // Remove the entity
        }
    }

}

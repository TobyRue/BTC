package io.github.tobyrue.btc.entity.custom;

import io.github.tobyrue.btc.entity.ModEntities;
import io.github.tobyrue.btc.item.ModItems;
import net.minecraft.client.render.entity.model.WindChargeEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;
import java.util.function.Function;

public class WaterBlastEntity extends ProjectileEntity {
//    private static final ExplosionBehavior EXPLOSION_BEHAVIOR;
//    private static final float EXPLOSION_POWER = 1.2F;
//    private int deflectCooldown = 5;
//    private float rotation = 0f;

    public WaterBlastEntity(EntityType<? extends WaterBlastEntity> entityType, World world) {
        super(entityType, world);
    }

//    public WaterBlastEntity(World world, ProjectileEntity user) {
//        super(ModEntities.WATER_BLAST, world);
//    }


    public WaterBlastEntity(PlayerEntity user, World world, double x, double y, double z, Vec3d velocity) {
        super(ModEntities.WATER_BLAST, world);
        this.updatePosition(x, y, z);
        this.setVelocity(velocity);
    }


    //    public WaterBlastEntity(World world, double x, double y, double z, Vec3d velocity) {
//        super(ModEntities.WATER_BLAST, world);
//        this.setVelocity(velocity);
//    }
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

//    @Override
//    public void tick() {
//        super.tick();
//
//        // Keep rotating the entity
//        rotation += 0.5f;  // Adjust the increment to change the rotation speed
//        if (rotation >= 360) {
//            rotation = 0;
//        }
//
//        // Deflect cooldown countdown
//        if (this.deflectCooldown > 0) {
//            --this.deflectCooldown;
//        }
//    }
//
//    // Get the current rotation of the entity for rendering
//    public float getRenderingRotation() {
//        return rotation;
//    }
//
//    // Handle deflection for projectiles
//    @Override
//    public boolean deflect(ProjectileDeflection deflection, Entity deflector, Entity owner, boolean fromAttack) {
//        if (this.deflectCooldown > 0) {
//            return false;
//        }
//        return super.deflect(deflection, deflector, owner, fromAttack);
//    }
//
//    // Create the wind charge explosion effect when triggered
//    protected void createExplosion(Vec3d pos) {
//        // Create explosion-like effect in the world
//        this.getWorld().createExplosion(
//                this,
//                null,  // No damage source
//                EXPLOSION_BEHAVIOR,
//                pos.getX(), pos.getY(), pos.getZ(),
//                EXPLOSION_POWER,
//                false,
//                World.ExplosionSourceType.TRIGGER,
//                ParticleTypes.GUST_EMITTER_SMALL,
//                ParticleTypes.GUST_EMITTER_LARGE,
//                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
//        );
//    }
//
//    // Initialize the explosion behavior
//    static {
//        EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
//                true,  // Causes block destruction
//                false, // Does not cause fires
//                Optional.of(1.22F), // Explosion power
//                Registries.BLOCK.getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
//        );
//    }
}

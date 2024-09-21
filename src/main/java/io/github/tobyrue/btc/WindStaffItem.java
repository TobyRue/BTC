package io.github.tobyrue.btc;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WindChargeItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class WindStaffItem extends Item {
    // Configurable pull range (in blocks)
    private static final double PULL_RADIUS = 25.0;
    private static final double SHOOT_RADIUS = 15.0;
    private static final double PULL_STRENGTH = 3.0;
    private static final double SHOOT_STRENGTH = 7.0;
    private static final int PROJECTILE_COUNT = 7; // Number of projectiles to shoot
    private static final float SPREAD_ANGLE = 0.5f;

    public WindStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!world.isClient && user.isSneaking()) {
            // Pull mobs towards the user immediately
            pullMobsTowardsPlayer(world, user);
            user.getItemCooldownManager().set(this, 40);
            return TypedActionResult.success(itemStack);
            // Schedule shooting mobs away after a delay (e.g., 100 ticks = 5 seconds)
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed()) {
            // Push mobs away from the user (Left Alt + Right-Click)
            shootMobsAway(user, world);
            user.getItemCooldownManager().set(this, 80);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed() && user.isSneaking()) {
            // Push mobs away from the user (Left Alt + Right-Click)
            shootMobsAway(user, world);
            user.getItemCooldownManager().set(this, 80);
            return TypedActionResult.success(itemStack);
        } else {
            if (!world.isClient) {
                shootWindCharges(user, world);
                return TypedActionResult.success(itemStack);
            }
        }


        return TypedActionResult.fail(itemStack);
    }
    private void shootWindCharges(PlayerEntity user, World world) {
        Vec3d baseDirection = user.getRotationVec(1.0f).normalize(); // Get the player's facing direction

        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            // Create a new WindChargeEntity
            WindChargeEntity windCharge = new WindChargeEntity(user, world, user.getX(), user.getY() + 1.0, user.getZ());

            // Calculate a random angle offset for scattering
            double angleOffset = (Math.random() - 0.5) * SPREAD_ANGLE; // Random angle offset
            double scatterX = baseDirection.x + Math.cos(angleOffset) * 0.5; // Modify X
            double scatterZ = baseDirection.z + Math.sin(angleOffset) * 0.5; // Modify Z

            // Create scatter direction and normalize it
            Vec3d scatterDirection = new Vec3d(scatterX, baseDirection.y, scatterZ).normalize();

            // Set the spawn position slightly in front of the player
            Vec3d spawnPosition = user.getPos().add(baseDirection.multiply(1)).add(0, 1, 0);
            windCharge.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            // Set the velocity of the wind charge
            windCharge.setVelocity(scatterDirection.multiply(2.0)); // Set speed of the wind charge
            world.spawnEntity(windCharge);
        }
    }
    private void pullMobsTowardsPlayer(World world, PlayerEntity user) {
        // Use the PULL_RADIUS variable to define the radius
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(PULL_RADIUS), entity -> entity != user);
        // Log the number of entities found
        // Pull all mobs towards the user
        for (LivingEntity entity : entities) {
            // Calculate the direction towards the user
            double dx = user.getX() - entity.getX();
            double dy = user.getY() - entity.getY();
            double dz = user.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double strength = PULL_STRENGTH;
            if (distance != 0) {
                entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
            }
        }
    }

    private void shootMobsAway(PlayerEntity user, World world) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(SHOOT_RADIUS), entity -> entity != user);

        // Shoot all mobs away from the user
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - user.getX();
            double dy = entity.getY() - user.getY();
            double dz = entity.getZ() - user.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Apply velocity away from the user
            double strength = SHOOT_STRENGTH;
            if (distance != 0) {
                entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
                System.out.println("Shooting entity away: " + entity.getName().getString());
            }

            // Optionally, deal damage to the entity
            if (entity instanceof PlayerEntity) {
                entity.damage(ModDamageTypes.of(world, ModDamageTypes.WIND_BURST), 5.0f);
            } else {
                entity.damage(world.getDamageSources().flyIntoWall(), 5);
            }
        }
    }
}

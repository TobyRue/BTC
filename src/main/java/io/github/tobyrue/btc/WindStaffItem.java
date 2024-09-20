package io.github.tobyrue.btc;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class WindStaffItem extends Item {
    // Configurable pull range (in blocks)
    private static final double PULL_RADIUS = 25.0;
    private static final double SHOOT_RADIUS = 15.0;
    private static final double PULL_STRENGTH = 3.0;
    private static final double SHOOT_STRENGTH = 7.0;

    public WindStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (!world.isClient && player.isSneaking()) {
            // Pull mobs towards the player immediately
            pullMobsTowardsPlayer(world, player);
            return TypedActionResult.success(itemStack);
            // Schedule shooting mobs away after a delay (e.g., 100 ticks = 5 seconds)
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed()) {
            // Push mobs away from the player (Left Alt + Right-Click)
            shootMobsAway(player, world);
        }

        return TypedActionResult.fail(itemStack);
    }

    private void pullMobsTowardsPlayer(World world, PlayerEntity player) {
        // Use the PULL_RADIUS variable to define the radius
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(PULL_RADIUS), entity -> entity != player);
        // Log the number of entities found
        // Pull all mobs towards the player
        for (LivingEntity entity : entities) {
            // Calculate the direction towards the player
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double dz = player.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double strength = PULL_STRENGTH;
            if (distance != 0) {
                entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
            }
        }
    }
    private void shootMobsAway(PlayerEntity player, World world) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(SHOOT_RADIUS), entity -> entity != player);

        // Shoot all mobs away from the player
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - player.getX();
            double dy = entity.getY() - player.getY();
            double dz = entity.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Apply velocity away from the player
            double strength = SHOOT_STRENGTH;
            if (distance != 0) {
                entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
                System.out.println("Shooting entity away: " + entity.getName().getString());
            }

            // Optionally, deal damage to the entity
            entity.damage(world.getDamageSources().flyIntoWall(), 10);
        }
    }
}

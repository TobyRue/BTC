package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.ModDamageTypes;
import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

    public WindStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && !user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Push mobs away from the user (Tilda + Right-Click)
            // Create and shoot the WindChargeEntity
            WindChargeEntity windCharge = new WindChargeEntity(user, world, user.getX(), user.getY() + 1.0, user.getZ());
            Vec3d direction = user.getRotationVec(1.0f);
            windCharge.setVelocity(direction.multiply(1.5)); // Adjust speed as needed
            user.getItemCooldownManager().set(this, 10);
            world.spawnEntity(windCharge);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Pull mobs towards the user immediately
            shootWindCharges(user, world);
            user.getItemCooldownManager().set(this, 20);
            return TypedActionResult.success(itemStack);
            // Schedule shooting mobs away after a delay (e.g., 100 ticks = 5 seconds)
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Push mobs away from the user (Left Alt + Right-Click)
            pullMobsTowardsPlayer(world, user);
            user.getItemCooldownManager().set(this, 40);
            return TypedActionResult.success(itemStack);
        } else if(!world.isClient && BTCClient.tildeKeyBinding.isPressed() && !BTCClient.leftAltKeyBinding.isPressed()){
            shootMobsAway(user, world);
            user.getItemCooldownManager().set(this, 80);
            return TypedActionResult.success(itemStack);

        }


        return TypedActionResult.fail(itemStack);
    }
    private void shootWindCharges(PlayerEntity user, World world) {
        Vec3d baseDirection = user.getRotationVec(1.0f).normalize(); // Get the player's facing direction
        double spreadFactor = 0.2; // Controls how much deviation there is from the forward direction

        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            // Create a new WindChargeEntity
            WindChargeEntity windCharge = new WindChargeEntity(user, world, user.getX(), user.getY() + 1.0, user.getZ());

            // Apply random spread within a controlled cone
            double randomPitch = (Math.random() - 0.5) * spreadFactor;
            double randomYaw = (Math.random() - 0.5) * spreadFactor;

            // Calculate the scattered direction by modifying the player's original facing direction
            Vec3d scatterDirection = baseDirection.add(randomYaw, randomPitch, randomYaw).normalize();

            // Set the spawn position slightly in front of the player
            Vec3d spawnPosition = user.getPos().add(baseDirection.multiply(1.5)).add(0, 1, 0);
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
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.getDescription1().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
        tooltip.add(this.getDescription2().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription3().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
        tooltip.add(this.getDescription4().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription5().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
        tooltip.add(this.getDescription6().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription7().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.DARK_AQUA));
        tooltip.add(this.getDescription8().formatted(Formatting.WHITE));
        // Add custom tooltip text
    }
    public MutableText getDescription1() {
        return Text.literal("Right Click:");
    }
    public MutableText getDescription2() {
        return Text.literal("Wind Charge");
    }
    public MutableText getDescription3() {
        return Text.literal("Shift Right Click:");
    }
    public MutableText getDescription4() {
        return Text.literal("Cluster Shot of Wind Charges");
    }
    public MutableText getDescription5() {
        return Text.literal("Alt Right Click:");
    }
    public MutableText getDescription6() {
        return Text.literal("Pull Nearby Mobs Towards You");
    }
    public MutableText getDescription7() {
        return Text.literal("Tilde Right Click:");
    }
    public MutableText getDescription8() {
        return Text.literal("Shoot Nearby Entities Away Dealing Damage");
    }
}

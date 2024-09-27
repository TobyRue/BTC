package io.github.tobyrue.btc;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DragonStaffItem extends Item {
    public DragonStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && !user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Create and spawn an Ender Pearl entity
            EnderPearlEntity enderPearl = new EnderPearlEntity(world, user);
            enderPearl.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

            // Spawn the ender pearl in the world
            world.spawnEntity(enderPearl);

            // Apply cooldown to the staff
            user.getItemCooldownManager().set(this, 30);

            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Get the player's facing direction
            Vec3d direction = user.getRotationVec(1.0F).normalize(); // Normalized direction vector

            // Create a new FireballEntity with the correct constructor
            DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, user, direction); // 1 is the explosion power

            // Set the position of the fireball slightly in front of the player
            dragonFireballEntity.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            // Set the fireball velocity (you can adjust the speed multiplier here)
            dragonFireballEntity.setVelocity(direction.multiply(1.5));

            // Spawn the fireball in the world
            world.spawnEntity(dragonFireballEntity);

            // Apply a cooldown to the staff to prevent spamming
            user.getItemCooldownManager().set(this, 100);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed() && !user.isSneaking()) {
            applyLifesteal(world, user, 10);
            user.getItemCooldownManager().set(this, 20);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.tildeKeyBinding.isPressed() && !BTCClient.leftAltKeyBinding.isPressed() && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 200, 0));
            user.getItemCooldownManager().set(this, 160);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.tildeKeyBinding.isPressed() && BTCClient.leftAltKeyBinding.isPressed() && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 600, 2));
            user.getItemCooldownManager().set(this, 640);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.tildeKeyBinding.isPressed() && BTCClient.leftAltKeyBinding.isPressed() && user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 1200, 4));
            user.getItemCooldownManager().set(this, 1200);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }
    private void applyLifesteal(World world, PlayerEntity player, double radius) {
        // Get all entities within the radius
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                new Box(player.getBlockPos()).expand(radius),
                entity -> entity != player && entity.isAlive());

        // Define the percentage of health to take (e.g., 10% = 0.10 or 5% = 0.05)
        float healthPercentage = 0.10f; // 10% of target's health

        // Iterate through the targets and apply damage/heal
        for (LivingEntity target : targets) {
            // Calculate the damage based on the target's current health
            float targetHealth = target.getHealth();
            float damage = targetHealth * healthPercentage; // Deal 10% of the target's current health

            // Deal damage to the target
            target.damage(world.getDamageSources().magic(), damage);

            // Heal the player for a percentage of the damage dealt
            float healAmount = damage * 0.5f; // Heal for 50% of damage dealt
            player.heal(healAmount);
        }

        // Optional: Play a sound or particle effect
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.getDescription1().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription2().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription3().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription4().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription5().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription6().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription7().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription8().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription9().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription10().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription11().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.BLUE));
        tooltip.add(this.getDescription12().formatted(Formatting.WHITE));
        // Add custom tooltip text
    }
    public MutableText getDescription1() {
        return Text.literal("Right Click:");
    }
    public MutableText getDescription2() {
        return Text.literal("Ender Pearl");
    }
    public MutableText getDescription3() {
        return Text.literal("Shift Right Click:");
    }
    public MutableText getDescription4() {
        return Text.literal("Dragons Breath");
    }
    public MutableText getDescription5() {
        return Text.literal("Alt Right Click:");
    }
    public MutableText getDescription6() {
        return Text.literal("Life Steal 10% of Nearby Mobs Health");
    }
    public MutableText getDescription7() {
        return Text.literal("Tilde Right Click:");
    }
    public MutableText getDescription8() {
        return Text.literal("Dragon Scales for 10 Seconds at Level 1");
    }
    public MutableText getDescription9() {
        return Text.literal("Tilde, Left Alt, Right Click:");
    }
    public MutableText getDescription10() {
        return Text.literal("Dragon Scales for 30 Seconds at Level 3");
    }
    public MutableText getDescription11() {
        return Text.literal("Tilde, Left Alt, Shift Right Click:");
    }
    public MutableText getDescription12() {
        return Text.literal("Dragon Scales for 60 Seconds at Level 5");
    }
}
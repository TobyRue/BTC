package io.github.tobyrue.btc;

import io.github.tobyrue.btc.client.BTCClient;
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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
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
            user.getItemCooldownManager().set(this, 100);

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
            user.getItemCooldownManager().set(this, 30); // Adjust the cooldown as necessary
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 100, 0));
            user.getItemCooldownManager().set(this, 100);
            user.setOnFireForTicks(100);
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && BTCClient.tildeKeyBinding.isPressed() && !BTCClient.leftAltKeyBinding.isPressed()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
            user.getItemCooldownManager().set(this, 600);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);

        // Add custom tooltip text
        tooltip.add(Text.of("Unleash the power of the dragon!"));// Plain text
    }
}
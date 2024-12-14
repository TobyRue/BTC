package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
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

import java.util.List;

public class FireStaffItem extends StaffItem {
    public FireStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (!world.isClient && !user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Get the player's facing direction
            Vec3d direction = user.getRotationVec(1.0F).normalize(); // Normalized direction vector

            // Create a new FireballEntity with the correct constructor
            FireballEntity fireball = new FireballEntity(world, user, direction, 1); // 1 is the explosion power

            // Set the position of the fireball slightly in front of the player
            fireball.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            // Set the fireball velocity (you can adjust the speed multiplier here)
            fireball.setVelocity(direction.multiply(1.5));

            // Spawn the fireball in the world
            world.spawnEntity(fireball);

            // Apply a cooldown to the staff to prevent spamming
            user.getItemCooldownManager().set(this, 30); // Adjust the cooldown as necessary
            return TypedActionResult.success(itemStack);
        } else if (!world.isClient && user.isSneaking() && !BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            // Get the player's facing direction
            Vec3d direction = user.getRotationVec(1.0F).normalize(); // Normalized direction vector

            // Create a new FireballEntity with the correct constructor
            FireballEntity fireball = new FireballEntity(world, user, direction, 5); // 1 is the explosion power

            // Set the position of the fireball slightly in front of the player
            fireball.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            // Set the fireball velocity (you can adjust the speed multiplier here)
            fireball.setVelocity(direction.multiply(1.5));

            // Spawn the fireball in the world
            world.spawnEntity(fireball);

            // Apply a cooldown to the staff to prevent spamming
            user.getItemCooldownManager().set(this, 100); // Adjust the cooldown as necessary
            return TypedActionResult.success(itemStack);
            // Schedule shooting mobs away after a delay (e.g., 100 ticks = 5 seconds)
        }else if (!world.isClient && BTCClient.leftAltKeyBinding.isPressed() && !BTCClient.tildeKeyBinding.isPressed()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
            user.getItemCooldownManager().set(this, 100);
            user.setOnFireForTicks(100);
            return TypedActionResult.success(itemStack);
        } else if(!world.isClient && BTCClient.tildeKeyBinding.isPressed() && !BTCClient.leftAltKeyBinding.isPressed()){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
            user.getItemCooldownManager().set(this, 600);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.getDescription1().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.RED));
        tooltip.add(this.getDescription2().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription3().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.RED));
        tooltip.add(this.getDescription4().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription5().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.RED));
        tooltip.add(this.getDescription6().formatted(Formatting.WHITE));
        tooltip.add(this.getDescription7().formatted(Formatting.ITALIC, Formatting.BOLD, Formatting.RED));
        tooltip.add(this.getDescription8().formatted(Formatting.WHITE));
        // Add custom tooltip text
    }
    public MutableText getDescription1() {
        return Text.literal("Right Click:");
    }
    public MutableText getDescription2() {
        return Text.literal("Weak Fire Ball");
    }
    public MutableText getDescription3() {
        return Text.literal("Shift Right Click:");
    }
    public MutableText getDescription4() {
        return Text.literal("Strong Fireball Attack");
    }
    public MutableText getDescription5() {
        return Text.literal("Alt Right Click:");
    }
    public MutableText getDescription6() {
        return Text.literal("Strength 2 for 7 Seconds");
    }
    public MutableText getDescription7() {
        return Text.literal("Tilde Right Click:");
    }
    public MutableText getDescription8() {
        return Text.literal("Resistance 3 for 5 Seconds");
    }
}
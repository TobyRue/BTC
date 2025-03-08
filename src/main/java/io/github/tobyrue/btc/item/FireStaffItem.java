package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class FireStaffItem extends StaffItem {
    private static final List<String> ATTACKS = List.of("Weak Fire Ball", "Strong Fireball", "Strength", "Resistance");

    public FireStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        ItemStack itemStack = user.getStackInHand(hand);
        String currentElement = getElement(stack);
        int nextIndex = (ATTACKS.indexOf(currentElement) + 1) % ATTACKS.size();
        String nextElement = ATTACKS.get(nextIndex);
        if (!world.isClient && user.isSneaking()) {
            user.sendMessage(Text.literal("Blaze Staff set to - " + nextElement), true);
            setElement(stack, nextElement);
            return TypedActionResult.success(stack);
        }
        if (getElement(stack).equals("Weak Fire Ball") && !user.isSneaking()) {
            Vec3d direction = user.getRotationVec(1.0F).normalize(); // Normalized direction vector

            FireballEntity fireball = new FireballEntity(world, user, direction, 1); // 1 is the explosion power

            fireball.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            fireball.setVelocity(direction.multiply(1.5));

            world.spawnEntity(fireball);

            user.getItemCooldownManager().set(this, 30);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Strong Fireball") && !user.isSneaking()) {
            Vec3d direction = user.getRotationVec(1.0F).normalize();

            FireballEntity fireball = new FireballEntity(world, user, direction, 5); // 1 is the explosion power

            fireball.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            fireball.setVelocity(direction.multiply(1.5));

            world.spawnEntity(fireball);

            user.getItemCooldownManager().set(this, 100);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Strength") && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
            user.getItemCooldownManager().set(this, 100);
            user.setOnFireForTicks(100);
            return TypedActionResult.success(itemStack);
        } else if(getElement(stack).equals("Resistance") && !user.isSneaking()){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
            user.getItemCooldownManager().set(this, 600);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.currentAttack(stack).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.RED));
        tooltip.add(this.attack1().formatted(Formatting.WHITE));
        tooltip.add(this.attack2().formatted(Formatting.WHITE));
        tooltip.add(this.attack3().formatted(Formatting.WHITE));
        tooltip.add(this.attack4().formatted(Formatting.WHITE));
    }

    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}
    public MutableText attack1() {return Text.literal("Weak Fire Ball");}
    public MutableText attack2() {return Text.literal("Strong Fireball");}
    public MutableText attack3() {return Text.literal("Strength - Lvl 2 for 7 Seconds");}
    public MutableText attack4() {return Text.literal("Resistance - Lvl 3 for 5 Seconds");}

    private String getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        return nbt.contains("Attack") ? nbt.getString("Attack") : ATTACKS.get(0);
    }

    private void setElement(ItemStack stack, String attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt(); // Get a modifiable copy
        nbt.putString("Attack", attack); // Update the element
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt)); // Create a new immutable NbtComponent
    }
}
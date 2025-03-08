package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.client.BTCClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class DragonStaffItem extends StaffItem {
    private static final List<String> ATTACKS = List.of("Ender Pearl", "Dragon Breath", "Life Steal", "Dragon Scales - 1", "Dragon Scales - 3", "Dragon Scales - 5");

    public DragonStaffItem(Settings settings) {
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
            user.sendMessage(Text.literal("Dragonhearted Staff set to - " + nextElement), true);
            setElement(stack, nextElement);
            return TypedActionResult.success(stack);
        }
        if (getElement(stack).equals("Ender Pearl") && !user.isSneaking()) {
            // Create and spawn an Ender Pearl entity
            EnderPearlEntity enderPearl = new EnderPearlEntity(world, user);
            enderPearl.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.5F, 1.0F);

            world.spawnEntity(enderPearl);

            user.getItemCooldownManager().set(this, 30);

            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Dragon Breath") && !user.isSneaking()) {
            Vec3d direction = user.getRotationVec(1.0F).normalize(); // Normalized direction vector

            DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, user, direction); // 1 is the explosion power

            dragonFireballEntity.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

            // Set the fireball velocity (you can adjust the speed multiplier here)
            dragonFireballEntity.setVelocity(direction.multiply(1.5));

            world.spawnEntity(dragonFireballEntity);

            user.getItemCooldownManager().set(this, 100);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Life Steal") && !user.isSneaking()) {
            applyLifesteal(world, user, 10);
            user.getItemCooldownManager().set(this, 20);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Dragon Scales - 1") && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 200, 0));
            user.getItemCooldownManager().set(this, 160);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Dragon Scales - 3") && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 600, 2));
            user.getItemCooldownManager().set(this, 640);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Dragon Scales - 5") && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(Registries.STATUS_EFFECT.getEntry(BTC.DRAGON_SCALES), 1200, 4));
            user.getItemCooldownManager().set(this, 1200);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }
    private void applyLifesteal(World world, PlayerEntity player, double radius) {
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class,
                new Box(player.getBlockPos()).expand(radius),
                entity -> entity != player && entity.isAlive());

        // Define the percentage of health to take (e.g., 10% = 0.10 or 5% = 0.05)
        float healthPercentage = 0.10f;

        for (LivingEntity target : targets) {
            float targetHealth = target.getHealth();
            float damage = targetHealth * healthPercentage;

            target.damage(world.getDamageSources().magic(), damage);

            float healAmount = damage * 0.5f; // Heal for 50% of damage dealt
            player.heal(healAmount);
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.currentAttack(stack).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.DARK_PURPLE));
        tooltip.add(this.attack1().formatted(Formatting.WHITE));
        tooltip.add(this.attack2().formatted(Formatting.WHITE));
        tooltip.add(this.attack3().formatted(Formatting.WHITE));
        tooltip.add(this.attack4().formatted(Formatting.WHITE));
        tooltip.add(this.attack5().formatted(Formatting.WHITE));
        tooltip.add(this.attack6().formatted(Formatting.WHITE));
    }

    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}
    public MutableText attack1() {return Text.literal("Ender Pearl - Throw a Ender Pearl and Teleport");}
    public MutableText attack2() {return Text.literal("Dragon Breath - Shoot a Dragon Fireball");}
    public MutableText attack3() {return Text.literal("Life Steal - Steal 10% of Nearby Mobs Health Only Gaining 5%");}
    public MutableText attack4() {return Text.literal("Dragon Scales - 1 - for 10 Seconds at Level 1");}
    public MutableText attack5() {return Text.literal("Dragon Scales - 3 - for 30 Seconds at Level 3");}
    public MutableText attack6() {return Text.literal("Dragon Scales - 5 - for 60 Seconds at Level 5");}


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
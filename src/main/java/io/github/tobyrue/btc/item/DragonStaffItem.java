package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.BTC;
import io.github.tobyrue.btc.CooldownProvider;
import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.enums.DragonStaffAttacks;
import io.github.tobyrue.btc.enums.FireStaffAttacks;
import io.github.tobyrue.btc.enums.WindStaffAttacks;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
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
import net.minecraft.stat.Stats;
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

    public DragonStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x9E00ED;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        DragonStaffAttacks current = getElement(stack);
        DragonStaffAttacks next = DragonStaffAttacks.next(current);
        String cooldownKey = current.getCooldownKey();

        if (!player.isSneaking()) {
            switch (current) {
                case ENDER_PEARL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        EnderPearlEntity enderPearl = new EnderPearlEntity(world, player);
                        enderPearl.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 1.0F);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        world.spawnEntity(enderPearl);
                        setCooldown(player, stack, cooldownKey, 40, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case DRAGONS_BREATH -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        Vec3d direction = player.getRotationVec(1.0F).normalize(); // Normalized direction vector

                        DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, player, direction); // 1 is the explosion power

                        dragonFireballEntity.setPos(player.getX() + direction.x * 1.5, player.getY() + 1.5, player.getZ() + direction.z * 1.5);

                        // Set the fireball velocity (you can adjust the speed multiplier here)
                        dragonFireballEntity.setVelocity(direction.multiply(1.5));

                        world.spawnEntity(dragonFireballEntity);

                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 400, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case LIFE_STEAL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        applyLifesteal(world, player, 10);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 20, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case DRAGON_SCALES_1 -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DRAGON_SCALES, 200, 0));
                        setCooldown(player, stack, cooldownKey, 600, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case DRAGON_SCALES_3 -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DRAGON_SCALES, 600, 2));
                        setCooldown(player, stack, cooldownKey, 800, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case DRAGON_SCALES_5 -> {
                    player.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.DRAGON_SCALES, 600, 4));
                        setCooldown(player, stack, cooldownKey, 1000, true);
                        return TypedActionResult.success(stack);
                    }
                }
            }
        }
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.dragon.set", Text.translatable("item.btc.spell.dragon." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        DragonStaffAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.dragon." + current.asString())).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.DARK_PURPLE));
    }


    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}
    public MutableText attack1() {return Text.literal("Ender Pearl - Throw a Ender Pearl and Teleport");}
    public MutableText attack2() {return Text.literal("Dragon Breath - Shoot a Dragon Fireball");}
    public MutableText attack3() {return Text.literal("Life Steal - Steal 10% of Nearby Mobs Health Only Gaining 5%");}
    public MutableText attack4() {return Text.literal("Dragon Scales - 1 - for 10 Seconds at Level 1");}
    public MutableText attack5() {return Text.literal("Dragon Scales - 3 - for 30 Seconds at Level 3");}
    public MutableText attack6() {return Text.literal("Dragon Scales - 5 - for 60 Seconds at Level 5");}


    private DragonStaffAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (DragonStaffAttacks attack : DragonStaffAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return DragonStaffAttacks.ENDER_PEARL;
    }

    private void setElement(ItemStack stack, DragonStaffAttacks attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        nbt.putString("Element", attack.asString());

        // Manage cooldown bar visibility on element swap
        NbtCompound cooldowns = nbt.getCompound("Cooldowns");
        String activeKey = attack.getCooldownKey();

        boolean found = false;
        for (String key : cooldowns.getKeys()) {
            NbtCompound entry = cooldowns.getCompound(key);
            if (key.equals(activeKey)) {
                entry.putBoolean("visible", true);
                found = true;
            } else {
                entry.remove("visible");
            }
            cooldowns.put(key, entry);
        }

        // If no active cooldown for new element, hide all bars
        if (!found) {
            for (String key : cooldowns.getKeys()) {
                NbtCompound entry = cooldowns.getCompound(key);
                entry.remove("visible");
                cooldowns.put(key, entry);
            }
        }

        nbt.put("Cooldowns", cooldowns);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
}
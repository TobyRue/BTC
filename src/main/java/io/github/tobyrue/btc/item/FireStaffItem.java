package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
import io.github.tobyrue.btc.enums.EarthStaffAttacks;
import io.github.tobyrue.btc.enums.FireStaffAttacks;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
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
    private static final List<String> ATTACKS = List.of("Weak Fire Ball", "Strong Fireball", "Concentrated Fire Storm", "Fire Storm", "Strength", "Resistance");
    private static final double FIRE_RADIUS = 10.0;
    private static final double FIRE_TIME_CHANGE = 10.0;
    private static final double MIN_TIME = 2.0;
    private static final double FIRE_TIME = FIRE_TIME_CHANGE + MIN_TIME;
    private static final double SMALL_FIRE_RADIUS = FIRE_RADIUS / 2;
    private static final double SMALL_MIN_TIME = 10;
    private static final double SMALL_FIRE_TIME = (FIRE_TIME_CHANGE * 2) + SMALL_MIN_TIME;
    private static final double EXTRA_COOLDOWN = 2;

    public FireStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        FireStaffAttacks current = getElement(stack);
        FireStaffAttacks next = FireStaffAttacks.next(current);

        if (!player.isSneaking()) {
            switch (current) {
                case WEAK_FIREBALL -> {
                    shootFireball(world, player, 1);
                    player.getItemCooldownManager().set(this, 30);
                    return TypedActionResult.success(stack);
                }
                case STRONG_FIREBALL -> {
                    shootFireball(world, player, 5);
                    player.getItemCooldownManager().set(this, 100);
                    return TypedActionResult.success(stack);
                }
                case CONCENTRATED_FIRE_STORM -> {
                    player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.FIRE_BURST, 8, 1));
                    return TypedActionResult.success(stack);
                }
                case FIRE_STORM -> {
                    player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.FIRE_BURST, 12, 3));
                    return TypedActionResult.success(stack);
                }
                case STRENGTH -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
                    return TypedActionResult.success(stack);
                }
                case RESISTANCE -> {
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
                    player.getItemCooldownManager().set(this, 600);
                    return TypedActionResult.success(stack);
                }
            }
        }
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.fire.set", Text.translatable("item.btc.spell.fire." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }
    private void shootFireball(World world, PlayerEntity player, int explosionPower) {
        Vec3d direction = player.getRotationVec(1.0F).normalize();

        FireballEntity fireball = new FireballEntity(world, player, direction, explosionPower);

        fireball.setPos(player.getX() + direction.x * 1.5, player.getY() + 1.5, player.getZ() + direction.z * 1.5);

        fireball.setVelocity(direction.multiply(1.5));

        world.spawnEntity(fireball);
    }
    private void setMobsOnFireHostile(World world, PlayerEntity player) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(FIRE_RADIUS),
                entity -> entity != player && entity instanceof HostileEntity); // Only affect hostile mobs
        for (LivingEntity entity : entities) {
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double dz = player.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (entities != null) {
                double timeNorm = (Math.abs(distance) * -1) + FIRE_TIME;
                entity.setOnFireFor((float) timeNorm);
                player.getItemCooldownManager().set(this, (int) ((FIRE_TIME + EXTRA_COOLDOWN) * 20));
            }
        }
    }
    private void setMobsOnFireSmall(World world, PlayerEntity player) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(SMALL_FIRE_RADIUS), entity -> entity != player);
        for (LivingEntity entity : entities) {
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double dz = player.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (entities != null) {
                double time1 = Math.pow(distance, 2);
                double time2 = Math.abs(time1) * -1;
                double time3 = time2 + SMALL_FIRE_TIME;
                entity.setOnFireFor((float) time3);
                player.getItemCooldownManager().set(this, (int) ((SMALL_FIRE_TIME + EXTRA_COOLDOWN) * 20));
            }
        }
    }

    private FireStaffAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (FireStaffAttacks attack : FireStaffAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return FireStaffAttacks.WEAK_FIREBALL;
    }

    private void setElement(ItemStack stack, FireStaffAttacks attack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        nbt.putString("Element", attack.asString());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        FireStaffAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.fire." + current.asString())).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.GRAY));
    }

    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}
    public MutableText attack1() {return Text.literal("Weak Fire Ball");}
    public MutableText attack2() {return Text.literal("Strong Fireball");}
    public MutableText attack3() {return Text.literal("Fire Storm - Set  Hostile Mobs on Fire Within a " +  FIRE_RADIUS + " Block Radius");}
    public MutableText attack3cont() {return Text.literal("  - With a Minimum of " + MIN_TIME + " Seconds of Burning and Max of " + FIRE_TIME);}
    public MutableText attack3cont2() {return Text.literal("  - Closer to a Hostile Entity the Longer the Burn Time Is");}
    public MutableText attack3small() {return Text.literal("Concentrated Fire Storm - Set All Mobs on Fire Within a " +  SMALL_FIRE_RADIUS + " Block Radius");}
    public MutableText attack3contsmall() {return Text.literal("  - With a Minimum of " + 5.0 + " Seconds of Burning and Max of " + SMALL_FIRE_TIME);}
    public MutableText attack3cont2small() {return Text.literal("  - Closer to a Entity the Longer the Burn Time Is");}
    public MutableText attack4() {return Text.literal("Strength - Lvl 2 for 7 Seconds");}
    public MutableText attack5() {return Text.literal("Resistance - Lvl 3 for 5 Seconds");}
}
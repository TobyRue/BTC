package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.client.BTCClient;
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
            shootFireball(world, user, 1);
            user.getItemCooldownManager().set(this, 30);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Strong Fireball") && !user.isSneaking()) {
            shootFireball(world, user, 5);
            user.getItemCooldownManager().set(this, 100);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Concentrated Fire Storm") && !user.isSneaking()) {
            setMobsOnFireSmall(world, user);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Fire Storm") && !user.isSneaking()) {
            setMobsOnFireHostile(world, user);
            return TypedActionResult.success(itemStack);
        } else if (getElement(stack).equals("Strength") && !user.isSneaking()) {
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
            user.setOnFireForTicks(100);
            return TypedActionResult.success(itemStack);
        } else if(getElement(stack).equals("Resistance") && !user.isSneaking()){
            user.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2));
            user.getItemCooldownManager().set(this, 600);
            return TypedActionResult.success(itemStack);
        }

        return TypedActionResult.fail(itemStack);
    }
    private void shootFireball(World world, PlayerEntity user, int explosionPower) {
        Vec3d direction = user.getRotationVec(1.0F).normalize();

        FireballEntity fireball = new FireballEntity(world, user, direction, explosionPower);

        fireball.setPos(user.getX() + direction.x * 1.5, user.getY() + 1.5, user.getZ() + direction.z * 1.5);

        fireball.setVelocity(direction.multiply(1.5));

        world.spawnEntity(fireball);
    }
    private void setMobsOnFireHostile(World world, PlayerEntity user) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(FIRE_RADIUS),
                entity -> entity != user && entity instanceof HostileEntity); // Only affect hostile mobs
        for (LivingEntity entity : entities) {
            double dx = user.getX() - entity.getX();
            double dy = user.getY() - entity.getY();
            double dz = user.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (entities != null) {
                double timeNorm = (Math.abs(distance) * -1) + FIRE_TIME;
                entity.setOnFireFor((float) timeNorm);
                user.getItemCooldownManager().set(this, (int) ((FIRE_TIME + EXTRA_COOLDOWN) * 20));
//                if (distance > 0) {
//                    double time = distance * -1;
//                    double time2 = time + FIRE_TIME;
//                    entity.setOnFireFor((float) time2);
//                    user.getItemCooldownManager().set(this, (int) ((FIRE_TIME + EXTRA_COOLDOWN) * 20));
//                } else if (distance < 0) {
//                    double time3 = distance + FIRE_TIME;
//                    entity.setOnFireFor((float) time3);
//                    user.getItemCooldownManager().set(this, (int) ((FIRE_TIME + EXTRA_COOLDOWN) * 20));
//                } else if (distance == 0) {
//                    entity.setOnFireFor((float) FIRE_TIME);
//                    user.getItemCooldownManager().set(this, (int) ((FIRE_TIME + EXTRA_COOLDOWN) * 20));
//                }
            }
        }
    }
    private void setMobsOnFireSmall(World world, PlayerEntity user) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(SMALL_FIRE_RADIUS), entity -> entity != user);

        for (LivingEntity entity : entities) {
            double dx = user.getX() - entity.getX();
            double dy = user.getY() - entity.getY();
            double dz = user.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (entities != null) {
                double time1 = Math.pow(distance, 2);
                double time2 = Math.abs(time1) * -1;
                double time3 = time2 + SMALL_FIRE_TIME;
                entity.setOnFireFor((float) time3);
                user.getItemCooldownManager().set(this, (int) ((SMALL_FIRE_TIME + EXTRA_COOLDOWN) * 20));
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        tooltip.add(this.currentAttack(stack).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.RED));
        tooltip.add(this.attack1().formatted(Formatting.WHITE));
        tooltip.add(this.attack2().formatted(Formatting.WHITE));
        tooltip.add(this.attack3().formatted(Formatting.WHITE));
        tooltip.add(this.attack3cont().formatted(Formatting.WHITE));
        tooltip.add(this.attack3cont2().formatted(Formatting.WHITE));
        tooltip.add(this.attack3small().formatted(Formatting.WHITE));
        tooltip.add(this.attack3contsmall().formatted(Formatting.WHITE));
        tooltip.add(this.attack3cont2small().formatted(Formatting.WHITE));
        tooltip.add(this.attack4().formatted(Formatting.WHITE));
        tooltip.add(this.attack5().formatted(Formatting.WHITE));
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
package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.CooldownProvider;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.FireStaffAttacks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

//TODO do what was done in this class for cooldowns in the others staffs / spell books
public class FireStaffItem extends StaffItem implements CooldownProvider {
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
    public boolean isItemBarVisible(ItemStack stack) {
        if (this instanceof CooldownProvider cp) {
            return cp.getVisibleCooldownKey(stack) != null;
        }
        return false;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        if (this instanceof CooldownProvider cp) {
            String key = cp.getVisibleCooldownKey(stack);
            if (key != null) {
                float progress = cp.getCooldownProgressInverse(stack, key);
                return Math.round(13 * progress);
            }
        }
        return 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xE5531D;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        FireStaffAttacks current = getElement(stack);
        FireStaffAttacks next = FireStaffAttacks.next(current);
        String cooldownKey = current.getCooldownKey();

        if (!player.isSneaking()) {
            switch (current) {
                case WEAK_FIREBALL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        setCooldown(player, stack, cooldownKey, 30, true);
                        shootFireball(world, player, 1);
                        return TypedActionResult.success(stack);
                    }
                }
                case STRONG_FIREBALL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        setCooldown(player, stack, cooldownKey, 60, true);
                        shootFireball(world, player, 5);
                        return TypedActionResult.success(stack);
                    }
                }
                case CONCENTRATED_FIRE_STORM -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        fireBurst(player, world, stack, 4, 8);
                        setCooldown(player, stack, cooldownKey, 80, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case FIRE_STORM -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        fireBurst(player, world, stack, 8, 16);
                        setCooldown(player, stack, cooldownKey, 100, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case STRENGTH -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
                        setCooldown(player, stack, cooldownKey, 400, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case RESISTANCE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 0));
                        setCooldown(player, stack, cooldownKey, 600, true);
                        return TypedActionResult.success(stack);
                    }
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

    private void fireBurst(LivingEntity entity, World world, ItemStack stack, int duration, double maxRadius) {
        Vec3d storedPos = entity.getPos();
        ((Ticker.TickerTarget) entity).add(Ticker.forSeconds((ticks) -> {
            if (world instanceof ServerWorld serverWorld) {
                double progress = ticks / (double) (duration * 20);
                double radius = maxRadius * progress;


                int count = (int) (maxRadius/64d*1280d);
                for (int i = 0; i < count; i++) {

                    double angle = (2 * Math.PI / count) * i;

                    double x = storedPos.getX() + Math.sin(angle) * radius;
                    double z = storedPos.getZ() + Math.cos(angle) * radius;

                    double yOffset = 0.2;
                    double y = storedPos.getY() + yOffset;

                    double xSpeed = Math.sin(angle) * 0.2;
                    double zSpeed = Math.cos(angle) * 0.2;

                    serverWorld.spawnParticles(ParticleTypes.FLAME, x, y, z, 0, xSpeed, 0.0, zSpeed, 0);
                }

                for (LivingEntity target : serverWorld.getEntitiesByClass(LivingEntity.class, entity.getBoundingBox().expand(maxRadius), e -> e.isAlive() && e != entity)) {
                    double dist = target.getPos().distanceTo(storedPos);

                    double stepSize = maxRadius / duration;
                    if (dist <= radius && dist > (radius - stepSize)) {
                        target.setOnFireFor((float) ((radius * -1) + maxRadius));
                        target.damage(entity.getDamageSources().inFire(), Math.min(8, (float) ((radius * -1) + maxRadius)));
                    }
                }
            }
        }, duration));
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
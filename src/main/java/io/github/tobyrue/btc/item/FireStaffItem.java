package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.CooldownProvider;
import io.github.tobyrue.btc.Ticker;
import io.github.tobyrue.btc.enums.FireStaffAttacks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FireStaffItem extends StaffItem {
    World world2 = null;

    public FireStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        FireStaffAttacks current = getElement(stack);
        if (current != FireStaffAttacks.ETERNAL_FIRE) {
            if (this instanceof CooldownProvider cp) {
                return cp.getVisibleCooldownKey(stack) != null;
            }
        } else {
            return nbt.containsUuid("TargetEternalFire");
        }
        return false;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        FireStaffAttacks current = getElement(stack);

        if (current != FireStaffAttacks.ETERNAL_FIRE) {
            if (this instanceof CooldownProvider cp) {
                String key = cp.getVisibleCooldownKey(stack);
                if (key != null) {
                    float progress = cp.getCooldownProgressInverse(stack, key);
                    return Math.round(13 * progress);
                }
            }
        } else {
            if (nbt.containsUuid("TargetEternalFire")) {
                if (nbt.contains("TargetEternalFireHealth") && nbt.contains("TargetEternalFireMaxHealth")) {
                    float health = nbt.getFloat("TargetEternalFireHealth");
                    float maxHealth = nbt.getFloat("TargetEternalFireMaxHealth");
                    return Math.round(13 * (health / maxHealth));
                } else {
                    return 0; // No health stored yet
                }
            }
            return 0; // No target
        }
        return 0;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xE5531D;
    }


    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (!world.isClient) {
            // Always tick cooldowns no matter where the item is (player or item entity)
            this.tickCooldowns(stack);

            // Eternal Fire tracking logic
            NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
            NbtCompound nbt = component.copyNbt();

            if (nbt.containsUuid("TargetEternalFire") && world instanceof ServerWorld serverWorld) {
                UUID targetId = nbt.getUuid("TargetEternalFire");
                Entity targetEntity = serverWorld.getEntity(targetId);

                if (targetEntity instanceof LivingEntity living) {
                    if (living.isAlive()) {
                        nbt.putFloat("TargetEternalFireHealth", living.getHealth());
                        nbt.putFloat("TargetEternalFireMaxHealth", living.getMaxHealth());
                        living.setOnFireFor(5);
                        living.setOnFire(true);
                        if (living.isTouchingWater() || !living.isOnFire()) {
                            living.damage(world.getDamageSources().onFire(), 2);
                        }
                    } else {
                        // Remove if dead
                        nbt.remove("TargetEternalFire");
                        nbt.remove("TargetEternalFireHealth");
                        nbt.remove("TargetEternalFireMaxHealth");
                    }
                } else {
                    // Remove if not found
                    nbt.remove("TargetEternalFire");
                    nbt.remove("TargetEternalFireHealth");
                    nbt.remove("TargetEternalFireMaxHealth");
                }

                stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        world2 = world;
        FireStaffAttacks current = getElement(stack);
        FireStaffAttacks next = FireStaffAttacks.next(current);
        String cooldownKey = current.getCooldownKey();



        if (!player.isSneaking()) {
            switch (current) {
                case WEAK_FIREBALL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        setCooldown(player, stack, cooldownKey, 30, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        shootFireball(world, player, 1);
                        return TypedActionResult.success(stack);
                    }
                }
                case STRONG_FIREBALL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        setCooldown(player, stack, cooldownKey, 60, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        shootFireball(world, player, 5);
                        return TypedActionResult.success(stack);
                    }
                }
                case CONCENTRATED_FIRE_STORM -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        fireBurst(player, world, stack, 2, 8);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 200, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case FIRE_STORM -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        fireBurst(player, world, stack, 4, 16);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 400, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case ETERNAL_FIRE -> {
                    Entity target = getEntityLookedAt(player, 24, 0.4D);
                    if (target != null) {
                        boolean applied = applyEternalFireToEntity(stack, target);
                        if (applied) {
                            player.incrementStat(Stats.USED.getOrCreateStat(this));
                            return TypedActionResult.success(stack);
                        }
                    }
                }
                case STRENGTH -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 140, 0));
                        setCooldown(player, stack, cooldownKey, 400, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case RESISTANCE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 0));
                        setCooldown(player, stack, cooldownKey, 600, true);
                        return TypedActionResult.success(stack);
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + current);
            }
        }
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.fire.set", Text.translatable("item.btc.spell.fire." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }
    private boolean applyEternalFireToEntity(ItemStack stack, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return false;
        if (livingEntity.isFireImmune()) return false;
        if (livingEntity instanceof WitchEntity) return false;

        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();

        if (nbt.containsUuid("TargetEternalFire")) return false; // already tracking a target

        // Store target's UUID and current health info
        nbt.putUuid("TargetEternalFire", livingEntity.getUuid());
        nbt.putFloat("TargetEternalFireHealth", livingEntity.getHealth());
        nbt.putFloat("TargetEternalFireMaxHealth", livingEntity.getMaxHealth());

        // Immediately set them on fire so it starts right away
        livingEntity.setOnFireFor(5);

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

        return true;
    }
    private static @Nullable Entity getEntityLookedAt(PlayerEntity player, double range, double aimmingForgivness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(aimmingForgivness); // slightly expanded hitbox
            Optional<Vec3d> optionalHit = entityBox.raycast(eyePos, reachVec);

            if (optionalHit.isPresent()) {
                double distanceSq = eyePos.squaredDistanceTo(optionalHit.get());
                if (distanceSq < closestDistanceSq) {
                    closestDistanceSq = distanceSq;
                    hitEntity = entity;
                }
            }
        }
        return hitEntity;
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
    private Entity getEntity(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        UUID name = nbt.getUuid("EternalFireTarget");
        if (stack.getHolder().getEntityWorld() instanceof ServerWorld serverWorld) {
            return serverWorld.getEntity(name);
        }
        return null;
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

}
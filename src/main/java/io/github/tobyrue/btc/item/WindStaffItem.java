package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.entity.custom.WindTornadoEntity;
import io.github.tobyrue.btc.enums.WindStaffAttacks;
import io.github.tobyrue.btc.regestries.ModDamageTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class WindStaffItem extends StaffItem {
    // Configurable pull range (in blocks)
    private static final double PULL_RADIUS = 25.0;
    private static final double SHOOT_RADIUS = 15.0;
    private static final double PULL_STRENGTH = 3.0;
    private static final double SHOOT_STRENGTH = 5.0;
    private static final int PROJECTILE_COUNT = 7; // Number of projectiles to shoot

    public WindStaffItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0xA0F4C5;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);

        WindStaffAttacks current = getElement(stack);
        WindStaffAttacks next = WindStaffAttacks.next(current);
        String cooldownKey = current.getCooldownKey();

        if (!player.isSneaking()) {
            switch (current) {
                case WIND_CHARGE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY() + 1.0, player.getZ());
                        Vec3d direction = player.getRotationVec(1.0f);
                        windCharge.setVelocity(direction.multiply(1.5));
                        world.spawnEntity(windCharge);
                        setCooldown(player, stack, cooldownKey, 20, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(stack);
                    }
                }
                case CLUSTER_WIND_CHARGE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        shootWindCharges(player, world);
                        setCooldown(player, stack, cooldownKey, 160, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(stack);
                    }
                }
                case TEMPESTS_CALL -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        pullMobsTowardsPlayer(world, player);
                        setCooldown(player, stack, cooldownKey, 80, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(stack);
                    }
                }
                case STORM_PUSH -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        shootMobsAway(player, world);
                        setCooldown(player, stack, cooldownKey, 100, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(stack);
                    }
                }
                case WIND_TORNADO -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        WindTornadoEntity windTornado = new WindTornadoEntity(player, world, player.getX(), player.getY(), player.getZ(), 320);
                        world.spawnEntity(windTornado);
                        setCooldown(player, stack, cooldownKey, 400, true);
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        return TypedActionResult.success(stack);
                    }
                }
            }
        }
        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.wind.set", Text.translatable("item.btc.spell.wind." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.fail(stack);
    }

    private void shootWindCharges(PlayerEntity player, World world) {
        Vec3d baseDirection = player.getRotationVec(1.0f).normalize(); // Get the player's facing direction
        double spreadFactor = 0.2; // Controls how much deviation there is from the forward direction

        for (int i = 0; i < PROJECTILE_COUNT; i++) {
            // Create a new WindChargeEntity
            WindChargeEntity windCharge = new WindChargeEntity(player, world, player.getX(), player.getY() + 1.0, player.getZ());

            // Apply random spread within a controlled cone
            double randomPitch = (Math.random() - 0.5) * spreadFactor;
            double randomYaw = (Math.random() - 0.5) * spreadFactor;

            // Calculate the scattered direction by modifying the player's original facing direction
            Vec3d scatterDirection = baseDirection.add(randomYaw, randomPitch, randomYaw).normalize();

            // Set the spawn position slightly in front of the player
            Vec3d spawnPosition = player.getPos().add(baseDirection.multiply(1.5)).add(0, 1, 0);
            windCharge.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            // Set the velocity of the wind charge
            windCharge.setVelocity(scatterDirection.multiply(2.0)); // Set speed of the wind charge
            world.spawnEntity(windCharge);
        }
    }
    private void pullMobsTowardsPlayer(World world, PlayerEntity player) {
        // Use the PULL_RADIUS variable to define the textRadius
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(PULL_RADIUS), entity -> entity != player);
        // Log the number of entities found
        // Pull all mobs towards the player
        for (LivingEntity entity : entities) {
            // Calculate the direction towards the player
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double dz = player.getZ() - entity.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double strength = PULL_STRENGTH;
            if (entities != null) {
                if (distance != 0) {
                    entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
                }
            }
        }
    }

    private void shootMobsAway(PlayerEntity player, World world) {
        List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(SHOOT_RADIUS), entity -> entity != player);

        // Shoot all mobs away from the player
        for (LivingEntity entity : entities) {
            double dx = entity.getX() - player.getX();
            double dy = entity.getY() - player.getY();
            double dz = entity.getZ() - player.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            // Apply velocity away from the player
            if (entities != null) {
                double strength = SHOOT_STRENGTH;
                if (distance != 0) {
                    entity.setVelocity(dx / distance * strength, dy / distance * strength, dz / distance * strength);
                }

                // Optionally, deal damage to the entity
                if (entity instanceof PlayerEntity) {
                    entity.damage(ModDamageTypes.of(world, ModDamageTypes.WIND_BURST), 5.0f);
                } else {
                    entity.damage(world.getDamageSources().flyIntoWall(), 5);
                }
            }
        }
    }
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext tooltipContext, List<Text> tooltip, TooltipType tooltipType) {
        super.appendTooltip(stack, tooltipContext, tooltip, tooltipType);
        WindStaffAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.wind." + current.asString())).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.AQUA));

        // Add custom tooltip text
    }
    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {
        return Text.literal("Attack Types:");

    }
    public MutableText attack1() {
        return Text.literal("Wind Charge");

    }
    public MutableText attack2() {
        return Text.literal("Wind Cluster Shot");
    }
    public MutableText attack3() {
        return Text.literal("Tempest's Call - Suck Entities Toward You");
    }
    public MutableText attack4() {
        return Text.literal("Storm Push - Blast Nearby Entities Away Dealing Damage");
    }

    private WindStaffAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (WindStaffAttacks attack : WindStaffAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return WindStaffAttacks.WIND_CHARGE;
    }

    private void setElement(ItemStack stack, WindStaffAttacks attack) {
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

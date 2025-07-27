package io.github.tobyrue.btc.item;

import io.github.tobyrue.btc.block.ModBlocks;
import io.github.tobyrue.btc.entity.custom.WaterBlastEntity;
import io.github.tobyrue.btc.enums.WaterStaffAttacks;
import io.github.tobyrue.btc.regestries.ModStatusEffects;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WaterStaffItem extends StaffItem {

    public WaterStaffItem(Settings settings) {
        super(settings);
    }


    @Override
    public int getItemBarColor(ItemStack stack) {
        return 0x1E90FF;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        WaterStaffAttacks current = getElement(stack);
        WaterStaffAttacks next = WaterStaffAttacks.next(current);
        String cooldownKey = current.getCooldownKey();

        if (!player.isSneaking()) {
            switch (current) {
                case WATER_BLAST -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                        Vec3d velocity = player.getRotationVec(1.0f).multiply(1.5f);
                        if (!world.isClient) {
                            WaterBlastEntity waterBlast = new WaterBlastEntity(player, world, player.getX(), player.getY() + 1.25, player.getZ(), velocity);
                            world.spawnEntity(waterBlast);
                        }
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 80, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case ICE_FREEZE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        if (freezeTargetArea(player, world) != null) {
                            // Method above does all the logic
                            player.incrementStat(Stats.USED.getOrCreateStat(this));
                            setCooldown(player, stack, cooldownKey, 320, true);
                        }
                        return TypedActionResult.success(stack);
                    }
                }
                case FROST_WALKER -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.FROST_WALKER, 500, 4));
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 960, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case DOLPHINS_GRACE -> {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 400, 1));
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 600, true);
                        return TypedActionResult.success(stack);
                    }
                }
                case CONDUIT_POWER ->  {
                    if (!isCooldownActive(stack, cooldownKey)) {
                        player.addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 400, 1));
                        player.incrementStat(Stats.USED.getOrCreateStat(this));
                        setCooldown(player, stack, cooldownKey, 600, true);
                        return TypedActionResult.success(stack);
                    }
                }
            }
        }

        if (!world.isClient && player.isSneaking()) {
            player.sendMessage(Text.translatable("item.btc.spell.water.set", Text.translatable("item.btc.spell.water." + next.asString())), true);
            setElement(stack, next);
            return TypedActionResult.success(stack);
        }

        return super.use(world, player, hand);
    }

    public static @Nullable Entity getEntityLookedAt(PlayerEntity player, double range, double aimingForgiveness) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F).normalize();
        Vec3d reachVec = eyePos.add(lookVec.multiply(range));

        // Create a box from the eye position to the reach vector
        Box searchBox = player.getBoundingBox().stretch(lookVec.multiply(range)).expand(1.0D, 1.0D, 1.0D);

        // Find the closest entity intersecting that line
        Entity hitEntity = null;
        double closestDistanceSq = range * range;

        for (Entity entity : player.getWorld().getOtherEntities(player, searchBox, e -> e.isAttackable() && e.canHit()) /*Replace isAttackable() and canHit() in the predicate with any condition you like (e.g., specific entity types or tags)*/) {
            Box entityBox = entity.getBoundingBox().expand(aimingForgiveness); // slightly expanded hitbox
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

    public Entity freezeTargetArea(PlayerEntity player, World world) {
        // Only run server-side
        if (!world.isClient) {
            Entity target = getEntityLookedAt(player, 16, 0.3D);
            // Iterate in a 3x3x3 cube around the target's block position
            BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            if (target instanceof LivingEntity) {
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 200, 4));

                BlockPos targetPos = target.getBlockPos();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -2; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            mutablePos.set(targetPos.getX() + x, targetPos.getY() + y + 1, targetPos.getZ() + z);

                            // Only replace air or water blocks
                            BlockState state = world.getBlockState(mutablePos);
                            if (state.isReplaceable() || state.getFluidState().isStill()) {
                                world.setBlockState(mutablePos, ModBlocks.MELTING_ICE.getDefaultState());
                            }
                        }
                    }
                }
                world.playSound(null, targetPos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return target;
            }
        }
        return null;
    }

    private WaterStaffAttacks getElement(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        String name = nbt.getString("Element");
        for (WaterStaffAttacks attack : WaterStaffAttacks.values()) {
            if (attack.asString().equals(name)) {
                return attack;
            }
        }
        return WaterStaffAttacks.WATER_BLAST;
    }

    private void setElement(ItemStack stack, WaterStaffAttacks attack) {
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
        WaterStaffAttacks current = getElement(stack);
        tooltip.add(Text.translatable("item.btc.spell.context.current", Text.translatable("item.btc.spell.water." + current.asString())).formatted(Formatting.BLUE));
        tooltip.add(this.attackTypes().formatted(Formatting.AQUA));
    }
    
    public MutableText currentAttack(ItemStack stack) {return Text.literal("Current Spell: " + getElement(stack));}
    public MutableText attackTypes() {return Text.literal("Attack Types:");}

}

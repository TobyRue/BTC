package io.github.tobyrue.btc;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayList;
import java.util.List;


public interface CooldownProvider {

    // Set a cooldown with option to make it visible
    default void setCooldown(LivingEntity entity, ItemStack stack, String key, int durationTicks, boolean visible) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();
        NbtCompound cooldowns = nbt.getCompound("Cooldowns");

        // Remove visibility from other cooldowns if this one will be visible
        if (visible) {
            for (String existingKey : cooldowns.getKeys()) {
                NbtCompound entry = cooldowns.getCompound(existingKey);
                entry.remove("visible");
                cooldowns.put(existingKey, entry);
            }
        }

        // Create cooldown entry
        NbtCompound entry = new NbtCompound();
        entry.putInt("ticks", 0);
        entry.putInt("max", durationTicks);
        if (visible) entry.putBoolean("visible", true);
        cooldowns.put(key, entry);

        nbt.put("Cooldowns", cooldowns);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }
    default void tickCooldowns(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();

        if (nbt.contains("Cooldowns")) {
            NbtCompound cooldowns = nbt.getCompound("Cooldowns");
            List<String> toRemove = new ArrayList<>();

            for (String key : cooldowns.getKeys()) {
                NbtCompound entry = cooldowns.getCompound(key);
                int ticks = entry.getInt("ticks") + 1;
                int max = entry.getInt("max");

                if (ticks >= max) {
                    toRemove.add(key);
                } else {
                    entry.putInt("ticks", ticks);
                    cooldowns.put(key, entry);
                }
            }

            // Remove finished cooldowns
            for (String key : toRemove) {
                cooldowns.remove(key);
            }

            // Clean up empty cooldowns object
            if (cooldowns.getKeys().isEmpty()) {
                nbt.remove("Cooldowns");
            } else {
                nbt.put("Cooldowns", cooldowns);
            }

            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }
    default void resetAllCooldowns(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = component.copyNbt();

        if (nbt.contains("Cooldowns")) {
            NbtCompound cooldowns = nbt.getCompound("Cooldowns");

            for (String key : cooldowns.getKeys()) {
                NbtCompound entry = cooldowns.getCompound(key);
                entry.putInt("ticks", 0);  // Reset 'ticks' to 0
                cooldowns.put(key, entry);
            }

            nbt.put("Cooldowns", cooldowns);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }

    // Check if a cooldown is active
    default boolean isCooldownActive(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        return cooldowns.contains(key);
    }

    // Get progress (0.0 to 1.0) for a specific cooldown
    default float getCooldownProgress(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        if (cooldowns.contains(key)) {
            NbtCompound entry = cooldowns.getCompound(key);
            int ticks = entry.getInt("ticks");
            int max = entry.getInt("max");
            return max > 0 ? (float) ticks / (float) max : 0f;
        }
        return 0f;
    }
    default float getCooldownProgressInverse(ItemStack stack, String key) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        if (cooldowns.contains(key)) {
            NbtCompound entry = cooldowns.getCompound(key);
            int ticks = entry.getInt("ticks");
            int max = entry.getInt("max");
            return max > 0 ? 1.0f - ((float) ticks / (float) max) : 0f;
        }
        return 0f;
    }
    // Get the key of the currently visible cooldown, if any
    default String getVisibleCooldownKey(ItemStack stack) {
        NbtComponent component = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound cooldowns = component.copyNbt().getCompound("Cooldowns");
        for (String key : cooldowns.getKeys()) {
            if (cooldowns.getCompound(key).getBoolean("visible")) {
                return key;
            }
        }
        return null;
    }
}
